package org.iotcity.iot.framework.core.util.task;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.iotcity.iot.framework.core.FrameworkCore;

/**
 * Use this task group executor to perform multi-threaded tasks.
 * @author ardon
 * @date 2021-06-30
 */
public class TaskGroupExecutor {

	/**
	 * The task handler to execute the tasks (not null).
	 */
	private final TaskHandler handler;
	/**
	 * The task group data context waiting to be executed using multithreading (not null).
	 */
	private final TaskGroupDataContext<?> context;
	/**
	 * The maximum number of threads used to perform the task (greater than 0).
	 */
	private final int threads;
	/**
	 * The total timeout in milliseconds of the task group execution.
	 */
	private final long groupTimeout;
	/**
	 * The number of tasks executed.
	 */
	private final AtomicInteger executed = new AtomicInteger();
	/**
	 * The number of tasks executed successfully.
	 */
	private final AtomicInteger successes = new AtomicInteger();
	/**
	 * Thread lock for executing tasks.
	 */
	private final Object threadLock = new Object();
	/**
	 * The number of threads currently executing the tasks.
	 */
	private int threadsCount = 0;
	/**
	 * The number of tasks submitted for execution.
	 */
	private int submittedCount = 0;
	/**
	 * The task waiting for idle threads to execute.
	 */
	private PriorityRunnable waitingTask = null;
	/**
	 * The group locker for execution status.
	 */
	private Object groupLock = new Object();
	/**
	 * Determines whether the execution has started.
	 */
	private boolean started = false;
	/**
	 * Determines whether the execution has stopped.
	 */
	private boolean stopped = false;
	/**
	 * The timeout task sequence number.
	 */
	private long timeoutID;

	/**
	 * Constructor for task group executor.
	 * @param handler The task handler to execute the tasks (required, can not be null).
	 * @param context The task group data context waiting to be executed using multithreading (required, can not be null).
	 * @param threads The maximum number of threads used to perform the task (required, must be greater than 0).
	 * @param expandCorePoolSize Whether to automatically expand the size of core threads in the thread pool according to the specified number of threads.
	 * @param groupTimeout The total timeout in milliseconds of the task group execution (set to 0 when execution without timeout; when timeout value greater than 0, whether the tasks have been executed or not, it will exit the task scheduling after this timeout).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "handler" and "context" is null or the parameter "threads" less than 1.
	 */
	public TaskGroupExecutor(TaskHandler handler, TaskGroupDataContext<?> context, int threads, boolean expandCorePoolSize, long groupTimeout) throws IllegalArgumentException {
		if (handler == null || context == null || threads < 1) throw new IllegalArgumentException("Parameter handler and context can not be null, and the parameter threads must be greater than 0!");
		this.handler = handler;
		this.context = context;
		this.threads = threads;
		this.groupTimeout = groupTimeout;
		// Determines whether to expand the core size.
		if (expandCorePoolSize) {
			// Get the executor.
			ThreadPoolExecutor executor = handler.getThreadPoolExecutor();
			synchronized (executor) {
				// Get the pool size.
				int corePoolSize = executor.getCorePoolSize();
				int maxPoolSize = executor.getMaximumPoolSize();
				// Check thread count.
				if (threads > corePoolSize) {
					// Get the new core pool size.
					int size;
					if (threads <= maxPoolSize) {
						size = threads;
					} else {
						size = maxPoolSize;
					}
					// Set new core pool size.
					if (size > corePoolSize) executor.setCorePoolSize(size);
				}
			}
		}
	}

	/**
	 * Gets the total timeout in milliseconds of the task group execution.
	 */
	public long getGroupTimeout() {
		return groupTimeout;
	}

	/**
	 * Gets the number of tasks submitted for execution.
	 */
	public int getSubmitted() {
		return submittedCount;
	}

	/**
	 * Gets the number of tasks executed.
	 */
	public int getExecuted() {
		return executed.get();
	}

	/**
	 * Gets the number of tasks executed successfully.
	 */
	public int getSuccesses() {
		return successes.get();
	}

