package org.iotcity.iot.framework.core.event;

/**
 * The event listener is used to process the event data after receiving the event.
 * @param <T> The class type of event type.
 * @param <E> The event class type.
 * @author Ardon
 * @date 2021-04-24
 */
public interface EventListener<T, E extends Event<T>> extends java.util.EventListener {

	/**
	 * Processing when data events are received.
	 * @param event Event object (not null).
	 * @return Returns true if the event has been executed successfully; otherwise, returns false.
	 */
	boolean onEvent(E event);

}
