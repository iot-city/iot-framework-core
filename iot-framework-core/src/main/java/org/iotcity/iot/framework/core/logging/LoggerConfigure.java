package org.iotcity.iot.framework.core.logging;

import org.iotcity.iot.framework.core.config.Configurable;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.config.PropertiesConfigure;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;

/**
 * Use this configuration loader to load the logger configuration.<br/>
 * The default external file to load: "framework-logging.properties".<br/>
 * <b>(Default configure template file: "org/iotcity/iot/framework/core/logging/iot-logger-template.properties")</b>
 * @author Ardon
 */
public class LoggerConfigure extends PropertiesConfigure<LoggerConfig[]> {

	@Override
	public String getPrefixKey() {
		return "iot.framework.core.logging";
	}

	@Override
	public PropertiesConfigFile getDefaultExternalFile() {
		return new PropertiesConfigFile("framework-logging.properties", "UTF-8", false);
	}

	@Override
	public boolean config(Configurable<LoggerConfig[]> configurable, boolean reset) {
		if (configurable == null || props == null) return false;
		// Get logging configure (the property key must be "iot.framework.core.logging")
		LoggerConfig[] configs = PropertiesLoader.getConfigArray(LoggerConfig.class, props, this.getPrefixKey());
		if (configs == null || configs.length == 0) return false;
		// Config factory
		return configurable.config(configs, reset);
	}

}
