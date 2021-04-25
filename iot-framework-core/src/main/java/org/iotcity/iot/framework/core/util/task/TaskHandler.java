package org.iotcity.iot.framework.core.util.task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.iotcity.iot.framework.core.beans.PostmanRunnable;
import org.iotcity.iot.framework.core.beans.ThreadLocalPostman;
import org.iotcity.iot.framework.core.beans.ThreadPoolSupport;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.core.util.helper.SystemHelper;

/**
 * Task handler object supporting thread pool to execute tasks and timer tasks.
 * @author Ardon
 */
public final class TaskHandler implements ThreadPoolSupport {

	// --------------------------- Public fields ----------------------------

	/**
	 * Default global task handler instance object.<br/>
	 * Use parameters below:<br/>
	 * <b>corePoolSize: 0, maximumPoolSize: 10, keepAliveTime: 60s, capacity: 0</b>
	 */
	public static final TaskHandler instance = new TaskHandler("Default");

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
	 * Use parameters below:<br/>
	 * <b>corePoolSize: 0, maximumPoolSize: 10, keepAliveTime: 60s, capacity: 0</b>
	 */
	public TaskHandler() {
		this(null, 0, 10, 60, 0);
	}

	/**
	 * Constructor for task handler.<br/>
	 * Use parameters below:<br/>
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
		this.name = StringHelper.isEmpty(name) ? "TaskHandler" : "TaskHandler-" + name;
		// Create task queue and timer loop thread
		queue = new TimerTaskQueue(this.name);
		thread = new TimerTaskThread(this.name, this, queue);
		// Create thread pool
		BlockingQueue<Runnable> blockingQueue = capacity <= 0 ? new SynchronousQueue<Runnable>() : new ArrayBlockingQueue<Runnable>(capacity);
		pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, blockingQueue, new ThreadPoolExecutor.AbortPolicy());
		// Logs message
		thread.logger.info(thread.locale.text("core.util.task.start", this.name, corePoolSize, maximumPoolSize, keepAliveTime, capacity));
	}

	// --------------------------- Private methods ----------------------------

	/**
	 * Gets the delay time for every milliseconds.
	 * @param ms The milliseconds.
	 * @return long Delay time.
	 */
	private static final long getDelayMilliseconds(long ms) {
		long now = System.currentTimeMillis();
		return ms - (now % ms);
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets the task handler name.
	 * @return Task handler name.
	 */
	public String getName() {
		return name;
	}

	@Override
	public boolean run(Runnable runnable, ThreadLocalPostman... postmen) {
		if (runnable == null || destroyed) return false;
		// Check for postman objects
		if (postmen != null && postmen.length > 0) runnable = new PostmanRunnable(runnable, postmen);
		try {
			synchronized (pool) {
				if (destroyed) return false;
				pool.execute(runnable);
			}
			return true;
		} catch (Exception e) {
			// Logs error
			// thread.logger.warn(thread.locale.text("core.util.task.pool.err", this.thread.getName(), e.getMessage()));
			return false;
		}
	}

	/**
	 * Remove all timer tasks and release the current handler resources.
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
			thread.logger.error(thread.locale.text("core.util.task.shutdown.err", name, e.getMessage()), e);
		}
		// Logs destroy message
		thread.logger.info(thread.locale.text("core.util.task.destory", name));
	}

	// --------------------------- Delay methods ----------------------------

	/**
	 * Get task delay time from current system time to every N seconds-unit.<br/>
	 * <b>For example, when set seconds to 10:</b><br/>
	 * 1) If the current time is "2021-05-01 <b>10:00:04</b>", the task will start at "2021-05-01 <b>10:00:10</b>".<br/>
	 * 2) If the current time is "2021-05-01 <b>10:00:16</b>", the task will start at "2021-05-01 <b>10:00:20</b>".<br/>
	 * <b>When set seconds to 0 or 60:</b><br/>
	 * The task will start at "2021-05-01 <b>10:00:00</b>", "2021-05-01 <b>10:01:00</b>", "2021-05-01 <b>10:02:00</b>"... etc.
	 * @param seconds The task starts when the system time reaches the multiple of the current set seconds (60 seconds by default).
	 * @return The delay time in milliseconds.
	 */
	public long getDelayForEverySeconds(long seconds) {
		if (seconds <= 0) seconds = 60;
		return getDelayMilliseconds(seconds * SystemHelper.SECOND_MS);
	}

	/**
	 * Get task delay time from current system time to every N minutes-unit.<br/>
	 * <b>For example, when set minutes to 10:</b><br/>
	 * 1) If the current time is "2021-05-01 <b>10:03:04</b>", the task will start at "2021-05-01 <b>10:10:00</b>".<br/>
	 * 2) If the current time is "2021-05-01 <b>10:30:16</b>", the task will start at "2021-05-01 <b>10:40:00</b>".<br/>
	 * <b>When set minutes to 0 or 60:</b><br/>
	 * The task will start at "2021-05-01 <b>10:00:00</b>", "2021-05-01 <b>11:00:00</b>", "2021-05-01 <b>12:00:00</b>"... etc.
	 * @param minutes The task starts when the system time reaches the multiple of the current set minutes (60 minutes by default).
	 * @return The delay time in milliseconds.
	 */
	public long getDelayForEveryMinutes(long minutes) {
		if (minutes <= 0) minutes = 60;
		return getDelayMilliseconds(minutes * SystemHelper.MINUTE_MS);
	}

	/**
	 * Get task delay time from current system time to every N hours-unit.<br/>
	 * <b>For example, when set hours to 2:</b><br/>
	 * 1) If the current time is "2021-05-01 <b>10:00:04</b>", the task will start at "2021-05-01 <b>12:00:00</b>".<br/>
	 * 2) If the current time is "2021-05-01 <b>11:30:16</b>", the task will start at "2021-05-01 <b>13:00:00</b>".<br/>
	 * <b>When set hours to 0 or 24:</b><br/>
	 * The task will start at "<b>2021-05-01</b> 00:00:00", "<b>2021-05-02</b> 00:00:00", "<b>2021-05-03</b> 00:00:00"... etc.
	 * @param hours The task starts when the system time reaches the multiple of the current set hours (24 hours by default).
	 * @return The delay time in milliseconds.
	 */
	public long getDelayForEveryHours(long hours) {
		if (hours <= 0) hours = 24;
		return getDelayMilliseconds(hours * SystemHelper.HOUR_MS);
	}

	// --------------------------- Timer task methods ----------------------------

	/**
	 * Add a task to be executed after the specified delay time, the task will be executed only once.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(Runnable task, long delay) {
		return this.add(null, task, delay, -1, 1);
	}

	/**
	 * Add a task to be executed after the specified delay time, and then execute according to each specified period.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param period Time in milliseconds between successive task executions (greater than 0).
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(Runnable task, long delay, long period) {
		return this.add(null, task, delay, period, -1);
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
		return this.add(null, task, delay, period, executions);
	}

	/**
	 * Add a task to be executed after the specified delay time, the task will be executed only once.
	 * @param name Task name, will be used for logging.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(String name, Runnable task, long delay) {
		return this.add(name, task, delay, -1, 1);
	}

	/**
	 * Add a task to be executed after the specified delay time, and then execute according to each specified period.
	 * @param name Task name, will be used for logging.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param period Time in milliseconds between successive task executions (greater than 0).
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(String name, Runnable task, long delay, long period) {
		return this.add(name, task, delay, period, -1);
	}

	/**
	 * Add a task to be executed after the specified delay time, and then execute according to each specified period.<br/>
	 * The maximum number of times the task runs does not exceed the number of executions.
	 * @param name Task name, will be used for logging.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param period Time in milliseconds between successive task executions (greater than 0).
	 * @param executions Maximum number of tasks executed (greater than 0).
	 * @return long Returns a task ID (sequence number), and -1 if it fails.
	 */
	public long add(String name, Runnable task, long delay, long period, long executions) {
		// Parameter verification (-1 means no restriction)
		if (destroyed || task == null || delay < 0 || period == 0 || period < -1 || executions == 0 || executions < -1) return -1;
		// Start timer thread
		thread.startLoop();
		// Add a task to queue
		return queue.add(name, task, delay, period, executions);
	}

	/**
	 * Gets the size of tasks in handler.
	 * @return Task size.
	 */
	public long size() {
		return queue.size();
	}

	/**
	 * Gets the task ID of the last added task.
	 * @return The task ID.
	 */
	public long getLastTaskID() {
		return queue.getLastTaskID();
	}

	/**
	 * Gets a task runnable object from handler, returns null when it does not exist.
	 * @param taskID The timer task sequence number returned when adding.
	 * @return The task runnable object, returns null when it does not exist.
	 */
	public Runnable getTask(long taskID) {
		TimerTask task = queue.get(taskID);
		return task == null ? null : task.getRunner();
	}

	/**
	 * Gets a task status data from handler, returns null when it does not exist.
	 * @param taskID The timer task sequence number returned when adding.
	 * @return Timer task status data, returns null when it does not exist.
	 */
	public TimerTaskStatus getTaskStatus(long taskID) {
		TimerTask task = queue.get(taskID);
		return task == null ? null : task.getStatus();
	}

	/**
	 * Gets timer task statistic data of all tasks (the returned data is not null).
	 * @return Timer task statistic data.
	 */
	public TimerTaskStatistic getTaskStatistic() {
		return queue.getStatistic();
	}

	/**
	 * Output task statistic message to logging.
	 */
	public void outputStatistic() {
		thread.outputStatistic();
	}

	/**
	 * Get the status data of all tasks (the returned data is not null).
	 * @return Tasks status data array.
	 */
	public TimerTaskStatus[] getTaskStatus() {
		return queue.getTaskStatus();
	}

	/**
	 * Gets the busiest timer task status data array (the returned data is not null).
	 * @param amount Maximum number of data returned.
	 * @return Tasks status data array.
	 */
	public TimerTaskStatus[] getBusyTaskStatus(int amount) {
		return queue.getBusyTaskStatus(amount);
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

}
