package org.iotcity.iot.framework.core.bus;

import java.util.List;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.event.BaseEventPublisher;
import org.iotcity.iot.framework.core.event.BaseListenerObject;

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
		int count = 0;
		Class<?> eventClass = event.getClass();
		List<BaseListenerObject<Class<?>, BusEvent, BusEventListener>> listeners = getClassListeners(event.getType());
		for (BaseListenerObject<Class<?>, BusEvent, BusEventListener> object : listeners) {
			if (event.isStopped()) break;
			if (object.filterEventClass != null && !object.filterEventClass.isAssignableFrom(eventClass)) {
				continue;
			}
			if (object.listener.onEvent(event)) {
				count++;
			}
		}
		return count;
	}

}
