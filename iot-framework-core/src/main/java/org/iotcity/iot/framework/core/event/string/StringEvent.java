package org.iotcity.iot.framework.core.event.string;

import org.iotcity.iot.framework.core.event.BaseEvent;

/**
 * String event object, used to provide the string event support.
 * @author ardon
 * @date 2021-05-09
 */
public class StringEvent extends BaseEvent<String> {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for an event object without event data.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param type The type of data event to listen on (required, not null).
	 * @param cancelable Indicates whether the subsequent execution of an event is allowed to be cancelled.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "source" or "type" is null.
	 */
	public StringEvent(Object source, String type, boolean cancelable) throws IllegalArgumentException {
		super(source, type, null, cancelable);
	}

	/**
	 * Constructor for an event object with event data.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param type The type of data event to listen on (required, not null).
	 * @param data The event data of this event object (optional, it can be set to null when not needed).
	 * @param cancelable Indicates whether the subsequent execution of an event is allowed to be cancelled.
	 * @throws IllegalArgumentException An error will be thrown when the parameter "source" or "type" is null.
	 */
	public StringEvent(Object source, String type, Object data, boolean cancelable) throws IllegalArgumentException {
		super(source, type, data, cancelable);
	}

}
