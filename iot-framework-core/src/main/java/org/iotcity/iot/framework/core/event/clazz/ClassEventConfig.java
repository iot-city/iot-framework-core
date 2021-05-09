package org.iotcity.iot.framework.core.event.clazz;

import org.iotcity.iot.framework.core.event.BaseEventConfig;

/**
 * Class event configure data.
 * @author ardon
 * @date 2021-05-09
 */
public class ClassEventConfig extends BaseEventConfig<Class<?>, ClassEvent, ClassEventListener> {

	/**
	 * Constructor for class configure data.
	 */
	public ClassEventConfig() {
	}

	/**
	 * Constructor for class event configure data.
	 * @param type The event type (required, not null).
	 * @param listener Event listener object (required, not null).
	 * @param priority The execution order priority for the listener (the priority with the highest value is called first, 0 by default).
	 */
	public ClassEventConfig(Class<?> type, ClassEventListener listener, int priority) {
		super(type, listener, priority);
	}

}
