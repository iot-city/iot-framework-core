package org.iotcity.iot.framework.core.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.iotcity.iot.framework.core.config.Configurable;
import org.iotcity.iot.framework.core.config.PropertiesConfigure;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

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

		// Get i18n configure keys (e.g. "core", "actor")
		String locales = props.getProperty("iot.framework.core.i18n");
		if (locales == null || locales.length() == 0) return false;
		String[] keys = locales.split("[,;]");
		if (keys == null || keys.length == 0) return false;

		// Create list
		List<LocaleConfig> list = new ArrayList<>();
		// Traverse all locale configurations
		for (int i = 0, c = keys.length; i < c; i++) {
			// (e.g. "core", "actor")
			String locale = keys[i].trim();
			if (locale.length() == 0) continue;

			// iot.framework.core.i18n.xxxx
			String localeKey = "iot.framework.core.i18n." + locale;

			// iot.framework.core.i18n.xxxx.name=CORE
			String name = props.getProperty(localeKey + ".name", locale);
			if (StringHelper.isEmpty(name)) name = locale;
			// iot.framework.core.i18n.core.enabled=true
			boolean enabled = ConvertHelper.toBoolean(props.getProperty(localeKey + ".enabled"), true);
			// Get default language key: iot.framework.core.i18n.xxxx.default
			String defaultLang = props.getProperty(localeKey + ".default", null);

			// Create configure data
			LocaleConfig config = new LocaleConfig();
			config.name = name;
			config.enabled = enabled;
			config.defaultLang = defaultLang;
			// Add to list
			list.add(config);

			// Skip disable locale
			if (!enabled) continue;

			// iot.framework.core.i18n.xxxx.langs=en_US, zh_CN
			String langs = props.getProperty(localeKey + ".langs");
			if (langs == null || langs.length() == 0) continue;
			String[] langAry = langs.split("[,;]");
			if (langAry == null || langAry.length == 0) continue;
			// Create text list
			List<LocaleConfigText> textList = new ArrayList<>();
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
				// Create text configure
				LocaleConfigText configText = new LocaleConfigText();
				configText.lang = lang;
				configText.texts = texts;
				// Add to list
				textList.add(configText);
			}
			// Set to locale languages
			config.langs = textList.toArray(new LocaleConfigText[textList.size()]);
		}
		// Returns config status
		return configurable.config(list.toArray(new LocaleConfig[list.size()]), reset);
	}

}
