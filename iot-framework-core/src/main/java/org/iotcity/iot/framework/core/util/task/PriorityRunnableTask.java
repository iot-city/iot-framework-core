package org.iotcity.iot.framework.core.util.task;

/**
 * Priority supported runnable task.
 * @author ardon
 * @date 2021-06-30
 */
final class PriorityRunnableTask extends PriorityRunnable {

	/**
	 * The runnable object.
	 */
	private final Runnable runnable;

	/**
	 * Constructor for priority supported runnable task.
	 * @param runnable The runnable object to be executed (required, not null).
	 * @param priority The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 */
	PriorityRunnableTask(Runnable runnable, int priority) {
		super(priority);
		this.runnable = runnable;
	}

	@Override
	public void run() {
		// Execute runnable
		runnable.run();
	}

}
