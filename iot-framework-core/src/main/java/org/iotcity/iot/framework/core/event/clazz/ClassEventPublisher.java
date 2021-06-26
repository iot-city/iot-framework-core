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
	 * Indicates whether to make all subclasses respond to events.
	 */
	private final boolean superClassEvents;

	/**
	 * Constructor for class event publisher is used to publish class event data processing.
	 * @param superClassEvents Indicates whether the listener will respond to events of the currently specified data type and inherited subclass types.
	 */
	public ClassEventPublisher(boolean superClassEvents) {
		this.superClassEvents = superClassEvents;
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
	public ClassEvent publish(ClassEvent event) throws IllegalArgumentException {
		if (superClassEvents) {
			if (event == null) throw new IllegalArgumentException("Parameter event can not be null!");
			Class<?> eventClass = event.getClass();
			List<BaseListenerObject<Class<?>, ClassEvent, ClassEventListener>> listeners = getClassListeners(event.getEventType());
			for (BaseListenerObject<Class<?>, ClassEvent, ClassEventListener> object : listeners) {
				if (event.isStopped()) break;
				if (object.filterEventClass != null && !object.filterEventClass.isAssignableFrom(eventClass)) {
					continue;
				}
				try {
					if (object.listener.onEvent(event)) {
						event.addExecutionCount();
					}
				} catch (Exception e) {
					event.addException(e);
					FrameworkCore.getLogger().error(FrameworkCore.getLocale().text("core.event.publish.err", this.getClass().getName(), e.getMessage()), e);
				}
			}
			return event;
		} else {
			return super.publish(event);
		}
	}

}
