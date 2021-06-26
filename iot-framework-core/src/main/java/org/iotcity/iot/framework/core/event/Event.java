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
	 * Gets the system time in milliseconds when the event happened.
	 * @return The event time.
	 */
	long getEventTime();

	/**
	 * The type data of the current event.
	 * @return Event type data.
	 */
	T getEventType();

	/**
	 * Gets data of this event object (optional, it can be null value when not needed).
	 * @param <V> The class type of event data.
	 */
	<V> V getEventData();

	/**
	 * Add the number of times this event was successfully executed.
	 */
	void addExecutionCount();

	/**
	 * Gets the number of times this event was successfully executed.
	 */
	int getExecutionCount();

	/**
	 * Add an exception encountered during the execution of the event.
	 * @param e The exception object.
	 */
	void addException(Exception e);

	/**
	 * Indicates whether an exception was encountered during event execution.
	 */
	boolean hasException();

	/**
	 * Gets the exceptions encountered during the execution of the event (returns null if there is no exception in this event).
	 * @return The exception array or null.
	 */
	Exception[] getExceptions();

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

}
