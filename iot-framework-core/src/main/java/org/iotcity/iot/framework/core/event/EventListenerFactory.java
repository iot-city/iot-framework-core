package org.iotcity.iot.framework.core.event;

/**
 * Event listener factory to get a listener of specified event type.
 * @param <T> The class type of event type.
 * @param <E> The event class type.
 * @param <L> The listener class type.
 * @author Ardon
 * @date 2021-04-24
 */
public interface EventListenerFactory<T, E extends Event<T>, L extends EventListener<T, E>> {

	/**
	 * Gets a listener for specified event type (returns null when no matching listener is found).
	 * @param type The type of data event to listen on.
	 * @return Event listener to process the event data after receiving the event.
	 */
	L getListener(T type);

}
