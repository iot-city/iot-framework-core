package org.iotcity.iot.framework.core.event;

/**
 * The event interface is used to provide common support for all events.
 * @param <T> The class type of event type.
 * @author Ardon
 * @date 2021-04-24
 */
public interface Event<T> {

	/**
	 * The object on which the Event initially occurred.
	 * @return The object on which the Event initially occurred.
	 */
	Object getSource();

	/**
	 * The type data of the current event.
	 * @return Event type data.
	 */
	T getType();

	/**
	 * Indicates whether the subsequent execution of this event is allowed to be cancelled.
	 */
	boolean isCancelable();

	/**
	 * Cancels subsequent execution corresponding to this event.
	 */
	void cancelEvent();

	/**
	 * Indicates whether the logic after execution of this event has been cancelled.
	 */
	boolean isCancelled();

	/**
	 * Stop the event propagation immediately.
	 */
	void stopPropagation();

	/**
	 * Whether the event propagation has stopped.
	 * @return Returns true if this event has been stopped; otherwise, returns false.
	 */
	boolean isStopped();

	/**
	 * Gets the system time in milliseconds when the event happened.
	 * @return The event time.
	 */
	long getEventTime();

}
