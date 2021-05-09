package org.iotcity.iot.framework.core.event;

/**
 * Basic event configure data.
 * @param <T> The class type of event type.
 * @param <E> The event class type.
 * @author ardon
 * @date 2021-05-07
 */
public class BaseEventConfig<T, E extends Event<T>> {

	/**
	 * The event type (required, not null).
	 */
	public T type;
	/**
	 * Event listener object (required, not null).
	 */
	public EventListener<T, E> listener;
	/**
	 * The execution order priority for the listener (the priority with the highest value is called first, 0 by default).
	 */
	public int priority;

	/**
	 * Constructor for event configure data.
	 */
	public BaseEventConfig() {
	}

	/**
	 * Constructor for event configure data.
	 * @param type The event type (required, not null).
	 * @param listener Event listener object (required, not null).
	 * @param priority The execution order priority for the listener (the priority with the highest value is called first, 0 by default).
	 */
	public BaseEventConfig(T type, EventListener<T, E> listener, int priority) {
		this.type = type;
		this.listener = listener;
		this.priority = priority;
	}

}
