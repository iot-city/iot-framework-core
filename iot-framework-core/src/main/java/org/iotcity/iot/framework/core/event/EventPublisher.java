package org.iotcity.iot.framework.core.event;

/**
 * The event publisher interface is used to publish event processing.
 * @param <T> The class type of event type.
 * @param <E> The event class type.
 * @author Ardon
 * @date 2021-04-24
 */
public interface EventPublisher<T, E extends Event<T>> {

	/**
	 * Notifies all listeners that match the current event to execute this event.
	 * @param event The event object that needs to be published (required, not null).
	 * @return The event object that has been executed (returns not null).
	 * @throws IllegalArgumentException An error will be thrown when parameter "event" is null.
	 */
	E publish(E event) throws IllegalArgumentException;

}
