package org.iotcity.iot.framework.core.event.clazz;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.event.BaseEventPublisher;

/**
 * Class event publisher is used to publish class event data processing.
 * @author ardon
 * @date 2021-05-08
 */
public class ClassEventPublisher extends BaseEventPublisher<Class<?>, ClassEvent, ClassEventListener, ClassEventListenerFactory> {

	/**
	 * Gets a listener for specified event data class type (returns null when no matching listener is found).
	 * @param <T> Class event listener type.
	 * @param type The listener class type to listen on.
	 * @return Class event listener to process the event data after receiving the event.
	 */
	public <T extends ClassEventListener> ClassEventListener getListener(Class<T> type) {
		final ClassEventListenerFactory factory = this.getListenerFactory();
		if (type == null) {
			return null;
		} else if (factory == null) {
			return IoTFramework.getGlobalInstanceFactory().getInstance(type);
		} else {
			return factory.getListener(type);
		}
	}

}
