package org.iotcity.iot.framework.core.util.task;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Priority supported runnable.
 * @author ardon
 * @date 2021-06-30
 */
public abstract class PriorityRunnable implements Comparable<PriorityRunnable>, Runnable {

	/**
	 * Global automatic numbering.
	 */
	private final static AtomicLong auto = new AtomicLong();
	/**
	 * Automatic numbering of current task priority.
	 */
	private final long seq;
	/**
	 * The runnable execution priority.
	 */
	private int priority;

	/**
	 * Constructor for priority supported runnable (0 priority by default).<br/>
	 */
	public PriorityRunnable() {
		this(0);
	}

	/**
	 * Constructor for priority supported runnable.
	 * @param priority The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 */
	public PriorityRunnable(int priority) {
		this.seq = auto.getAndIncrement();
		this.priority = priority;
	}

	/**
	 * Gets the runnable execution priority.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Set the runnable execution priority.
	 * @param priority The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public int compareTo(PriorityRunnable p) {
		if (priority == p.priority) {
			return seq < p.seq ? -1 : 1;
		} else {
			return priority < p.priority ? 1 : -1;
		}
	}

}
