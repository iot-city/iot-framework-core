package org.iotcity.iot.framework.core.config;

import org.iotcity.iot.framework.ConfigureHandler;
import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.annotation.AnnotationParser;

/**
 * Configure manager parser for auto configuration in the framework.
 * @author Ardon
 * @date 2021-04-26
 */
public final class ConfigureManagerParser implements AnnotationParser {

	/**
	 * The configure manager handler of framework.
	 */
	private final ConfigureHandler handler;

	/**
	 * Constructor for configure manager parser.
	 */
	public ConfigureManagerParser() {
		handler = IoTFramework.getConfigureHandler();
	}

	@Override
	public void parse(Class<?> clazz) {
		if (clazz.isInterface() || !clazz.isAnnotationPresent(AutoConfigureManager.class)) return;
		if (!ConfigureManager.class.isAssignableFrom(clazz)) return;
		AutoConfigureManager auto = clazz.getAnnotation(AutoConfigureManager.class);
		if (auto == null || !auto.enabled()) return;
		@SuppressWarnings("unchecked")
		Class<? extends ConfigureManager> managerClass = (Class<? extends ConfigureManager>) clazz;
		handler.addManager(managerClass);
	}

}
