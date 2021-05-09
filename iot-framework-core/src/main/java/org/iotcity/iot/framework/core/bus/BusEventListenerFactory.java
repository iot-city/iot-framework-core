package org.iotcity.iot.framework.core.bus;

import org.iotcity.iot.framework.core.event.EventListenerFactory;

/**
 * Bus event listener factory to get a listener of specified listener class type.
 * @author ardon
 * @date 2021-05-09
 */
public interface BusEventListenerFactory extends EventListenerFactory<Class<?>, BusEvent, BusEventListener> {

}
