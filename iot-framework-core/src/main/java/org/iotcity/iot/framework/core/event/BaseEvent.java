package org.iotcity.iot.framework.core.event;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

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

	// --------------------------- Protected object fields ----------------------------

	/**
	 * The type data of the current event.
	 */
	protected final T type;
	/**
	 * The data of this event object (optional, it can be null value when not needed).
	 */
	protected final Object data;

	// --------------------------- Private object fields ----------------------------

	/**
	 * The exception list.
	 */
	private final List<Exception> exceptions = new ArrayList<>(1);
	/**
	 * The number of times this event was executed.
	 */
	private int executions;
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
	 * @param data The event data of this event object (optional, it can be set to null when not needed).
	 * @param cancelable Indicates whether the subsequent execution of an event is allowed to be cancelled.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "source" or "type" is null.
	 */
	public BaseEvent(Object source, T type, Object data, boolean cancelable) throws IllegalArgumentException {
		super(source);
		if (type == null) throw new IllegalArgumentException("Parameter type can not be null!");
		this.type = type;
		this.data = data;
		this.cancelable = cancelable;
		this.time = System.currentTimeMillis();
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public final Object getSource() {
		return source;
	}

	@Override
	public final long getEventTime() {
		return time;
	}

	@Override
	public final T getEventType() {
		return type;
	}

	@Override
	public final <V> V getEventData() {
		@SuppressWarnings("unchecked")
		V tdata = (V) data;
		return tdata;
	}

	@Override
	public final void addExecutionCount() {
		executions++;
	}

	@Override
	public final int getExecutionCount() {
		return executions;
	}

	@Override
	public final void addException(Exception e) {
		exceptions.add(e);
	}

	@Override
	public final boolean hasException() {
		return exceptions.size() > 0;
	}

	@Override
	public final Exception[] getExceptions() {
		return exceptions.size() == 0 ? null : exceptions.toArray(new Exception[exceptions.size()]);
	}

	@Override
	public final boolean isCancelable() {
		return cancelable;
	}

	@Override
	public final void cancelEvent() {
		if (cancelable) cancelled = true;
	}

	@Override
	public final boolean isCancelled() {
		return cancelled;
	}

	@Override
	public final void stopPropagation() {
		if (!stopped) stopped = true;
	}

	@Override
	public final boolean isStopped() {
		return stopped;
	}

}