	/**
	 * Gets the number of tasks waiting to execute.
	 */
	public int getRemains() {
		return context.getSize() - submittedCount;
	}

	/**
	 * Starts using multithreading to execute tasks (this method is only allowed to be called once).
	 * @return The number of tasks executed successfully (if repeated method calling or abort method has been called before calling this method, return - 1).
	 */
	public int execute() {

		// Check started status.
		if (started || stopped) return -1;
		// Get a lock to check started status again.
		synchronized (groupLock) {
			if (started || stopped) return -1;
			started = true;
		}

		// Check the total timeout of the execution task group
		if (groupTimeout > 0) {
			// Add stop method task.
			timeoutID = TaskHandler.getDefaultHandler().addDelayTask(new PriorityRunnable(0) {

				@Override
				public void run() {
					// Call abort.
					abort();
					// Gets the data type.
					Type[] types = ((ParameterizedType) context.getClass().getGenericSuperclass()).getActualTypeArguments();
					Class<?> dataType = (Class<?>) types[0];
					// Log a message.
					FrameworkCore.getLogger().warn(FrameworkCore.getLocale().text("core.util.task.group.timeout", TaskGroupExecutor.class.getSimpleName(), dataType.getName(), groupTimeout));
				}

			}, groupTimeout);
		}

		// Create the task callback object.
		TaskGroupTaskCallback callback = new TaskGroupTaskCallback() {

			@Override
			public void onExecuted(boolean success) {
				// Increase the number of tasks executed.
				executed.incrementAndGet();
				// Increase the number of successes.
				if (success) successes.incrementAndGet();
				// Get a lock to notify loop thread.
				synchronized (threadLock) {
					// Decrement the number of threads used.
					threadsCount--;
					// Notify the lock.
					threadLock.notify();
				}
			}

		};

		// Traversal group data.
		while (!stopped) {

			// Check current task.
			if (waitingTask != null) {

				// Wait for idle threads.
				synchronized (threadLock) {
					try {
						threadLock.wait(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// Check the stop status.
				if (stopped) break;
				// Try to submit the waiting task.
				if (handler.run(waitingTask)) {
					// Get a lock to increase threads count.
					synchronized (threadLock) {
						// Increase the number of threads used.
						threadsCount++;
					}
					// Increase the number of tasks submitted for execution.
					submittedCount++;
					// Reset the waiting task.
					waitingTask = null;
				}

			} else {

				// Check next task.
				if (!context.hasNext()) break;

				// Get a lock to check threads.
				synchronized (threadLock) {
					// Check the number of threads.
					if (threadsCount >= threads) {
						try {
							// Wait for task execution to complete.
							threadLock.wait();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				// Check the stop status.
				if (stopped) break;

				// Get next task.
				PriorityRunnable task = context.nextTask(callback);
				// Check whether the task is valid.
				if (task == null) break;

				// Submit a task to thread pool executor.
				if (handler.run(task)) {
					synchronized (threadLock) {
						// Increase the number of threads used.
						threadsCount++;
					}
					// Increase the number of tasks submitted for execution.
					submittedCount++;
				} else {
					// Set as waiting task.
					waitingTask = task;
				}

			}

		}

		// Remove timeout task.
		if (timeoutID > 0) TaskHandler.getDefaultHandler().remove(timeoutID);
		// Check threads status.
		if (threadsCount > 0) {
			// Get a lock to check threads count.
			synchronized (threadLock) {
				// Wait for task execution to complete.
				while (threadsCount > 0) {
					try {
						threadLock.wait();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		// Return the number of successes.
		return successes.get();
	}

	/**
	 * Terminate execution of current execution (this method allows only one call).
	 */
	public void abort() {
		// Check stopped status.
		if (stopped) return;
		// Get a lock to check stopped status again.
		synchronized (groupLock) {
			if (stopped) return;
			stopped = true;
			// Get a lock to notify loop thread.
			synchronized (threadLock) {
				// Notify the lock.
				threadLock.notify();
			}
		}
	}

}
