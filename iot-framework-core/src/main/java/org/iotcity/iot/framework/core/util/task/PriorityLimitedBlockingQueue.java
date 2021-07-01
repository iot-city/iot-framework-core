package org.iotcity.iot.framework.core.util.task;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * The bounded priority blocking queue.
 * @author ardon
 * @date 2021-07-01
 */
public class PriorityLimitedBlockingQueue<E> extends PriorityBlockingQueue<E> {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The capacity of blocking queue to cache tasks in thread pool executor.
	 */
	private final int queueCapacity;

	/**
	 * Constructor for bounded priority blocking queue.
	 * @param queueCapacity The capacity of blocking queue to cache tasks in thread pool executor.
	 */
	public PriorityLimitedBlockingQueue(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}

	/**
	 * Inserts the specified element into this priority queue (if the bounded queue capacity is reached, false will be returned).
	 * @param e the element to add.
	 * @return Returns true if the element was added to this queue, else {@code false}.
	 * @throws ClassCastException if the specified element cannot be compared with elements currently in the priority queue according to the priority queue's ordering.
	 * @throws NullPointerException if the specified element is null.
	 */
	public boolean offer(E e) {
		// In order to achieve optimal performance, no locking processing is required.
		if (this.size() >= queueCapacity) return false;
		return super.offer(e);
	}

}
