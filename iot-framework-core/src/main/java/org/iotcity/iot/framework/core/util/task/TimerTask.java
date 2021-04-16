package org.iotcity.iot.framework.core.util.task;

import org.iotcity.iot.framework.core.FrameworkCore;

/**
 * The timer task<br/>
 * This task will be executed in single thread mode within the thread pool.
 * @author Ardon
 */
final class TimerTask implements Runnable {

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
	private final long period;
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
	 * Total number of tasks executed
	 */
	private long runsCount;
	/**
	 * Whether the execution has finished
	 */
	private boolean finished;
	/**
	 * The next execution time of the task.
	 */
	private long nextRunTime;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for timer task
	 * @param id Task serial number.
	 * @param task Task to be execute.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param period Time in milliseconds between successive task executions (greater than 0 or -1 means no restriction).
	 * @param executions Maximum number of tasks executed (greater than 0 or -1 means no restriction).
	 * @param currentTime Current system time.
	 */
	TimerTask(TimerTaskQueue queue, long id, Runnable task, long delay, long period, long executions, long currentTime) {
		// Config data
		this.queue = queue;
		this.id = id;
		this.task = task;
		this.delay = delay;
		this.period = period;
		this.executions = executions;
		this.createTime = currentTime;
		// Status data
		this.running = false;
		this.runsCount = 0;
		this.finished = false;
		this.nextRunTime = currentTime + delay;
	}

	// --------------------------- Friendly methods ----------------------------

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
	 * @return Whether the current task is allowed to be executed.
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
		// Update next execution time if can not be executed in thread pool
		if (!running) queue.updateNextRunTime(this);
	}

	/**
	 * Update the status of the task being executed, remove finished task.
	 */
	void updateExecuted() {
		// Set runtime status
		runsCount++;
		// Determine maximum number of execution
		if (executions != -1 && runsCount >= executions) {
			// Set execution complete
			finished = true;
			// Remove from queue
			queue.remove(id);
		} else {
			// Determine execution cycle
			if (period > 0) {
				// Set next execution time
				nextRunTime += period;
				// Update next execution time
				queue.updateNextRunTime(this);
			} else {
				// Set execution complete
				finished = true;
				// Remove from queue
				queue.remove(id);
			}
		}
	}

	/**
	 * Automatically adjust task time when system time is updated.
	 * @param currentTime Current system time.
	 */
	void systemTimeUpdate(long currentTime) {
		// Skip tasks with no period or finished
		if (period <= 0 || finished) return;
		// Get running start time
		long startTime = createTime + delay;
		// Check the range from start time to now time
		if (startTime <= currentTime) {
			// Set next running time
			nextRunTime = currentTime + period - ((currentTime - startTime) % period);
		} else {
			// Set to previous running time
			nextRunTime = currentTime + (startTime - currentTime) % period;
		}
	}

	// --------------------------- Public methods ----------------------------

	@Override
	public void run() {
		try {
			// Run task
			task.run();
		} catch (Exception e) {
			// Logs error
			FrameworkCore.getLogger().error(FrameworkCore.getLocale().text("core.util.task.task.err", queue.getName(), task.getClass(), e.getMessage()), e);
		}
		// Set running status (No need to consider thread safety, it's safe in main loop).
		running = false;
	}

}
