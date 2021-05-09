package org.iotcity.iot.framework.core.event.string;

import org.iotcity.iot.framework.core.event.EventListenerFactory;

/**
 * String event listener factory to get a listener of specified string value.
 * @author ardon
 * @date 2021-05-10
 */
public interface StringEventListenerFactory extends EventListenerFactory<String, StringEvent, StringEventListener> {

}
