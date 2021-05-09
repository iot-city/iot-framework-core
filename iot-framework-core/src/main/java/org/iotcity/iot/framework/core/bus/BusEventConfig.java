package org.iotcity.iot.framework.core.bus;

import org.iotcity.iot.framework.core.event.BaseEventConfig;

/**
 * Bus event configure data.
 * @author ardon
 * @date 2021-05-09
 */
public class BusEventConfig extends BaseEventConfig<Class<?>, BusEvent, BusEventListener> {

	/**
	 * Constructor for bus event configure data.
	 */
	public BusEventConfig() {
	}

	/**
	 * Constructor for bus event configure data.
	 * @param type The event type (required, not null).
	 * @param listener Event listener object (required, not null).
	 * @param priority The execution order priority for the listener (the priority with the highest value is called first, 0 by default).
	 */
	public BusEventConfig(Class<?> type, BusEventListener listener, int priority) {
		super(type, listener, priority);
	}

}
