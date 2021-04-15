package org.iotcity.iot.framework.core.i18n;

import java.util.Properties;

import org.iotcity.iot.framework.core.config.PropertiesConfigure;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Use this configuration loader to load the default localization configuration.<br/>
 * <b>(Default locale factory template file: "org/iotcity/iot/framework/core/i18n/iot-i18n-template.properties")</b>
 * @author Ardon
 */
public final class DefaultLocaleConfigure implements PropertiesConfigure<DefaultLocaleFacotry> {

	/**
	 * Load locale text resource to locale factory.<br/>
	 * <b>(Default locale factory template file: "org/iotcity/iot/framework/core/i18n/iot-i18n-template.properties")</b>
	 */
	@Override
	public boolean config(DefaultLocaleFacotry configurable, String configFile, boolean fromPackage) {
		// Parameters verification
		if (configurable == null || StringHelper.isEmpty(configFile)) return false;

		// Load file properties
		Properties props = PropertiesLoader.loadProperties(configFile, "UTF-8", fromPackage);
		if (props == null) return false;

		// Get i18n configure keys (e.g. "core", "actor")
		String locales = props.getProperty("iot.framework.core.i18n");
		if (locales == null || locales.length() == 0) return false;
		String[] keys = locales.split("[,;]");
		if (keys == null || keys.length == 0) return false;

		// Traverse all locale configurations
		for (int i = 0, c = keys.length; i < c; i++) {
			// (e.g. "core", "actor")
			String locale = keys[i].trim();
			if (locale.length() == 0) continue;

			// iot.framework.core.i18n.xxxx
			String localeKey = "iot.framework.core.i18n." + locale;
			// iot.framework.core.i18n.core.enabled=true
			boolean enabled = ConvertHelper.toBoolean(props.getProperty(localeKey + ".enabled"), true);
			if (!enabled) continue;

			// iot.framework.core.i18n.xxxx.name=CORE
			String name = props.getProperty(localeKey + ".name", locale);
			if (StringHelper.isEmpty(name)) name = locale;
			// Get default language key: iot.framework.core.i18n.xxxx.default
			String defaultLang = props.getProperty(localeKey + ".default", null);
			// Set default language key
			configurable.setDefaultLangKey(name, defaultLang);

			// iot.framework.core.i18n.xxxx.langs=en_US, zh_CN
			String langs = props.getProperty(localeKey + ".langs");
			if (langs == null || langs.length() == 0) continue;
			String[] langAry = langs.split("[,;]");
			if (langAry == null || langAry.length == 0) continue;
			// Get each language key
			for (int x = 0, y = langAry.length; x < y; x++) {
				// en_US
				String lang = langAry[x].trim();
				if (lang.length() == 0) continue;
				// iot.framework.core.i18n.xxxx.langs.en_US.fromPackage=true
				boolean frompkg = ConvertHelper.toBoolean(props.getProperty(localeKey + ".langs." + lang + ".fromPackage"), true);
				// iot.framework.core.i18n.xxxx.langs.en_US.file=org/iotcity/iot/framework/core/i18n/i18n-core-en-us.properties
				String file = props.getProperty(localeKey + ".langs." + lang + ".file");
				if (file == null) continue;
				file = file.trim();
				if (file.length() == 0) continue;
				// Load text properties file
				Properties texts = PropertiesLoader.loadProperties(file, "UTF-8", frompkg);
				// Add text to factory
				configurable.addLocale(name, lang, new DefaultLocaleText(name, lang, texts));
			}
		}
		// Returns status
		return true;
	}

}
