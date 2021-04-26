package org.iotcity.iot.framework.core.i18n;

import org.iotcity.iot.framework.core.config.Configurable;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.config.PropertiesConfigure;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;

/**
 * Use this configuration loader to load the localization configuration.<br/>
 * The default external file to load: "framework-i18n.properties".<br/>
 * <b>(Default locale factory template file: "org/iotcity/iot/framework/core/i18n/iot-i18n-template.properties")</b>
 * @author Ardon
 */
public final class LocaleConfigure extends PropertiesConfigure<LocaleConfig[]> {

	@Override
	public String getPrefixKey() {
		return "iot.framework.core.i18n";
	}

	@Override
	public PropertiesConfigFile getDefaultExternalFile() {
		return new PropertiesConfigFile("framework-i18n.properties", "UTF-8", false);
	}

	@Override
	public boolean config(Configurable<LocaleConfig[]> configurable, boolean reset) {
		if (configurable == null || props == null) return false;
		// Get i18n configure (the property key must be "iot.framework.core.i18n")
		LocaleConfig[] configs = PropertiesLoader.getConfigArray(LocaleConfig.class, props, this.getPrefixKey());
		if (configs == null || configs.length == 0) return false;
		// Returns config status
		return configurable.config(configs, reset);
	}

}
