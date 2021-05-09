package org.iotcity.iot.framework.core.bus;

import org.iotcity.iot.framework.core.event.EventListener;

/**
 * The bus event listener is used to process the bus event data after receiving the event.
 * @author ardon
 * @date 2021-05-09
 */
public interface BusEventListener extends EventListener<Class<?>, BusEvent> {

}
