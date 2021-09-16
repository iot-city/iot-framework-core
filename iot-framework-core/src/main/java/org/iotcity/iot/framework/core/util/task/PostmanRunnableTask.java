package org.iotcity.iot.framework.core.util.task;

import org.iotcity.iot.framework.core.beans.ThreadLocalPostman;

/**
 * A runnable task that posts thread local data.
 * @author Ardon
 * @date 2021-04-25
 */
final class PostmanRunnableTask extends PriorityRunnable {

	/**
	 * The runnable object.
	 */
	private final Runnable runnable;
	/**
	 * Postman objects.
	 */
	private final ThreadLocalPostman[] postmen;

	/**
	 * Constructor for a runnable object that posts thread local data.
	 * @param runnable The runnable object to be executed (required, not null).
	 * @param postmen Postmen who sends thread local data from one thread to another.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "runnable" is null.
	 */
	PostmanRunnableTask(Runnable runnable, ThreadLocalPostman[] postmen) throws IllegalArgumentException {
		super(0);
		if (runnable == null) throw new IllegalArgumentException("Parameter runnable can not be null!");
		this.runnable = runnable;
		this.postmen = postmen;
	}

	/**
	 * Constructor for a runnable object that posts thread local data.
	 * @param runnable The runnable object to be executed (required, not null).
	 * @param priority The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 * @param postmen Postmen who sends thread local data from one thread to another.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "runnable" is null.
	 */
	PostmanRunnableTask(Runnable runnable, int priority, ThreadLocalPostman[] postmen) throws IllegalArgumentException {
		super(priority);
		if (runnable == null) throw new IllegalArgumentException("Parameter runnable can not be null!");
		this.runnable = runnable;
		this.postmen = postmen;
	}

	@Override
	public void run() {
		// Store thread local data to runnable thread.
		if (postmen != null && postmen.length > 0) {
			for (ThreadLocalPostman postman : postmen) {
				postman.storeToCurrentThread();
			}
		}

		// Execute runnable
		runnable.run();

		// Remove all variables in current thread local.
		if (postmen != null && postmen.length > 0) {
			for (ThreadLocalPostman postman : postmen) {
				postman.removeAll();
			}
		}
	}

}
