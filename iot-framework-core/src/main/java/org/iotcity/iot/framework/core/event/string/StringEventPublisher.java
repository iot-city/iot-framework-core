package org.iotcity.iot.framework.core.event.string;

import org.iotcity.iot.framework.core.event.BaseEventPublisher;

/**
 * String event publisher is used to publish string event data processing.
 * @author ardon
 * @date 2021-05-09
 */
public class StringEventPublisher extends BaseEventPublisher<String, StringEvent, StringEventListener, StringEventListenerFactory> {

	@Override
	public StringEventListener getListenerInstanceFromFactory(String type) {
		final StringEventListenerFactory factory = this.getListenerFactory();
		if (type == null || factory == null) {
			return null;
		} else {
			return factory.getListener(type);
		}
	}

}
