package org.iotcity.iot.framework.core.event.clazz;

import org.iotcity.iot.framework.core.event.EventListenerFactory;

/**
 * Class event listener factory to get a listener of specified class type.
 * @author ardon
 * @date 2021-05-10
 */
public interface ClassEventListenerFactory extends EventListenerFactory<Class<?>, ClassEvent, ClassEventListener> {

}
