package org.iotcity.iot.framework.core.event.clazz;

import org.iotcity.iot.framework.core.event.BaseEvent;

/**
 * Class event object, used to provide the class event support.
 * @author ardon
 * @date 2021-05-08
 */
public class ClassEvent extends BaseEvent<Class<?>> {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for an event object with event data.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param data The event data of this class event object (required, not null).
	 * @param cancelable Indicates whether the subsequent execution of an event is allowed to be cancelled.
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "data" is null.
	 */
	public ClassEvent(Object source, Object data, boolean cancelable) throws IllegalArgumentException {
		super(source, data == null ? null : data.getClass(), data, cancelable);
	}

}
