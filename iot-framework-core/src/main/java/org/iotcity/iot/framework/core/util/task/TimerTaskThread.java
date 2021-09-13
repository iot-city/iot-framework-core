package org.iotcity.iot.framework.core.util.task;

import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.i18n.LocaleText;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.helper.SystemHelper;

/**
 * Timer task thread object.
 * @author Ardon
 */
final class TimerTaskThread extends Thread {

	// --------------------------- Static fields ----------------------------

	/**
	 * Logs statistic message time.
	 */
	private final static long STATISTIC_TIME = SystemHelper.HOUR_MS;
	/**
	 * The time format to log message.
	 */
	private final static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	// --------------------------- Friendly fields ----------------------------

	/**
	 * The logger object.
	 */
	final Logger logger;
	/**
	 * The locale object.
	 */
	final LocaleText locale;

	// --------------------------- Private fields ----------------------------

	/**
	 * Task handler object.
	 */
	private final TaskHandler handler;
	/**
	 * Timer task queue object.
	 */
	private final TimerTaskQueue queue;
	/**
	 * Timer task time recorder.
	 */
	private final TimerTaskRecorder recorder;
	/**
	 * The thread state lock
	 */
	private Object lock = new Object();
	/**
	 * Whether the thread is started.
	 */
	private boolean started = false;
	/**
	 * Whether the thread is stopped.
	 */
	private boolean stopped = false;
	/**
	 * Last time logs statistic message (the first record will be log in 10 minutes).
	 */
	private long statisticTime = System.currentTimeMillis() - 50 * 60 * 1000;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for timer task thread.
	 * @param name The thread name.
	 * @param handler Task handler object.
	 * @param queue Timer task queue object.
	 */
	TimerTaskThread(String name, TaskHandler handler, TimerTaskQueue queue) {
		super(name);
		this.handler = handler;
		this.queue = queue;
		this.logger = FrameworkCore.getLogger();
		this.locale = FrameworkCore.getLocale();
		this.recorder = new TimerTaskRecorder();
	}

	// --------------------------- Loop methods ----------------------------

	@Override
	public void run() {
		// Check started status
		if (!started) return;
		// Logs start
		// this.logger.info(this.locale.text("core.util.task.thread.start", this.getName()));
		try {
			// Do main loop
			mainLoop();
		} finally {
			// Clear all tasks
			queue.clear();
		}
		// Logs end
		this.logger.info(this.locale.text("core.util.task.thread.end", this.getName()));
	}

	/**
	 * The main timer loop.
	 */
	private void mainLoop() {

		// Get start time of task execution
		long startTime = System.currentTimeMillis();
		// Loop execution while not stopped
		while (!stopped) {

			// ---------------------- EXECUTE TASKS -----------------

			// Get tasks that ready to be executed
			TimerTask[] tasks = queue.getReadyTasks(startTime);
			// Traverse data to perform tasks
			for (TimerTask task : tasks) {

				// Check stopped state
				if (stopped) break;
				// Set running status
				task.setRunning(true);
				// Run task using thread pool
				if (handler.run(task)) {
					// Update executed status and remove finished task
					task.updateExecuted();
				} else {
					// Reset running status
					task.setRunning(false);
				}

			}

			// ---------------------- WAIT FOR NEXT TIME -----------------

			// Get end time of task execution
			long endTime = System.currentTimeMillis();
			// Get wait time for next execution (greater then 0)
			long waitTime = queue.getWaitTime(endTime);
			try {
				// Synchronize queue to get a lock
				synchronized (queue) {

					// Check stopped state
					if (stopped) break;
					// Waiting for next loop
					queue.wait(waitTime);

				}
			} catch (Exception e) {
				// if any thread interrupted the current thread before or while the current thread was waiting for a notification.
				JavaHelper.log("Timer task queue lock interrupted.");
			}

			// ---------------------- RECORD TASK RUNNING STATUS -----------------

			// Get current system time
			long currentTime = System.currentTimeMillis();
			// Get the wait time after notify or timeout
			long waitTimeOnNotify = currentTime - endTime;
			// Check for notify
			if (waitTimeOnNotify >= 0 && waitTimeOnNotify < waitTime) {

				// // Has been notified
				// String end = ConvertHelper.formatDate(new Date(endTime), TIME_FORMAT);
				// String now = ConvertHelper.formatDate(new Date(currentTime), TIME_FORMAT);
				// // Logs message
				// // core.util.task.task.notify=The execution waiting lock has been released in the task handler: "{0}", time before waiting: {1}, current time: {2}, previous wait time: {3} ms, current wait time: {4} ms.
				// logger.debug(locale.text("core.util.task.task.notify", this.getName(), end, now, waitTime, waitTimeOnNotify));

				// Set actual wait time
				waitTime = waitTimeOnNotify;

			}
			// Record to the time's recorder
			recorder.record(startTime, endTime, waitTime, currentTime);

			// ---------------------- CHECK FOR SYSTEM TIME UPDATE -----------------

			// Skip stopped
			if (stopped) break;

			// Check for system time update
			if (recorder.systemTimeChanged(startTime, waitTime, currentTime)) {

				// The system time has changed, get expected milliseconds that have changed
				long execTime = endTime - startTime;
				if (execTime > recorder.avgExecTime) execTime = recorder.avgExecTime;
				// The maximum deviation time of the change time is within the set maximum waiting time
				long changes = currentTime - waitTime - execTime - startTime;

				// Set new statistic time
				statisticTime += changes;
				// Update task execution time when system time changed
				queue.systemTimeUpdate(changes, currentTime);

				// Logs change message
				String start = ConvertHelper.formatDate(new Date(startTime), TIME_FORMAT);
				String end = ConvertHelper.formatDate(new Date(endTime), TIME_FORMAT);
				String now = ConvertHelper.formatDate(new Date(currentTime), TIME_FORMAT);
				// Logs message
				// core.util.task.time.changed=The system time has changed, task handler: "{0}"; execution start time: {1}; execution end (waiting) time: {2}; current time: {3}; wait time: {4} ms; time changes: {5} ({6} ms).
				logger.warn(locale.text("core.util.task.time.changed", this.getName(), start, end, now, waitTime, ConvertHelper.formatMilliseconds(changes), changes));

			}

			// ---------------------- OUTPUT STATISTIC -----------------

			// Logs statistic message every specified time
			if (currentTime - statisticTime > STATISTIC_TIME) {
				// Set to current time
				statisticTime = currentTime;
				// Logs message
				outputStatistic();
			}

			// ---------------------- SET START TIME --------------------

			// Set start time to current time for next loop
			// Important: This step will ensure the correctness of all times in the loop
			startTime = currentTime;

		}

		// Logs message before exiting
		outputStatistic();

	}

