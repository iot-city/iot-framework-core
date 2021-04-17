package org.iotcity.iot.framework.core.util.task;

/**
 * Timer task statistic data of all tasks.
 * @author Ardon
 */
public final class TimerTaskStatistic {

	/**
	 * Number of running tasks at statistic time.
	 */
	public final long runningTasks;
	/**
	 * Total execution times of all tasks.
	 */
	public final long totalExecuteCount;
	/**
	 * Total number of tasks finished.
	 */
	public final long totalFinished;
	/**
	 * Total number of times all tasks were run.
	 */
	public final long totalRunTimes;
	/**
	 * Total elapsed time of all tasks in milliseconds.
	 */
	public final long totalElapsedTime;
	/**
	 * Average elapsed time per run of all tasks in milliseconds.
	 */
	public final long avgElapsedTImePerRun;

	/**
	 * Constructor for timer task statistic data.
	 * @param runningTasks Number of running tasks at statistic time.
	 * @param totalExecuteCount Total execution times of all tasks.
	 * @param totalFinished Total number of tasks finished.
	 * @param totalRunTimes Total number of times all tasks were run.
	 * @param totalElapsedTime Total elapsed time of all tasks in milliseconds.
	 */
	TimerTaskStatistic(long runningTasks, long totalExecuteCount, long totalFinished, long totalRunTimes, long totalElapsedTime) {
		this.runningTasks = runningTasks;
		this.totalExecuteCount = totalExecuteCount;
		this.totalFinished = totalFinished;
		this.totalRunTimes = totalRunTimes;
		this.totalElapsedTime = totalElapsedTime;
		this.avgElapsedTImePerRun = totalRunTimes > 0 ? totalElapsedTime / totalRunTimes : 0;
	}

}
