package org.iotcity.iot.framework.core.util.task;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Timer task thread object.
 * @author Ardon
 */
final class TimerTaskThread extends Thread {

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

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for timer task thread.
	 * @param name The thread name.
	 * @param handler Task handler object.
	 * @param queue Timer task queue object.
	 */
	TimerTaskThread(String name, TaskHandler handler, TimerTaskQueue queue) {
		super(StringHelper.isEmpty(name) ? "TimerTaskThread" : name);
		this.handler = handler;
		this.queue = queue;
	}

	// --------------------------- Loop methods ----------------------------

	@Override
	public void run() {
		// Check started status
		if (!started) return;
		try {
			// Do main loop
			mainLoop();
		} finally {
			// Clear all tasks
			queue.clear();
		}
	}

	/**
	 * The main timer loop.
	 */
	private void mainLoop() {

		// Loop execution while not stopped
		while (!stopped) {

			// ---------------------- EXECUTE TASKS -----------------

			// Get system time
			long currentTime = System.currentTimeMillis();
			// Get the tasks ready to be executed
			TimerTask[] tasks = queue.getReadyTasks(currentTime);

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

			// Get the wait time for next loop
			long waitTime = queue.getWaitTime();

			try {
				// Synchronize queue to get a lock
				synchronized (queue) {

					// Check stopped state
					if (stopped) break;
					// Waiting for next loop
					if (waitTime > 0) {
						queue.wait(waitTime);
					} else {
						queue.wait();
					}

				}
			} catch (Exception e) {
				// if any thread interrupted the current thread before or while the current thread was waiting for a notification.
				System.out.println("Timer task queue lock interrupted.");
			}

		}
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
