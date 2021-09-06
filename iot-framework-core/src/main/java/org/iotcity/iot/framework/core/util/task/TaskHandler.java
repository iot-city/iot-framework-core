package org.iotcity.iot.framework.core.util.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.iotcity.iot.framework.ConfigureHandler;
import org.iotcity.iot.framework.core.beans.ThreadLocalPostman;
import org.iotcity.iot.framework.core.beans.ThreadPoolSupport;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;
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
	 * <b>corePoolSize: 8, maximumPoolSize: 8, keepAliveTime: 60s, queueCapacity: 1000</b>
	 */
	private static TaskHandler instance = null;
	/**
	 * The instance lock.
	 */
	private static final Object instanceLock = new Object();

	// --------------------------- Private fields ----------------------------

	/**
	 * The task handler name.
	 */
	private final String name;
	/**
	 * The timer task queue.
	 */
	private final TimerTaskQueue queue;
	/**
	 * The timer task loop thread.
	 */
	private final TimerTaskThread thread;
	/**
	 * The thread pool executor for executing tasks (not null).
	 */
	private final ThreadPoolExecutor executor;
	/**
	 * The thread pool execution lock.
	 */
	private final Object executionLock = new Object();
	/**
	 * Whether has been destroyed.
	 */
	private boolean destroyed = false;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for task handler.<br/>
	 * Use the thread pool executor parameters below:<br/>
	 * <b>corePoolSize: 8, maximumPoolSize: 8, keepAliveTime: 60s, queueCapacity: 1000, queueClass: {@link PriorityLimitedBlockingQueue }.</b>
	 */
	public TaskHandler() {
		this(null, 8, 8, 60, 1000);
	}

	/**
	 * Constructor for task handler.<br/>
	 * Use the thread pool executor parameters below:<br/>
	 * <b>corePoolSize: 8, maximumPoolSize: 8, keepAliveTime: 60s, queueCapacity: 1000, queueClass: {@link PriorityLimitedBlockingQueue }.</b>
	 * @param name The handler name used for the loop thread.
	 */
	public TaskHandler(String name) {
		this(name, 8, 8, 60, 1000);
	}

	/**
	 * Constructor for task handler.<br/>
	 * <b>NOTE:</b><br/>
	 * When the <b>corePoolSize</b> equals to the <b>maximumPoolSize</b> and the <b>keepAliveTime</b> greater than 0, <br/>
	 * the thread pool executor with allow the core threads timeout automatically.
	 * @param name The handler name used for the loop thread.
	 * @param corePoolSize The number of threads to keep in the pool.
	 * @param maximumPoolSize The maximum number of threads to allow in the pool.
	 * @param keepAliveTime The maximum seconds that excess idle threads will wait for new tasks before terminating.
	 * @param queueCapacity The capacity of blocking queue to cache tasks in thread pool executor (when set to 0, the synchronous queue {@link SynchronousQueue } is used; when set to greater than 0, the bounded priority blocking queue {@link PriorityLimitedBlockingQueue } is used; when set to less than 0, the unbounded priority blocking queue {@link PriorityBlockingQueue } is used).
	 */
	public TaskHandler(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, int queueCapacity) {
		this(name, createExecutor(name, corePoolSize, maximumPoolSize, keepAliveTime, queueCapacity), queueCapacity);
	}

	/**
	 * Constructor for task handler.
	 * @param name The handler name used for the loop thread.
	 * @param executor The thread pool executor for executing tasks (required, can not be null).
	 * @param queueCapacity The capacity of blocking queue to cache tasks in thread pool executor (this parameter is only used for logging).
	 */
	public TaskHandler(String name, ThreadPoolExecutor executor, int queueCapacity) {
		// Set handler name
		this.name = StringHelper.isEmpty(name) ? "Task-Handler" : "Task-" + name;
		// Create task queue and timer loop thread
		this.queue = new TimerTaskQueue(this.name);
		this.thread = new TimerTaskThread(this.name, this, queue);
		this.executor = executor;
		// Logs message
		this.thread.logger.info(thread.locale.text("core.util.task.start", this.name, executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getKeepAliveTime(TimeUnit.SECONDS), queueCapacity));
	}

	// --------------------------- Static methods ----------------------------

	/**
	 * Gets a default global task handler instance object (returns not null).<br/>
	 * Use parameters below or use the framework configuration parameters:<br/>
	 * <b>corePoolSize: 8, maximumPoolSize: 8, keepAliveTime: 60s, queueCapacity: 1000</b>
	 * @return Task handler object.
	 */
	public static final TaskHandler getDefaultHandler() {
		if (instance != null) return instance;
		synchronized (instanceLock) {
			if (instance != null) return instance;
			// Gets the global configuration.
			TaskThreadPoolConfig config = PropertiesLoader.getConfigBean(TaskThreadPoolConfig.class, ConfigureHandler.getFrameworkConfiguration(), "iot.framework.global.task.pool");
			// Create instance.
			if (config == null) {
				instance = new TaskHandler("Default", 8, 8, 60, 1600);
			} else {
				instance = new TaskHandler("Default", config.corePoolSize, config.maximumPoolSize, config.keepAliveTime, config.capacity);
			}
		}
		return instance;
	}

	/**
	 * Create a thread pool executor.<br/>
	 * <b>NOTE:</b><br/>
	 * When the <b>corePoolSize</b> equals to the <b>maximumPoolSize</b> and the <b>keepAliveTime</b> greater than 0, <br/>
	 * the thread pool executor with allow the core threads timeout automatically.
	 * @param name The handler name used for the loop thread.
	 * @param corePoolSize The number of threads to keep in the pool.
	 * @param maximumPoolSize The maximum number of threads to allow in the pool.
	 * @param keepAliveTime The maximum seconds that excess idle threads will wait for new tasks before terminating.
	 * @param queueCapacity The capacity of blocking queue to cache tasks in thread pool executor (when set to 0, the synchronous queue {@link SynchronousQueue } is used; when set to greater than 0, the bounded priority blocking queue {@link PriorityLimitedBlockingQueue } is used; when set to less than 0, the unbounded priority blocking queue {@link PriorityBlockingQueue } is used).
	 * @return A new thread pool executor.
	 */
	private static final ThreadPoolExecutor createExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, int queueCapacity) {
		// Define the blocking queue.
		BlockingQueue<Runnable> queue;
		// Check queue capacity.
		if (queueCapacity > 0) {
			// Create a bounded priority blocking queue.
			queue = new PriorityLimitedBlockingQueue<Runnable>(queueCapacity);
		} else if (queueCapacity == 0) {
			// Create a synchronous queue.
			queue = new SynchronousQueue<Runnable>();
		} else {
			// Create a bounded priority blocking queue.
			queue = new PriorityBlockingQueue<Runnable>();
		}
		// Create thread factory.
		NamedThreadFactory factory = new NamedThreadFactory(StringHelper.isEmpty(name) ? "Task-Handler" : "Task-" + name);
		// Create the executor.
		ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, queue, factory, new ThreadPoolExecutor.AbortPolicy());
		// Check the core pool size and maximum pool size.
		if (corePoolSize == maximumPoolSize && keepAliveTime > 0) {
			// Set up a core thread pool that automatically increases or decreases.
			executor.allowCoreThreadTimeOut(true);
		}
		// Return the executor.
		return executor;
	}

	/**
	 * Gets the delay time for every milliseconds.
	 * @param ms The milliseconds.
	 * @return long Delay time.
	 */
	private static final long getDelayMilliseconds(long ms) {
		long now = System.currentTimeMillis();
		return ms - (now % ms);
	}

	// --------------------------- Public executor methods ----------------------------

	/**
	 * Gets the task handler name.
	 * @return Task handler name.
	 */
	public String getName() {
		return name;
	}

	@Override
	public ThreadPoolExecutor getThreadPoolExecutor() {
		return executor;
	}

	@Override
	public boolean run(Runnable runnable) {
		return run(runnable, 0, null);
	}

	@Override
	public boolean run(Runnable runnable, int priority) {
		return run(runnable, priority, null);
	}

	@Override
	public boolean run(Runnable runnable, ThreadLocalPostman[] postmen) {
		return run(runnable, 0, postmen);
	}

	@Override
	public boolean run(Runnable runnable, int priority, ThreadLocalPostman[] postmen) {
		// Check parameter and status.
		if (runnable == null || destroyed) return false;

		// Define the runner.
		Runnable runner;
		// Check for postman objects
		if (postmen != null && postmen.length > 0) {
			// Create the postman runnable object.
			runner = new PostmanRunnableTask(runnable, priority, postmen);
		} else if (priority == 0 && (runnable instanceof PriorityRunnable)) {
			// Set as runner.
			runner = runnable;
		} else {
			// Create the priority runnable object.
			runner = new PriorityRunnableTask(runnable, priority);
		}

		try {
			synchronized (executionLock) {
				if (destroyed) return false;
				executor.execute(runner);
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
		synchronized (executionLock) {
			if (destroyed) return;
			destroyed = true;
		}
		// Stop thread loop, the queue will be cleared
		thread.stopLoop();
		try {
			// Shutdown thread pool
			executor.shutdown();
		} catch (Exception e) {
			// Logs error
			thread.logger.error(thread.locale.text("core.util.task.shutdown.err", name, e.getMessage()), e);
		}
		// Logs destroy message
		thread.logger.info(thread.locale.text("core.util.task.destory", name));
	}

	// --------------------------- Delay time methods ----------------------------

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

	// --------------------------- Delay task methods ----------------------------

	/**
	 * Add a delay task to be executed after the specified delay time, the task will be executed only once.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @return When the addition is successful, the task sequence number greater than 0 will be returned, returns 0 if it fails.
	 */
	public long addDelayTask(Runnable task, long delay) {
		return this.addExecutionTask(null, task, delay, -1, 1, 0);
	}

	/**
	 * Add a delay task to be executed after the specified delay time, the task will be executed only once.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param priority The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 * @return When the addition is successful, the task sequence number greater than 0 will be returned, returns 0 if it fails.
	 */
	public long addDelayTask(Runnable task, long delay, int priority) {
		return this.addExecutionTask(null, task, delay, -1, 1, priority);
	}

	/**
	 * Add a delay task to be executed after the specified delay time, the task will be executed only once.
	 * @param name Task name, will be used for logging.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @return When the addition is successful, the task sequence number greater than 0 will be returned, returns 0 if it fails.
	 */
	public long addDelayTask(String name, Runnable task, long delay) {
		return this.addExecutionTask(name, task, delay, -1, 1, 0);
	}

	/**
	 * Add a delay task to be executed after the specified delay time, the task will be executed only once.
	 * @param name Task name, will be used for logging.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param priority The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 * @return When the addition is successful, the task sequence number greater than 0 will be returned, returns 0 if it fails.
	 */
	public long addDelayTask(String name, Runnable task, long delay, int priority) {
		return this.addExecutionTask(name, task, delay, -1, 1, priority);
	}

	// --------------------------- Interval task methods ----------------------------

	/**
	 * Add an interval task to be executed after the specified delay time, and then execute according to each specified interval.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param interval Time in milliseconds between successive task executions (greater than 0).
	 * @return When the addition is successful, the task sequence number greater than 0 will be returned, returns 0 if it fails.
	 */
	public long addIntervalTask(Runnable task, long delay, long interval) {
		return this.addExecutionTask(null, task, delay, interval, -1, 0);
	}

	/**
	 * Add a interval task to be executed after the specified delay time, and then execute according to each specified interval.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param interval Time in milliseconds between successive task executions (greater than 0).
	 * @param priority The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 * @return When the addition is successful, the task sequence number greater than 0 will be returned, returns 0 if it fails.
	 */
	public long addIntervalTask(Runnable task, long delay, long interval, int priority) {
		return this.addExecutionTask(null, task, delay, interval, -1, priority);
	}

	/**
	 * Add a interval task to be executed after the specified delay time, and then execute according to each specified interval.
	 * @param name Task name, will be used for logging.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param interval Time in milliseconds between successive task executions (greater than 0).
	 * @return When the addition is successful, the task sequence number greater than 0 will be returned, returns 0 if it fails.
	 */
	public long addIntervalTask(String name, Runnable task, long delay, long interval) {
		return this.addExecutionTask(name, task, delay, interval, -1, 0);
	}

	/**
	 * Add a interval task to be executed after the specified delay time, and then execute according to each specified interval.
	 * @param name Task name, will be used for logging.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param interval Time in milliseconds between successive task executions (greater than 0).
	 * @param priority The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 * @return When the addition is successful, the task sequence number greater than 0 will be returned, returns 0 if it fails.
	 */
	public long addIntervalTask(String name, Runnable task, long delay, long interval, int priority) {
		return this.addExecutionTask(name, task, delay, interval, -1, priority);
	}

	// --------------------------- Execution task methods ----------------------------

	/**
	 * Add a execution task to be executed after the specified delay time, and then execute according to each specified interval.<br/>
	 * The maximum number of times the task runs does not exceed the number of executions.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param interval Time in milliseconds between successive task executions (greater than 0).
	 * @param executions Maximum number of tasks executed (greater than 0).
	 * @return When the addition is successful, the task sequence number greater than 0 will be returned, returns 0 if it fails.
	 */
	public long addExecutionTask(Runnable task, long delay, long interval, long executions) {
		return this.addExecutionTask(null, task, delay, interval, executions, 0);
	}

	/**
	 * Add a execution task to be executed after the specified delay time, and then execute according to each specified interval.<br/>
	 * The maximum number of times the task runs does not exceed the number of executions.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param interval Time in milliseconds between successive task executions (greater than 0).
	 * @param executions Maximum number of tasks executed (greater than 0).
	 * @param priority The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 * @return When the addition is successful, the task sequence number greater than 0 will be returned, returns 0 if it fails.
	 */
	public long addExecutionTask(Runnable task, long delay, long interval, long executions, int priority) {
		return this.addExecutionTask(null, task, delay, interval, executions, priority);
	}

	/**
	 * Add a execution task to be executed after the specified delay time, and then execute according to each specified interval.<br/>
	 * The maximum number of times the task runs does not exceed the number of executions.
	 * @param name Task name, will be used for logging.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param interval Time in milliseconds between successive task executions (greater than 0).
	 * @param executions Maximum number of tasks executed (greater than 0).
	 * @return When the addition is successful, the task sequence number greater than 0 will be returned, returns 0 if it fails.
	 */
	public long addExecutionTask(String name, Runnable task, long delay, long interval, long executions) {
		return this.addExecutionTask(name, task, delay, interval, executions, 0);
	}

	/**
	 * Add a execution task to be executed after the specified delay time, and then execute according to each specified interval.<br/>
	 * The maximum number of times the task runs does not exceed the number of executions.
	 * @param name Task name, will be used for logging.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param interval Time in milliseconds between successive task executions (greater than 0).
	 * @param executions Maximum number of tasks executed (greater than 0).
	 * @param priority The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 * @return When the addition is successful, the task sequence number greater than 0 will be returned, returns 0 if it fails.
	 */
	public long addExecutionTask(String name, Runnable task, long delay, long interval, long executions, int priority) {
		// Parameter verification (-1 means no restriction)
		if (destroyed || task == null || delay < 0 || interval == 0 || interval < -1 || executions == 0 || executions < -1) return 0;
		// Start timer thread
		thread.startLoop();
		// Add a task to queue
		return queue.add(name, task, delay, interval, executions, priority);
	}

	// --------------------------- Other public methods ----------------------------

	/**
	 * Gets the size of tasks in handler.
	 * @return Task size.
	 */
	public long size() {
		return queue.size();
	}

	/**
	 * Gets the task ID of the last added task (the return value is 0 before adding tasks).
	 * @return The task ID.
	 */
	public long getLastTaskID() {
		return queue.getLastTaskID();
	}

	/**
	 * Gets a task runnable object from handler, returns null when it does not exist.
	 * @param taskID The timer task sequence number returned when adding (required, greater than 0).
	 * @return The task runnable object, returns null when it does not exist.
	 */
	public Runnable getTask(long taskID) {
		TimerTask task = queue.get(taskID);
		return task == null ? null : task.getRunner();
	}

	/**
	 * Gets a task status data from handler, returns null when it does not exist.
	 * @param taskID The timer task sequence number returned when adding (required, greater than 0).
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
	 * @param amount Maximum number of data returned (required, greater than 0).
	 * @return Tasks status data array.
	 */
	public TimerTaskStatus[] getBusyTaskStatus(int amount) {
		return queue.getBusyTaskStatus(amount);
	}

	/**
	 * Determine whether the timer task exists.
	 * @param taskID The timer task sequence number returned when adding (required, greater than 0).
	 * @return boolean whether the task exists.
	 */
	public boolean contains(long taskID) {
		return queue.contains(taskID);
	}

	/**
	 * Remove a timer task from task queue of handler.
	 * @param taskID The timer task sequence number returned when adding (required, greater than 0).
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
