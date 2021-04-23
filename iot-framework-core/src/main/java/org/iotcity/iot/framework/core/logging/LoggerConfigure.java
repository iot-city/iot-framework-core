package org.iotcity.iot.framework.core.logging;

import org.iotcity.iot.framework.core.config.Configurable;
import org.iotcity.iot.framework.core.config.PropertiesConfigure;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;

/**
 * Use this configuration loader to load the logger configuration.<br/>
 * <b>(Default configure template file: "org/iotcity/iot/framework/core/logging/iot-logger-template.properties")</b>
 * @author Ardon
 */
public class LoggerConfigure extends PropertiesConfigure<LoggerConfig[]> {

	/**
	 * Constructor for automatic properties configuration object..<br/>
	 * <b>(Default configure template file: "org/iotcity/iot/framework/core/logging/iot-logger-template.properties")</b>
	 * @param configFile The configure properties file to load (required, not null or empty).
	 * @param fromPackage Whether load the file from package.
	 * @throws IllegalArgumentException An error is thrown when the parameter is null
	 */
	public LoggerConfigure(String configFile, boolean fromPackage) {
		super(configFile, fromPackage);
	}

	/**
	 * Constructor for automatic properties configuration object..<br/>
	 * <b>(Default configure template file: "org/iotcity/iot/framework/core/logging/iot-logger-template.properties")</b>
	 * @param configFile The configure properties file to load (required, not null or empty).
	 * @param encoding Text encoding (optional, if it is set to null, it will be judged automatically).
	 * @param fromPackage Whether load the file from package.
	 * @throws IllegalArgumentException An error is thrown when the parameter is null
	 */
	public LoggerConfigure(String configFile, String encoding, boolean fromPackage) {
		super(configFile, encoding, fromPackage);
	}

	@Override
	public boolean config(Configurable<LoggerConfig[]> configurable, boolean reset) {
		if (configurable == null) return false;
		// Get logging configure (the property key must be "iot.framework.core.logging")
		LoggerConfig[] configs = PropertiesLoader.getConfigArray(LoggerConfig.class, props, "iot.framework.core.logging");
		if (configs == null || configs.length == 0) return false;
		// Config factory
		return configurable.config(configs, reset);
	}

}
