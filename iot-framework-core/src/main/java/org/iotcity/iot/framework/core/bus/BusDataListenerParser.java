package org.iotcity.iot.framework.core.bus;

import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.annotation.AnnotationParser;

/**
 * Bus data listener parser for data listener annotation.
 * @author ardon
 * @date 2021-05-09
 */
public final class BusDataListenerParser implements AnnotationParser {

	/**
	 * The bus event publisher of the framework (not null).
	 */
	private final BusEventPublisher publisher;

	/**
	 * Constructor for bus data listener parser.
	 * @param publisher The bus event publisher object (required, can not be null).
	 */
	public BusDataListenerParser(BusEventPublisher publisher) {
		this.publisher = publisher;
	}

	@Override
	public void parse(Class<?> clazz) {
		// Filtrate the listener class
		if (clazz.isInterface() || !clazz.isAnnotationPresent(BusDataListener.class)) return;
		if (!BusEventListener.class.isAssignableFrom(clazz)) {
			FrameworkCore.getLogger().warn(FrameworkCore.getLocale().text("core.annotation.interface.warn", BusDataListener.class.getSimpleName(), clazz.getName(), BusEventListener.class.getName()));
			return;
		}

		// Get annotation
		BusDataListener annotation = clazz.getAnnotation(BusDataListener.class);
		if (annotation == null || !annotation.enabled()) return;

		// Get data class type
		Class<?> dataType = annotation.value();
		if (dataType == null) return;

		// Get listener object
		BusEventListener listener = publisher.getListenerInstanceFromFactory(clazz);
		if (listener == null) return;

		// Get filter event
		Class<? extends BusEvent> filterEvent = annotation.filterEvent() == BusEvent.class ? null : annotation.filterEvent();

		// Add to publisher
		publisher.addListener(dataType, listener, annotation.priority(), filterEvent);
	}

}
