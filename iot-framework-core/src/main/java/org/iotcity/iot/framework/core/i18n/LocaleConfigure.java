package org.iotcity.iot.framework.core.i18n;

import org.iotcity.iot.framework.core.config.Configurable;
import org.iotcity.iot.framework.core.config.PropertiesConfigure;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;

/**
 * Use this configuration loader to load the localization configuration.<br/>
 * <b>(Default locale factory template file: "org/iotcity/iot/framework/core/i18n/iot-i18n-template.properties")</b>
 * @author Ardon
 */
public final class LocaleConfigure extends PropertiesConfigure<LocaleConfig[]> {

	/**
	 * Constructor for automatic properties configuration object.<br/>
	 * <b>(Default locale factory template file: "org/iotcity/iot/framework/core/i18n/iot-i18n-template.properties")</b>
	 * @param configFile The configure properties file to load (required, not null or empty).
	 * @param fromPackage Whether load the file from package.
	 * @throws IllegalArgumentException An error is thrown when the parameter is null
	 */
	public LocaleConfigure(String configFile, boolean fromPackage) {
		super(configFile, fromPackage);
	}

	/**
	 * Constructor for automatic properties configuration object.<br/>
	 * <b>(Default locale factory template file: "org/iotcity/iot/framework/core/i18n/iot-i18n-template.properties")</b>
	 * @param configFile The configure properties file to load (required, not null or empty).
	 * @param encoding Text encoding (optional, if it is set to null, it will be judged automatically).
	 * @param fromPackage Whether load the file from package.
	 * @throws IllegalArgumentException An error is thrown when the parameter is null
	 */
	public LocaleConfigure(String configFile, String encoding, boolean fromPackage) {
		super(configFile, encoding, fromPackage);
	}

	@Override
	public boolean config(Configurable<LocaleConfig[]> configurable, boolean reset) {
		if (configurable == null) return false;
		// Get i18n configure (the property key must be "iot.framework.core.i18n")
		LocaleConfig[] configs = PropertiesLoader.getConfigArray(LocaleConfig.class, props, "iot.framework.core.i18n");
		if (configs == null || configs.length == 0) return false;
		// Returns config status
		return configurable.config(configs, reset);
	}

}
