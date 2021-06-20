package org.iotcity.iot.framework.core.event;

/**
 * Event listener object class for container.
 * @author ardon
 * @date 2021-05-12
 */
public class BaseListenerObject<T, E extends Event<T>, L extends EventListener<T, E>> {

	/**
	 * Event listener instance (not null).
	 */
	public final L listener;
	/**
	 * The execution order priority for the listener (optional, the priority with the highest value is called first, 0 by default).
	 */
	public final int priority;
	/**
	 * The class type of event to listen on (optional, the listener will respond to events of the currently specified event type and inherited subclass types).
	 */
	public final Class<? extends E> filterEventClass;

	/**
	 * Constructor for listener object class.
	 * @param listener Event listener instance.
	 * @param priority The execution order priority for the listener (the priority with the highest value is called first, 0 by default).
	 * @param filterEventClass Specifies the class type of event to listen on (optional, the listener will respond to events of the currently specified event type and inherited subclass types).
	 */
	BaseListenerObject(L listener, int priority, Class<? extends E> filterEventClass) {
		this.listener = listener;
		this.priority = priority;
		this.filterEventClass = filterEventClass;
	}

}
