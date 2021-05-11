package org.iotcity.iot.framework.core.bus;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.event.BaseEventPublisher;

/**
 * Bus event publisher is used to publish bus event data processing.
 * @author ardon
 * @date 2021-05-09
 */
public final class BusEventPublisher extends BaseEventPublisher<Class<?>, BusEvent, BusEventListener, BusEventListenerFactory> {

	@Override
	public BusEventListener getListenerInstanceFromFactory(Class<?> type) {
		final BusEventListenerFactory factory = this.getListenerFactory();
		if (type == null) {
			return null;
		} else if (factory == null) {
			try {
				return IoTFramework.getGlobalInstanceFactory().getInstance(type);
			} catch (Exception e) {
				FrameworkCore.getLogger().error(FrameworkCore.getLocale().text("core.global.instance.error", type.getName(), e.getMessage()), e);
				return null;
			}
		} else {
			return factory.getListener(type);
		}
	}

	@Override
	public int publish(BusEvent event) throws IllegalArgumentException {
		if (event == null) throw new IllegalArgumentException("Parameter event can not be null!");
		Class<?> type = event.getType();
		int count = 0;
		while (type != null) {
			// Publish event with specified type.
			count += publishType(type, event);
			// Check event status.
			if (event.isStopped()) break;
			// Publish event to all super class listeners.
			type = type.getSuperclass();
		}
		return count;
	}

}
