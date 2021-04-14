package org.iotcity.iot.framework.core.util.task;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Ardon
 */
final class TimerTaskQueue {

	private final AtomicLong autoID = new AtomicLong(0);

	/**
	 * Constructor for
	 */
	TimerTaskQueue() {
	}

	public long add(TimerTask task) {
		autoID.incrementAndGet();
		return 0;
	}

	public boolean contains(long taskID) {
		return false;
	}

	public Runnable remove(long taskID) {
		return null;
	}

	public void clear() {

	}

}
