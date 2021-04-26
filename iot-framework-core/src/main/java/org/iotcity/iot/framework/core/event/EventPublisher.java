package org.iotcity.iot.framework.core.event;

/**
 * The event publisher interface is used to publish event processing.
 * @param <T> The class type of event type.
 * @author Ardon
 * @date 2021-04-24
 */
public interface EventPublisher<T> {

	/**
	 * Notifies all listeners that match the current event to execute this event.
	 * @param event The event object that needs to be published (required, not null).
	 * @return Number of successful execution of this event (the return value is 0 when no event is executed).
	 */
	int publish(Event<T> event);

}
