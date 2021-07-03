package org.iotcity.iot.framework.core.util.task;

import java.util.concurrent.atomic.AtomicLong;

import org.iotcity.iot.framework.core.FrameworkCore;

/**
 * The timer task.<br/>
 * This task will be executed in single thread mode within the thread pool.
 * @author Ardon
 */
final class TimerTask extends PriorityRunnable {

	// --------------------------- Private fields ----------------------------

	/**
	 * Timer task queue object
	 */
	private final TimerTaskQueue queue;
	/**
	 * Task serial number.
	 */
	private final long id;
	/**
	 * Task name, will be used for logging.
	 */
	private final String name;
	/**
	 * Task to be execute (not null).
	 */
	private final Runnable task;
	/**
	 * Delay in milliseconds before task is to be executed (greater than 0).
	 */
	private final long delay;
	/**
	 * Time in milliseconds between successive task executions (greater than 0 or -1 means no restriction).
	 */
	private final long interval;
	/**
	 * Maximum number of tasks executed (greater than 0 or -1 means no restriction).
	 */
	private final long executions;
	/**
	 * The task creation time.
	 */
	private final long createTime;

	// --------------------------- Status fields ----------------------------

	/**
	 * Whether the task is running (No need to consider thread safety, it's safe in main loop).
	 */
	private boolean running;
	/**
	 * Total number of tasks scheduled.
	 */
	private long scheduleCount;
	/**
	 * Total number of tasks executed.
	 */
	private long executeCount;
	/**
	 * Whether the execution has finished.
	 */
	private boolean finished;
	/**
	 * Whether run task finished.
	 */
	private boolean runFinished;
	/**
	 * The next execution time of the task.
	 */
	private long nextRunTime;
	/**
	 * The number of times the task runs.
	 */
	private long runTimes;
	/**
	 * Elapsed time of task running in milliseconds.
	 */
	private long runElapsedTime;
	/**
	 * System time be changed within running.
	 */
	private AtomicLong systemTimeChanged = new AtomicLong(0);

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for timer task
	 * @param queue Timer task queue object.
	 * @param id Task serial number.
	 * @param name Task name, will be used for logging.
	 * @param task Task to be execute.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param interval Time in milliseconds between successive task executions (greater than 0 or -1 means no restriction).
	 * @param executions Maximum number of tasks executed (greater than 0 or -1 means no restriction).
	 * @param priority The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 * @param currentTime Current system time.
	 */
	TimerTask(TimerTaskQueue queue, long id, String name, Runnable task, long delay, long interval, long executions, long currentTime, int priority) {
		super(priority);
		// Config data
		this.queue = queue;
		this.id = id;
		this.name = name;
		this.task = task;
		this.delay = delay;
		this.interval = interval;
		this.executions = executions;
		this.createTime = currentTime;
		// Status data
		this.nextRunTime = currentTime + delay;
	}

	// --------------------------- Friendly methods ----------------------------

	/**
	 * Gets running status data of current timer task (not null).
	 * @return Running status data.
	 */
	TimerTaskStatus getStatus() {
		return new TimerTaskStatus(id, name, running, scheduleCount, runFinished, nextRunTime, runTimes, runElapsedTime);
	}

	/**
	 * Task name, will be used for logging.
	 * @return Task name.
	 */
	String getName() {
		return name;
	}

	/**
	 * Gets the runner object of this task.
	 * @return Runnable object.
	 */
	Runnable getRunner() {
		return task;
	}

	/**
	 * Gets the next execution time of the task.
	 * @return
	 */
	long getNextTime() {
		return nextRunTime;
	}

	/**
	 * Determine whether the current task is allowed to be executed.
	 * @param currentTime Current system time.
	 * @return Returns true if the current task is allowed to be executed; otherwise, returns false.
	 */
	boolean isReady(long currentTime) {
		// Check the task time and running status
		return (currentTime >= nextRunTime && !running && !finished);
	}

