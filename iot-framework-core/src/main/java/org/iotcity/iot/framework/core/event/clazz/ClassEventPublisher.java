package org.iotcity.iot.framework.core.event.clazz;

import java.util.List;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.event.BaseEventPublisher;
import org.iotcity.iot.framework.core.event.BaseListenerObject;

/**
 * Class event publisher is used to publish class event data processing.
 * @author ardon
 * @date 2021-05-08
 */
public class ClassEventPublisher extends BaseEventPublisher<Class<?>, ClassEvent, ClassEventListener, ClassEventListenerFactory> {

	/**
	 * Whether including super class when publish an event.
	 */
	private final boolean includingSuperClass;

	/**
	 * Constructor for class event publisher is used to publish class event data processing.
	 * @param publishIncludingSuperClass Whether including super class listeners when publish an event.
	 */
	public ClassEventPublisher(boolean publishIncludingSuperClass) {
		this.includingSuperClass = publishIncludingSuperClass;
	}

	@Override
	public ClassEventListener getListenerInstanceFromFactory(Class<?> type) {
		final ClassEventListenerFactory factory = this.getListenerFactory();
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
	public int publish(ClassEvent event) throws IllegalArgumentException {
		if (includingSuperClass) {
			if (event == null) throw new IllegalArgumentException("Parameter event can not be null!");
			int count = 0;
			List<BaseListenerObject<Class<?>, ClassEvent, ClassEventListener>> listeners = getClassListeners(event.getType());
			for (BaseListenerObject<Class<?>, ClassEvent, ClassEventListener> object : listeners) {
				if (event.isStopped()) break;
				if (object.listener.onEvent(event)) {
					count++;
				}
			}
			return count;
		} else {
			return super.publish(event);
		}
	}

}
