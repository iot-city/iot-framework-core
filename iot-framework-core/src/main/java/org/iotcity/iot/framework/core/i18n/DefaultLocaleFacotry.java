package org.iotcity.iot.framework.core.i18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.iotcity.iot.framework.core.util.config.PropertiesLoader;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.core.util.helper.SystemHelper;

/**
 * Default locale factory
 * @author Ardon
 */
public class DefaultLocaleFacotry implements LocaleFactory {

	// --------------------------- Private fields ----------------------------

	/**
	 * Default language key (e.g. "en_US")
	 */
	private String defaultLang;
	/**
	 * The text map of current factory, the key is name and language key, the value is the locale text object
	 */
	private final Map<String, LocaleText> texts = new HashMap<>();

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for default locale factory
	 */
	public DefaultLocaleFacotry() {
		this.defaultLang = SystemHelper.getSystemLang();
	}

	/**
	 * Constructor for default locale factory<br/>
	 * <b>(Default locale factory template file: "org/iotcity/iot/framework/core/i18n/iot-i18n-template.properties")</b>
	 * @param configFile The locale configure properties file for this factory (required, not null or empty)
	 * @param fromPackage Whether load the file from package
	 */
	public DefaultLocaleFacotry(String configFile, boolean fromPackage) {
		this.defaultLang = SystemHelper.getSystemLang();
		this.load(configFile, fromPackage);
	}

	// --------------------------- Private methods ----------------------------

	/**
	 * Get a map key for locale text map
	 * @param name The locale name (required, not null or empty, e.g. "CORE")
	 * @param lang Locale text language key (required, not null or empty, e.g. "en_US", "zh_CN")
	 * @return String The map key
	 */
	private static String getKey(String name, String lang) {
		return name.toUpperCase() + "|" + lang.toUpperCase();
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Load locale text resource to locale factory<br/>
	 * <b>(Default locale factory template file: "org/iotcity/iot/framework/core/i18n/iot-i18n-template.properties")</b>
	 * @param configFile The locale configure properties file for this factory (required, not null or empty)
	 * @param fromPackage Whether load the file from package
	 * @return boolean Whether config successfully
	 */
	public boolean load(String configFile, boolean fromPackage) {
		if (StringHelper.isEmpty(configFile)) return false;

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

			// iot.framework.core.i18n.core
			String localeKey = "iot.framework.core.i18n." + locale;
			// iot.framework.core.i18n.core.enabled=true
			boolean enabled = ConvertHelper.toBoolean(props.getProperty(localeKey + ".enabled"), true);
			if (!enabled) continue;
			// iot.framework.core.i18n.core.name=CORE
			String name = props.getProperty(localeKey + ".name", locale);
			// iot.framework.core.i18n.core.langs=en_US, zh_CN
			String langs = props.getProperty(localeKey + ".langs");
			if (langs == null || langs.length() == 0) continue;
			String[] langAry = langs.split("[,;]");
			if (langAry == null || langAry.length == 0) continue;
			// Get each language key
			for (int x = 0, y = langAry.length; x < y; x++) {
				// en_US
				String lang = langAry[x].trim();
				if (lang.length() == 0) continue;
				// iot.framework.core.i18n.core.langs.en_US.fromPackage=true
				boolean frompkg = ConvertHelper.toBoolean(props.getProperty(localeKey + ".langs." + lang + ".fromPackage"), true);
				// iot.framework.core.i18n.core.langs.en_US.file=org/iotcity/iot/framework/core/i18n/i18n-core-en-us.properties
				String file = props.getProperty(localeKey + ".langs." + lang + ".file");
				if (file == null) continue;
				file = file.trim();
				if (file.length() == 0) continue;
				// Load text properties file
				Properties textProps = PropertiesLoader.loadProperties(file, "UTF-8", frompkg);
				// Add text to factory
				this.addLocale(name, lang, new DefaultLocaleText(name, lang, textProps));
			}
		}
		// Returns true
		return true;
	}

	/**
	 * Gets locale configures size
	 * @return int Size
	 */
	public int size() {
		return this.texts.size();
	}

	/**
	 * Add locale text map to factory
	 * @param name The locale name (required, not null or empty, e.g. "CORE")
	 * @param lang Locale text language key (required, not null or empty, e.g. "en_US", "zh_CN")
	 * @param locale The locale text object (not null)
	 */
	public void addLocale(String name, String lang, LocaleText locale) {
		if (StringHelper.isEmpty(name) || StringHelper.isEmpty(lang) || locale == null) return;
		String key = getKey(name, lang);
		synchronized (texts) {
			this.texts.put(key, locale);
		}
	}

	/**
	 * Remove multiple locale text objects from factory (returns null if not found)
	 * @param name The locale name (required, not null or empty, e.g. "CORE")
	 * @return LocaleText The locale text objects that be removed, returns null if not found
	 */
	public LocaleText[] removeLocales(String name) {
		if (StringHelper.isEmpty(name)) return null;
		List<LocaleText> rets = new ArrayList<>();
		synchronized (texts) {
			Iterator<Map.Entry<String, LocaleText>> it = this.texts.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, LocaleText> entry = it.next();
				LocaleText text = entry.getValue();
				if (name.equals(text.getName())) {
					rets.add(text);
					it.remove();
				}
			}
		}
		return rets.toArray(new LocaleText[rets.size()]);
	}

	/**
	 * Remove a locale text object from factory
	 * @param name The locale name (required, not null or empty, e.g. "CORE")
	 * @param lang Locale text language key (required, not null or empty, e.g. "en_US", "zh_CN")
	 * @return LocaleText The locale text object that be removed, returns null if not found
	 */
	public LocaleText removeLocale(String name, String lang) {
		if (StringHelper.isEmpty(name) || StringHelper.isEmpty(lang)) return null;
		String key = getKey(name, lang);
		synchronized (texts) {
			return this.texts.remove(key);
		}
	}

	/**
	 * Clear all locale text objects in factory
	 */
	public void clearLocales() {
		synchronized (texts) {
			this.texts.clear();
		}
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public void setDefaultLang(String lang) {
		if (StringHelper.isEmpty(lang)) return;
		this.defaultLang = lang;
	}

	@Override
	public String getDefaultLang() {
		return defaultLang;
	}

	@Override
	public LocaleText getDefaultLocale(String name) {
		if (StringHelper.isEmpty(name)) return new DefaultLocaleText(null, defaultLang, null);
		LocaleText text = this.texts.get(getKey(name, defaultLang));
		return text == null ? new DefaultLocaleText(name, defaultLang, null) : text;
	}

	@Override
	public LocaleText getLocale(String name, String lang) {
		if (StringHelper.isEmpty(name) || StringHelper.isEmpty(lang)) {
			return new DefaultLocaleText(name, lang, null);
		}
		LocaleText text = this.texts.get(getKey(name, lang));
		if (text == null) text = this.texts.get(getKey(name, defaultLang));
		return text == null ? new DefaultLocaleText(name, lang, null) : text;
	}

}
