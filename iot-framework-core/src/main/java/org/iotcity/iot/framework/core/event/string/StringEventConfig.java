package org.iotcity.iot.framework.core.event.string;

import org.iotcity.iot.framework.core.event.BaseEventConfig;

/**
 * String event configure data.
 * @author ardon
 * @date 2021-05-09
 */
public class StringEventConfig extends BaseEventConfig<String, StringEvent, StringEventListener> {

	/**
	 * Constructor for string configure data.
	 */
	public StringEventConfig() {
	}

	/**
	 * Constructor for string event configure data.
	 * @param type The event type (required, not null).
	 * @param listener Event listener object (required, not null).
	 * @param priority The execution order priority for the listener (the priority with the highest value is called first, 0 by default).
	 */
	public StringEventConfig(String type, StringEventListener listener, int priority) {
		super(type, listener, priority);
	}

}