	/**
	 * Set the running status of the task.
	 * @param running Whether the task is running.
	 */
	void setRunning(boolean running) {
		// Set the running status of the task
		this.running = running;
		// Check running
		if (running) {
			// Increase scheduled count
			scheduleCount++;
			// Increase total executed
			queue.totalExecuteCount.incrementAndGet();
		} else {
			// Update next execution time if can not be executed in thread pool
			queue.updateNextRunTime(this);
		}
	}

	/**
	 * Update the status of the task being executed, remove finished task.
	 */
	void updateExecuted() {
		// Increase executed count
		executeCount++;
		// Increase total running task
		queue.runningTasks.incrementAndGet();
		// Determine maximum number of execution
		if (executions != -1 && executeCount >= executions) {
			// Set execution complete
			finished = true;
		} else {
			// Determine execution cycle
			if (interval > 0) {
				// Set next execution time
				nextRunTime += interval;
				// Update next execution time
				queue.updateNextRunTime(this);
			} else {
				// Set execution complete
				finished = true;
			}
		}
	}

	/**
	 * Automatically adjust task time when system time is updated.
	 * @param changeMilliseconds Milliseconds that have changed.
	 * @param currentTime Current system time.
	 */
	void systemTimeUpdate(long changeMilliseconds, long currentTime) {
		// Skip tasks when finished
		if (finished) return;
		// Save the time that have changed
		systemTimeChanged.addAndGet(changeMilliseconds);
		// Check the interval
		if (interval <= 0) {
			// The task is executed only once after the delay time
			nextRunTime += changeMilliseconds;
		} else {
			// Get running start time
			long startTime = createTime + delay;
			// Check the range from start time to now time
			if (startTime <= currentTime) {
				// Set next running time
				nextRunTime = currentTime + interval - ((currentTime - startTime) % interval);
			} else {
				// Set to previous running time
				nextRunTime = currentTime + ((startTime - currentTime) % interval);
			}
		}
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * No need to consider thread safety, it is safe in the thread pool managed by the main loop.
	 */
	@Override
	public void run() {

		// Gets running start time
		long start = System.currentTimeMillis();
		// Reset the system time that have changed before running
		systemTimeChanged.set(0);

		try {
			// Run task
			task.run();
		} catch (Exception e) {
			// Logs error: Timer task execution error in handler "{0}", task name: "{1}", class: "{2}", message: {3}
			FrameworkCore.getLogger().error(FrameworkCore.getLocale().text("core.util.task.task.err", queue.getName(), name, task.getClass().getName(), e.getMessage()), e);
		}

		// Get running end time
		long end = System.currentTimeMillis();
		// Get time changed within running
		long changed = systemTimeChanged.get();
		// Average elapsed time
		long avgElapse = runTimes > 0 ? runElapsedTime / runTimes : 0;
		// Elapsed time
		long elapsed;
		// Check time changes
		if (changed != 0) {
			// Use changed time to get elapsed time if the system time update event has caught
			elapsed = end - start - changed;
			if (elapsed < 0) elapsed = avgElapse;
		} else if (end < start || avgElapse > 0 && end - start > avgElapse * 5) {
			// The time has changed
			elapsed = avgElapse;
		} else {
			// Set normal time
			elapsed = end - start;
		}
		// Increase the elapsed time
		runElapsedTime += elapsed;
		// Increase total elapsed time
		queue.totalElapsedTime.addAndGet(elapsed);

		// Increase the number of times
		runTimes++;
		// Increase total run times
		queue.totalRunTimes.incrementAndGet();
		// Decrease total running task
		queue.runningTasks.decrementAndGet();
		// Remove finished
		if (finished) {
			// Set task finished
			runFinished = true;
			// Increase total finished
			queue.totalFinished.incrementAndGet();
			// Remove from queue
			queue.remove(id);
		}

		// Set running status.
		running = false;
	}

}
