package org.iotcity.iot.framework.core;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.config.PropertiesConfigureManager;
import org.iotcity.iot.framework.core.i18n.LocaleConfigure;
import org.iotcity.iot.framework.core.logging.LoggerConfigure;

/**
 * Configure manager of framework core.
 * @author Ardon
 * @date 2021-04-25
 */
public class CoreConfigureManager extends PropertiesConfigureManager {

	/**
	 * Constructor for configure manager of framework core.
	 */
	public CoreConfigureManager() {

		// For internal i18n locale configure
		PropertiesConfigFile file = new PropertiesConfigFile();
		file.file = "org/iotcity/iot/framework/core/resources/i18n-core-config.properties";
		file.fromPackage = true;
		this.addInternal(new LocaleConfigure(), IoTFramework.getLocaleFactory(), file, false);
		// For external i18n locale configure
		this.addExternal(new LocaleConfigure(), IoTFramework.getLocaleFactory(), false);

		// For internal logging configure
		file = new PropertiesConfigFile();
		file.file = "org/iotcity/iot/framework/core/resources/logging-core-config.properties";
		file.fromPackage = true;
		this.addInternal(new LoggerConfigure(), IoTFramework.getLoggerFactory(), file, false);
		// For external logging configure
		this.addExternal(new LoggerConfigure(), IoTFramework.getLoggerFactory(), false);

	}

}
