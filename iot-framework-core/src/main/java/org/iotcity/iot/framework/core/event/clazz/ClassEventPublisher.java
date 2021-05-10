package org.iotcity.iot.framework.core.event.clazz;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.event.BaseEventPublisher;

/**
 * Class event publisher is used to publish class event data processing.
 * @author ardon
 * @date 2021-05-08
 */
public class ClassEventPublisher extends BaseEventPublisher<Class<?>, ClassEvent, ClassEventListener, ClassEventListenerFactory> {

	@Override
	public ClassEventListener getListenerInstanceFromFactory(Class<?> type) {
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
