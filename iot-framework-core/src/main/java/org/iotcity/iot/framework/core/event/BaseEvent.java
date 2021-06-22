package org.iotcity.iot.framework.core.event;

import java.util.EventObject;

/**
 * Basic event object, used to provide the common event support.
 * @param <T> The class type of event type.
 * @author Ardon
 * @date 2021-04-24
 */
public class BaseEvent<T> extends EventObject implements Event<T> {

	// --------------------------- Private static fields ----------------------------

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	// --------------------------- Private object fields ----------------------------

	/**
	 * The type data of the current event.
	 */
	private final T type;
	/**
	 * Indicates whether the subsequent execution of an event is allowed to be cancelled.
	 */
	private final boolean cancelable;
	/**
	 * Indicates whether the logic after execution of this event has been cancelled.
	 */
	private boolean cancelled;
	/**
	 * Whether the event propagation has stopped.
	 */
	private boolean stopped;
	/**
	 * The system time in milliseconds when the event happened.
	 */
	private final long time;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for a basic event object.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param type The type of data event to listen on (required, not null).
	 * @param cancelable Indicates whether the subsequent execution of an event is allowed to be cancelled.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "source" or "type" is null.
	 */
	public BaseEvent(Object source, T type, boolean cancelable) throws IllegalArgumentException {
		super(source);
		if (type == null) throw new IllegalArgumentException("Parameter type can not be null!");
		this.type = type;
		this.cancelable = cancelable;
		this.time = System.currentTimeMillis();
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public T getType() {
		return type;
	}

	@Override
	public boolean isCancelable() {
		return cancelable;
	}

	@Override
	public void cancelEvent() {
		if (cancelable) cancelled = true;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void stopPropagation() {
		if (!stopped) stopped = true;
	}

	@Override
	public boolean isStopped() {
		return stopped;
	}

	@Override
	public long getEventTime() {
		return time;
	}

}
