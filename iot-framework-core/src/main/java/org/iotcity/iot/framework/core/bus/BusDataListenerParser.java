package org.iotcity.iot.framework.core.bus;

import org.iotcity.iot.framework.IoTFramework;
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
	 */
	public BusDataListenerParser() {
		publisher = IoTFramework.getBusEventPublisher();
	}

	@Override
	public void parse(Class<?> clazz) {
		if (clazz.isInterface() || !clazz.isAnnotationPresent(BusDataListener.class)) return;
		// Get annotation
		if (!BusEventListener.class.isAssignableFrom(clazz)) return;
		BusDataListener annotation = clazz.getAnnotation(BusDataListener.class);
		if (annotation == null || !annotation.enabled()) return;
		// Get class type
		Class<?> dataType = annotation.dataType();
		@SuppressWarnings("unchecked")
		BusEventListener listener = publisher.getListener((Class<? extends BusEventListener>) clazz);
		if (dataType == null || listener == null) return;
		// Add to publisher
		publisher.addListener(dataType, listener, annotation.priority());
	}

}
