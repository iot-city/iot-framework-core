package org.iotcity.iot.framework.core.util.task;

import java.util.Date;

import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.i18n.LocaleText;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.SystemHelper;

/**
 * Timer task thread object.
 * @author Ardon
 */
final class TimerTaskThread extends Thread {

	// --------------------------- Static fields ----------------------------

	/**
	 * Logs statistic message time
	 */
	private final static long STATISTIC_TIME = SystemHelper.HOUR_MS;

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
		this.logger.info(this.locale.text("core.util.task.thread.start", this.getName()));
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

		// Loop execution while not stopped
		while (!stopped) {

			// ---------------------- EXECUTE TASKS -----------------

			// Get start time of task execution
			long startTime = System.currentTimeMillis();
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

			// Logs statistic message every specified time
			if (endTime - statisticTime > STATISTIC_TIME) {
				// Set to current time
				statisticTime = endTime;
				// Logs message
				logger.info(locale.text("core.util.task.task.stat"));
				logger.info(locale.text("core.util.task.task.stat.info", this.getName(), queue.size(), recorder.loopCount, recorder.size(), recorder.maxExecTime, recorder.minExecTime, recorder.avgExecTime, recorder.avgWaitTime));
			}

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
				System.out.println("Timer task queue lock interrupted.");
			}

			// ---------------------- CHECK FOR SYSTEM TIME UPDATE -----------------

			// Get current system time
			long currentTime = System.currentTimeMillis();
			// Record to the time's recorder
			recorder.record(startTime, endTime, waitTime, currentTime);

			// Skip stopped
			if (!stopped) {
				// Check for system time update
				if (recorder.systemTimeChanged(startTime, waitTime, currentTime)) {
					// Update task execution time when system time changed
					queue.systemTimeUpdate(currentTime);

					// Logs change message
					String start = ConvertHelper.formatDate(new Date(startTime));
					String end = ConvertHelper.formatDate(new Date(endTime));
					String now = ConvertHelper.formatDate(new Date(currentTime));
					// Logs message
					logger.warn(locale.text("core.util.task.time.changed", this.getName(), now, start, end, waitTime));

				}
			}

		}

		// Logs message before exiting
		logger.info(locale.text("core.util.task.task.stat"));
		logger.info(locale.text("core.util.task.task.stat.info", this.getName(), queue.size(), recorder.loopCount, recorder.size(), recorder.maxExecTime, recorder.minExecTime, recorder.avgExecTime, recorder.avgWaitTime));

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
