package org.iotcity.iot.framework.core.util.task;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Ardon
 */
public class TaskHandler {

	// --------------------------- Private fields ----------------------------

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
	 * @param capacity The capacity of blocking queue to cache tasks when reaches the maximum of threads in the pool.
	 */
	public TaskHandler(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, int capacity) {
		// Create task queue and timer loop thread
		this.queue = new TimerTaskQueue();
		this.thread = new TimerTaskThread(name, this, this.queue);
		// Create thread pool
		BlockingQueue<Runnable> blockingQueue = capacity <= 0 ? new SynchronousQueue<Runnable>() : new ArrayBlockingQueue<Runnable>(capacity);
		this.pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, blockingQueue, new ThreadPoolExecutor.AbortPolicy());
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Use the thread pool to execute tasks immediately.
	 * @param task Task to be execute.
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
			System.err.println("Failed to execute task using thread pool in task handler: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Add a task to be executed at a specified time, the task will be executed only once.
	 * @param task Task to be execute.
	 * @param time Time at which task is to be executed.
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(Runnable task, Date time) {
		return this.add(task, time, -1, 1);
	}

	/**
	 * Add a task to be executed at the start time, and then execute according to each specified period.
	 * @param task Task to be execute.
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
	 * @param task Task to be execute.
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
	 * @param task Task to be execute.
	 * @param delay Delay in milliseconds before task is to be executed.
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(Runnable task, long delay) {
		return this.add(task, delay, -1, 1);
	}

	/**
	 * Add a task to be executed after the specified delay time, and then execute according to each specified period.
	 * @param task Task to be execute.
	 * @param delay Delay in milliseconds before task is to be executed.
	 * @param period Time in milliseconds between successive task executions (greater than 0).
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(Runnable task, long delay, long period) {
		return this.add(task, delay, period, -1);
	}

	/**
	 * Add a task to be executed after the specified delay time, and then execute according to each specified period.<br/>
	 * The maximum number of times the task runs does not exceed the number of executions.
	 * @param task Task to be execute.
	 * @param delay Delay in milliseconds before task is to be executed.
	 * @param period Time in milliseconds between successive task executions (greater than 0).
	 * @param executions Maximum number of tasks executed (greater than 0).
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(Runnable task, long delay, long period, long executions) {
		// Parameter verification (-1 means no restriction)
		if (destroyed || task == null || period == 0 || period < -1 || executions == 0 || executions < -1) return -1;
		// Start timer thread
		this.thread.startLoop();
		// Add a task to queue
		return this.queue.add(new TimerTask(task, delay, period, executions));
	}

	/**
	 * Determine whether the timer task exists.
	 * @param taskID The timer task sequence number returned when adding.
	 * @return boolean whether the task exists.
	 */
	public boolean contains(long taskID) {
		return this.queue.contains(taskID);
	}

	/**
	 * Remove a timer task from task queue of handler.
	 * @param taskID The timer task sequence number returned when adding.
	 * @return The task that has been removed, returns null when it does not exist.
	 */
	public Runnable remove(long taskID) {
		return this.queue.remove(taskID);
	}

	/**
	 * Remove all timer tasks
	 */
	public void clear() {
		this.queue.clear();
	}

	/**
	 * Pause all timer tasks
	 */
	public void pause() {
		this.thread.pauseLoop();
	}

	/**
	 * Resume all timer tasks
	 */
	public void resume() {
		this.thread.resumeLoop();
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
		// Stop thread loop
		this.thread.stopLoop();
		// Clear all tasks
		this.queue.clear();
		try {
			// Shutdown thread pool
			this.pool.shutdown();
		} catch (Exception e) {
			System.err.println("Shutdown thread pool in task handler error: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
