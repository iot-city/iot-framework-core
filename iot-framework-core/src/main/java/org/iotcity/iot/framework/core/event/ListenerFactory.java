package org.iotcity.iot.framework.core.event;

/**
 * Event listener factory to get listeners of specified event type.
 * @param <T> The class type of event type.
 * @author Ardon
 * @date 2021-04-24
 */
public interface ListenerFactory<T> {

	/**
	 * Gets listeners for specified event type (returns null when no matching listener is found).
	 * @param type The type of data event to listen on.
	 * @return Event listeners to process the event data after receiving the event.
	 */
	Listener<T>[] getListeners(T type);

}
