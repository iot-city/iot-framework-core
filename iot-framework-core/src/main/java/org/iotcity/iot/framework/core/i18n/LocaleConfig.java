package org.iotcity.iot.framework.core.i18n;

import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.util.config.PropertiesMap;

/**
 * Locale configure data.
 * @author Ardon
 */
public class LocaleConfig {

	/**
	 * The locale text name (e.g. "CORE").
	 */
	public String name;
	/**
	 * Whether enable this configuration.
	 */
	public boolean enabled;
	/**
	 * Default language key for this locale (e.g. "en_US").
	 */
	public String defaultLang;
	/**
	 * Locale configuration files for languages.
	 */
	public PropertiesMap<PropertiesConfigFile> files;

}
