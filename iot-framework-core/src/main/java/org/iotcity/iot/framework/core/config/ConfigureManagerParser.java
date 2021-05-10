package org.iotcity.iot.framework.core.config;

import org.iotcity.iot.framework.ConfigureHandler;
import org.iotcity.iot.framework.core.FrameworkCore;
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
	 * @param handler The configure manager handler (required, can not be null).
	 */
	public ConfigureManagerParser(ConfigureHandler handler) {
		this.handler = handler;
	}

	@Override
	public void parse(Class<?> clazz) {
		// Filtrate the manager class
		if (clazz.isInterface() || !clazz.isAnnotationPresent(AutoConfigureManager.class)) return;
		if (!ConfigureManager.class.isAssignableFrom(clazz)) {
			FrameworkCore.getLogger().warn(FrameworkCore.getLocale().text("core.annotation.interface.warn", AutoConfigureManager.class.getSimpleName(), clazz.getName(), ConfigureManager.class.getName()));
			return;
		}
		// Get annotation
		AutoConfigureManager auto = clazz.getAnnotation(AutoConfigureManager.class);
		if (auto == null || !auto.enabled()) return;
		// Add to handler
		@SuppressWarnings("unchecked")
		Class<? extends ConfigureManager> managerClass = (Class<? extends ConfigureManager>) clazz;
		handler.addManager(managerClass);
	}

}
