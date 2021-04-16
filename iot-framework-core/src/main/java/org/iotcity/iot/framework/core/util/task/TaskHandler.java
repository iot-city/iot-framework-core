package org.iotcity.iot.framework.core.util.task;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Task handler objects supporting thread pool to execute tasks and timer tasks.
 * @author Ardon
 */
public final class TaskHandler {

	// --------------------------- Private fields ----------------------------

	/**
	 * The task handler name.
	 */
	private final String name;
	/**
	 * The timer task queue
	 */
	private final TimerTaskQueue queue;
	/**
	 * The timer task loop thread
	 */
	private final TimerTaskThread thread;
	/**
	 * Thread pool for executing tasks
	 */
	private final ThreadPoolExecutor pool;
	/**
	 * Whether has been destroyed
	 */
	private boolean destroyed = false;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for task handler.<br/>
	 * It will use parameters below:<br/>
	 * <b>corePoolSize: 0, maximumPoolSize: 10, keepAliveTime: 60s, capacity: 0</b>
	 */
	public TaskHandler() {
		this(null, 0, 10, 60, 0);
	}

	/**
	 * Constructor for task handler.<br/>
	 * It will use parameters below:<br/>
	 * <b>corePoolSize: 0, maximumPoolSize: 10, keepAliveTime: 60s, capacity: 0</b>
	 * @param name The handler name used for the loop thread.
	 */
	public TaskHandler(String name) {
		this(name, 0, 10, 60, 0);
	}

	/**
	 * Constructor for task handler.
	 * @param name The handler name used for the loop thread.
	 * @param corePoolSize The number of threads to keep in the pool.
	 * @param maximumPoolSize The maximum number of threads to allow in the pool.
	 * @param keepAliveTime The maximum seconds that excess idle threads will wait for new tasks before terminating.
	 * @param capacity The capacity of blocking queue to cache tasks when reaches the maximum of threads in the pool (0 by default).
	 */
	public TaskHandler(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, int capacity) {
		// Set handler name
		this.name = StringHelper.isEmpty(name) ? "TaskHandler" : name;
		// Create task queue and timer loop thread
		queue = new TimerTaskQueue(this.name);
		thread = new TimerTaskThread(this.name, this, queue);
		// Create thread pool
		BlockingQueue<Runnable> blockingQueue = capacity <= 0 ? new SynchronousQueue<Runnable>() : new ArrayBlockingQueue<Runnable>(capacity);
		pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, blockingQueue, new ThreadPoolExecutor.AbortPolicy());
		// Logs message
		thread.logger.info(thread.locale.text("core.util.task.start", this.name, corePoolSize, maximumPoolSize, keepAliveTime, capacity));
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets the task handler name.
	 * @return Task handler name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Use the thread pool to execute tasks immediately.
	 * @param task Task to be execute.
	 * @return If the task cannot be submitted for execution, it returns false; otherwise, it returns true.
	 */
	public boolean run(Runnable task) {
		if (task == null || destroyed) return false;
		try {
			synchronized (pool) {
				if (destroyed) return false;
				pool.execute(task);
			}
			return true;
		} catch (Exception e) {
			// Logs error
			thread.logger.warn(thread.locale.text("core.util.task.pool.err", this.thread.getName(), e.getMessage()));
			return false;
		}
	}

	/**
	 * Add a task to be executed at a specified time, the task will be executed only once.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param time Time at which task is to be executed.
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(Runnable task, Date time) {
		return this.add(task, time, -1, 1);
	}

	/**
	 * Add a task to be executed at the start time, and then execute according to each specified period.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param startTime Start time at which task is to be executed.
	 * @param period Time in milliseconds between successive task executions (greater than 0).
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(Runnable task, Date startTime, long period) {
		return this.add(task, startTime, period, -1);
	}

	/**
	 * Add a task to be executed at the start time, and then execute according to each specified period.<br/>
	 * The maximum number of times the task runs does not exceed the number of executions.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param startTime Start time at which task is to be executed.
	 * @param period Time in milliseconds between successive task executions (greater than 0).
	 * @param executions Maximum number of tasks executed (greater than 0).
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(Runnable task, Date startTime, long period, long executions) {
		if (task == null || startTime == null || period == 0 || executions == 0) return -1;
		long time = startTime.getTime();
		long now = System.currentTimeMillis();
		long delay;
		if (time < now) {
			if (period < 0) return -1;
			if (executions > 0) {
				long exec = (now - time) / period;
				if (exec >= executions) return -1;
			}
			delay = period - ((now - time) % period);
		} else {
			delay = time - now;
		}
		return this.add(task, delay, period, executions);
	}

	/**
	 * Add a task to be executed after the specified delay time, the task will be executed only once.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(Runnable task, long delay) {
		return this.add(task, delay, -1, 1);
	}

	/**
	 * Add a task to be executed after the specified delay time, and then execute according to each specified period.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param period Time in milliseconds between successive task executions (greater than 0).
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(Runnable task, long delay, long period) {
		return this.add(task, delay, period, -1);
	}

	/**
	 * Add a task to be executed after the specified delay time, and then execute according to each specified period.<br/>
	 * The maximum number of times the task runs does not exceed the number of executions.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param period Time in milliseconds between successive task executions (greater than 0).
	 * @param executions Maximum number of tasks executed (greater than 0).
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(Runnable task, long delay, long period, long executions) {
		// Parameter verification (-1 means no restriction)
		if (destroyed || task == null || delay < 0 || period == 0 || period < -1 || executions == 0 || executions < -1) return -1;
		// Start timer thread
		thread.startLoop();
		// Add a task to queue
		return queue.add(task, delay, period, executions);
	}

	/**
	 * Determine whether the timer task exists.
	 * @param taskID The timer task sequence number returned when adding.
	 * @return boolean whether the task exists.
	 */
	public boolean contains(long taskID) {
		return queue.contains(taskID);
	}

	/**
	 * Remove a timer task from task queue of handler.
	 * @param taskID The timer task sequence number returned when adding.
	 * @return The task that has been removed, returns null when it does not exist.
	 */
	public Runnable remove(long taskID) {
		return queue.remove(taskID);
	}

	/**
	 * Remove all timer tasks
	 */
	public void clear() {
		queue.clear();
	}

	/**
	 * Remove all timer tasks and release the current handler resources
	 */
	public void destroy() {
		// Ensure that destroyed only once
		if (destroyed) return;
		synchronized (pool) {
			if (destroyed) return;
			destroyed = true;
		}
		// Stop thread loop, the queue will be cleared
		thread.stopLoop();
		try {
			// Shutdown thread pool
			pool.shutdown();
		} catch (Exception e) {
			// Logs error
			thread.logger.error(thread.locale.text("core.util.task.shutdown.err", this.thread.getName(), e.getMessage()), e);
		}
		// Logs destroy message
		thread.logger.info(thread.locale.text("core.util.task.destory", this.thread.getName()));
	}

}