	/**
	 * Logs task information.
	 */
	void outputTaskInfo() {
		// Gets the executor.
		ThreadPoolExecutor executor = handler.getThreadPoolExecutor();
		// Logs message
		logger.info(locale.text("core.util.task.info", handler.getName(), executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getKeepAliveTime(TimeUnit.SECONDS), handler.getQueueCapacity()));
	}

	/**
	 * Logs statistic message.
	 */
	void outputStatistic() {
		// Logs task information.
		outputTaskInfo();
		// core.util.task.task.stat=------------------ TIMER TASK EXECUTION STATISTIC INFORMATION ------------------
		logger.info(locale.text("core.util.task.task.stat"));
		// core.util.task.task.stat.info=Task handler "{0}" statistic of main loop, current tasks: {1}; loop count: {2}; latest records: {3}; execution time: {4} ms(max), {5} ms(min), {6} ms(avg); wait time: {7} ms(avg).
		logger.info(locale.text("core.util.task.task.stat.info", this.getName(), queue.size(), recorder.loopCount, recorder.size(), recorder.maxExecTime, recorder.minExecTime, recorder.avgExecTime, recorder.avgWaitTime));
		// Logs statistic message
		TimerTaskStatistic stat = queue.getStatistic();
		// core.util.task.task.stat.run=Task handler "{0}" statistic of all tasks, accumulative total tasks: {1}, total execution times: {2}; total run times: {3}; task running: {4}; task finished: {5}; total elapsed time: {6} ({7} ms); elapsed time per run: {8} ({9} ms).
		logger.info(locale.text("core.util.task.task.stat.run", this.getName(), queue.getLastTaskID(), stat.totalExecuteCount, stat.totalRunTimes, stat.runningTasks, stat.totalFinished, ConvertHelper.formatMilliseconds(stat.totalElapsedTime), stat.totalElapsedTime, ConvertHelper.formatMilliseconds(stat.avgElapsedTImePerRun), stat.avgElapsedTImePerRun));
		// Get busy tasks
		TimerTaskStatus[] busies = queue.getBusyTaskStatus(3);
		// Logs task status
		for (TimerTaskStatus status : busies) {
			// core.util.task.task.stat.task=Current take a long time task of handler "{0}", task id: {1}; name: {2}; execution times: {3}; run times: {4}; running: {5}; finished: {6}; total elapsed time: {7} ({8} ms); elapsed time per run: {9} ({10} ms); next execution time: {11}.
			logger.info(locale.text("core.util.task.task.stat.task", this.getName(), status.id, status.name, status.executeCount, status.runTimes, status.running, status.finished, ConvertHelper.formatMilliseconds(status.runElapsedTime), status.runElapsedTime, ConvertHelper.formatMilliseconds(status.avgElapsedTImePerRun), status.avgElapsedTImePerRun, ConvertHelper.formatDate(new Date(status.nextRunTime), TIME_FORMAT)));
		}
		// core.util.task.task.stat.end=------------------------------------------------------------------------------------
		logger.info(locale.text("core.util.task.task.stat.end"));
	}

	// --------------------------- Friendly methods ----------------------------

	/**
	 * Start the main loop for task detection.
	 */
	void startLoop() {
		// Check started status
		if (started) return;
		synchronized (lock) {
			if (started) return;
			started = true;
		}
		// Start this thread
		this.start();
	}

	/**
	 * Stop task detection thread.
	 */
	void stopLoop() {
		// STOPPED can only be set after starting and not stopping
		if (!started || stopped) return;
		synchronized (lock) {
			if (!started || stopped) return;
			stopped = true;
		}
		// Synchronize queue to release the lock
		synchronized (queue) {
			// Continue the loop to stop this thread
			queue.notifyAll();
		}
	}

}
