package org.iotcity.iot.framework.core.event;

import java.util.EventListener;

/**
 * The event listener is used to process the event data after receiving the event.
 * @param <T> The class type of event type.
 * @author Ardon
 * @date 2021-04-24
 */
public interface Listener<T> extends EventListener {

	/**
	 * Processing when data events are received.
	 * @param event Event object (required, not null).
	 * @return Whether the event has been executed in listener.
	 */
	boolean onEvent(Event<T> event);

}
