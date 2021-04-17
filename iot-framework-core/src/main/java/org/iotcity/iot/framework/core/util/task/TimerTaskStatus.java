package org.iotcity.iot.framework.core.util.task;

/**
 * Running status data of timer task.
 * @author Ardon
 */
public final class TimerTaskStatus {

	/**
	 * The timer task ID.
	 */
	public final long id;
	/**
	 * Task name.
	 */
	public final String name;
	/**
	 * Whether the task is running.
	 */
	public final boolean running;
	/**
	 * Total number of task executed.
	 */
	public final long executeCount;
	/**
	 * Whether the task execution has finished.
	 */
	public final boolean finished;
	/**
	 * The next execution time in milliseconds of the task.
	 */
	public final long nextRunTime;
	/**
	 * The number of times the task runs.
	 */
	public final long runTimes;
	/**
	 * Elapsed time of task running in milliseconds.
	 */
	public final long runElapsedTime;
	/**
	 * Average elapsed time per run in milliseconds.
	 */
	public final long avgElapsedTImePerRun;

	/**
	 * Constructor for running status data of timer task.
	 * @param id The timer task ID.
	 * @param running Whether the task is running.
	 * @param executeCount Total number of task executed.
	 * @param finished Whether the task execution has finished.
	 * @param nextRunTime The next execution time in milliseconds of the task.
	 * @param runTimes The number of times the task runs.
	 * @param runElapsedTime Elapsed time of task running in milliseconds.
	 */
	TimerTaskStatus(long id, String name, boolean running, long executeCount, boolean finished, long nextRunTime, long runTimes, long runElapsedTime) {
		this.id = id;
		this.name = name;
		this.running = running;
		this.executeCount = executeCount;
		this.finished = finished;
		this.nextRunTime = nextRunTime;
		this.runTimes = runTimes;
		this.runElapsedTime = runElapsedTime;
		this.avgElapsedTImePerRun = runTimes > 0 ? runElapsedTime / runTimes : 0;
	}

}
