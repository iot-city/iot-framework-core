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
	 * @param filterEventClass Specifies the class type of event to listen on (optional, the listener will respond to events of the currently specified event type and inherited subclass types).
	 */
	public ClassEventConfig(Class<?> type, ClassEventListener listener, int priority, Class<? extends ClassEvent> filterEventClass) {
		super(type, listener, priority, filterEventClass);
	}

}
