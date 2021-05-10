package org.iotcity.iot.framework.core.bus;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.event.BaseEventPublisher;

/**
 * Bus event publisher is used to publish bus event data processing.
 * @author ardon
 * @date 2021-05-09
 */
public final class BusEventPublisher extends BaseEventPublisher<Class<?>, BusEvent, BusEventListener, BusEventListenerFactory> {

	/**
	 * Gets a listener for specified event data class type (returns null when no matching listener is found).
	 * @param <T> Bus event listener type.
	 * @param type The listener class type to listen on.
	 * @return Bus event listener to process the event data after receiving the event.
	 */
	public <T extends BusEventListener> BusEventListener getListener(Class<T> type) {
		final BusEventListenerFactory factory = this.getListenerFactory();
		if (type == null) {
			return null;
		} else if (factory == null) {
			return IoTFramework.getGlobalInstanceFactory().getInstance(type);
		} else {
			return factory.getListener(type);
		}
	}

}
