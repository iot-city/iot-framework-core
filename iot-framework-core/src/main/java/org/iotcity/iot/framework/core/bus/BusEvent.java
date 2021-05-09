package org.iotcity.iot.framework.core.bus;

import org.iotcity.iot.framework.core.event.BaseEvent;

/**
 * Bus event object, used to provide the bus event support.
 * @author ardon
 * @date 2021-05-09
 */
public class BusEvent extends BaseEvent<Class<?>> {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The data of this event object (required, not null).
	 */
	private final Object data;

	/**
	 * Constructor for bus event object, used to provide the bus event support.
	 * @param source The object on which the Event initially occurred (required, not null).
	 * @param data The event data of this bus event object (required, not null).
	 * @throws IllegalArgumentException An error will be thrown when one of the parameters "source", "type" or "data" is null.
	 */
	public BusEvent(Object source, Object data) throws IllegalArgumentException {
		super(source, data == null ? null : data.getClass());
		this.data = data;
	}

	/**
	 * Gets data of this event object (returns not null).
	 * @param <T> The class type of event data.
	 */
	public <T> T getData() {
		@SuppressWarnings("unchecked")
		T tdata = (T) data;
		return tdata;
	}

}
