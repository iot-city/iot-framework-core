package org.iotcity.iot.framework.core.beans;

/**
 * A runnable object that posts thread local data.
 * @author Ardon
 * @date 2021-04-25
 */
public class PostmanRunnable implements Runnable {

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
	public PostmanRunnable(Runnable runnable, ThreadLocalPostman... postmen) throws IllegalArgumentException {
		if (runnable == null) throw new IllegalArgumentException("Parameter runnable can not be null!");
		this.runnable = runnable;
		this.postmen = postmen;
	}

	@Override
	public void run() {
		if (postmen != null && postmen.length > 0) {
			// Store thread local data to runnable thread.
			for (ThreadLocalPostman postman : postmen) {
				postman.storeToCurrentThread();
			}
		}
		// Execute runnable
		runnable.run();
	}

}
