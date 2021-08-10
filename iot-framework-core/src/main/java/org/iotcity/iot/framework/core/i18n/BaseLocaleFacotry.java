package org.iotcity.iot.framework.core.i18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;
import org.iotcity.iot.framework.core.util.config.PropertiesMap;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.core.util.helper.SystemHelper;

/**
 * The base locale factory object, used to provide the common locale factory support.
 * @author ardon
 * @date 2021-08-10
 */
public abstract class BaseLocaleFacotry implements LocaleFactory {

	// --------------------------- Private fields ----------------------------

	/**
	 * Global language key (e.g. "en_US").
	 */
	protected String globalLang;
	/**
	 * The language keys map of current factory, the key is upper case name, the value is language key.
	 */
	protected final Map<String, String> langs = new HashMap<>();
	/**
	 * The text map of current factory, the key is name and language key, the value is the locale text object.
	 */
	protected final Map<String, LocaleText> texts = new HashMap<>();

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for default locale factory
	 */
	protected BaseLocaleFacotry() {
		this.globalLang = SystemHelper.getSystemLang();
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets locale configures size
	 * @return int Size
	 */
	public final int size() {
		return this.texts.size();
	}

	/**
	 * Add locale text map to factory
	 * @param name The locale name (required, not null or empty, e.g. "CORE")
	 * @param lang Locale text language key (required, not null or empty, e.g. "en_US", "zh_CN")
	 * @param locale The locale text object (not null)
	 */
	public final void addLocale(String name, String lang, LocaleText locale) {
		if (StringHelper.isEmpty(name) || StringHelper.isEmpty(lang) || locale == null) return;
		String key = getKey(name, lang);
		synchronized (texts) {
			this.texts.put(key, locale);
		}
	}

	/**
	 * Returns true if this factory contains the locale name.
	 * @param name The locale name (required, not null or empty, e.g. "CORE")
	 * @return Returns true if this factor contains the locale name; otherwise, returns false.
	 */
	public final boolean containsLocale(String name) {
		if (StringHelper.isEmpty(name)) return false;
		if (this.langs.containsKey(name.toUpperCase())) return true;
		synchronized (texts) {
			for (LocaleText text : this.texts.values()) {
				if (name.equalsIgnoreCase(text.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns true if this factory contains the locale name and language key.
	 * @param name The locale name (required, not null or empty, e.g. "CORE")
	 * @param lang Locale text language key (required, not null or empty, e.g. "en_US", "zh_CN")
	 * @return Returns true if this factor contains the locale name; otherwise, returns false.
	 */
	public final boolean containsLocale(String name, String lang) {
		if (StringHelper.isEmpty(name) || StringHelper.isEmpty(lang)) return false;
		String key = getKey(name, lang);
		return this.texts.containsKey(key);
	}

	/**
	 * Remove multiple locale text objects from factory (returns null if not found)
	 * @param name The locale name (required, not null or empty, e.g. "CORE")
	 * @return LocaleText The locale text objects that be removed, returns null if not found
	 */
	public final LocaleText[] removeLocales(String name) {
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
	public final LocaleText removeLocale(String name, String lang) {
		if (StringHelper.isEmpty(name) || StringHelper.isEmpty(lang)) return null;
		String key = getKey(name, lang);
		synchronized (texts) {
			return this.texts.remove(key);
		}
	}

	/**
	 * Clear all locale text objects in factory
	 */
	public final void clearLocales() {
		synchronized (texts) {
			this.texts.clear();
		}
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public final boolean config(LocaleConfig[] data, boolean reset) {
		if (data == null) return false;
		if (reset) {
			this.clearLocales();
			this.langs.clear();
		}
		for (LocaleConfig config : data) {
			if (config == null || StringHelper.isEmpty(config.name)) continue;

			// Set language key
			String name = config.name;
			// Remove disable locale
			if (!reset && !config.enabled) {
				this.removeLocales(name);
				this.setDefaultLangKey(name, null);
				continue;
			}
			// Set default language
			this.setDefaultLangKey(name, config.defaultLang);
			if (!config.enabled) continue;

			// Load language texts
			PropertiesMap<PropertiesConfigFile> files = config.files;
			if (files == null || files.size() == 0) continue;
			for (Entry<String, PropertiesConfigFile> kv : files.entrySet()) {

				String lang = kv.getKey();
				PropertiesConfigFile file = kv.getValue();
				Properties texts = PropertiesLoader.loadProperties(file.file, file.encoding, file.fromPackage);
				if (texts == null) return false;
				// Ensure that use this.texts to get the locale object
				LocaleText locale = this.texts.get(getKey(name, lang));
				if (locale == null) {
					locale = createLocaleText(config.name, lang, texts);
					this.addLocale(name, lang, locale);
				} else {
					locale.config(texts, reset);
				}

			}
		}
		return true;
	}

	@Override
	public final String getGlobalLangKey() {
		return this.globalLang;
	}

	@Override
	public final void setGlobalLangKey(String lang) {
		if (StringHelper.isEmpty(lang)) return;
		this.globalLang = lang;
	}

	@Override
	public final String getDefaultLangKey(String name) {
		if (StringHelper.isEmpty(name)) return this.globalLang;
		String lang = this.langs.get(name.toUpperCase());
		return StringHelper.isEmpty(lang) ? this.globalLang : lang;
	}

	@Override
	public final void setDefaultLangKey(String name, String lang) {
		if (StringHelper.isEmpty(name)) return;
		this.langs.put(name.toUpperCase(), lang);
	}

	@Override
	public final LocaleText getLocale(String name) {
		String defaultLang = this.getDefaultLangKey(name);
		if (StringHelper.isEmpty(name)) return createLocaleText(null, defaultLang, null);
		LocaleText text = this.texts.get(getKey(name, defaultLang));
		if (text != null) return text;
		text = this.texts.get(getKey(name, "en_US"));
		return text != null ? text : createLocaleText(name, defaultLang, null);
	}

	@Override
	public final LocaleText getLocale(String name, String lang) {
		if (StringHelper.isEmpty(name)) {
			return createLocaleText(name, lang, null);
		}
		if (StringHelper.isEmpty(lang)) {
			LocaleText text = this.texts.get(getKey(name, this.getDefaultLangKey(name)));
			if (text != null) return text;
			text = this.texts.get(getKey(name, "en_US"));
			return text != null ? text : createLocaleText(name, lang, null);
		} else {
			LocaleText text = this.texts.get(getKey(name, lang));
			if (text != null) return text;
			text = this.texts.get(getKey(name, this.getDefaultLangKey(name)));
			if (text != null) return text;
			text = this.texts.get(getKey(name, "en_US"));
			return text != null ? text : createLocaleText(name, lang, null);
		}
	}

	@Override
	public final LocaleText getLocale(String name, String[] langs) {
		if (StringHelper.isEmpty(name)) {
			return createLocaleText(name, langs != null && langs.length > 0 ? langs[0] : null, null);
		}
		// Defined the object
		LocaleText text = null;
		// If there is any language key, try to get one
		if (langs != null && langs.length > 0) {
			// Traversal language keys
			for (String lang : langs) {
				// Verify the language key
				if (StringHelper.isEmpty(lang)) continue;
				// Get the locale object
				text = this.texts.get(getKey(name, lang));
				// Return data if the object is not null
				if (text != null) return text;
			}
		}
		// Get default language key
		text = this.texts.get(getKey(name, this.getDefaultLangKey(name)));
		if (text != null) return text;
		text = this.texts.get(getKey(name, "en_US"));
		return text != null ? text : createLocaleText(name, langs != null && langs.length > 0 ? langs[0] : null, null);
	}

	// --------------------------- Private methods ----------------------------

	/**
	 * Get a map key for locale text map
	 * @param name The locale name (required, not null or empty, e.g. "CORE")
	 * @param lang Locale text language key (required, not null or empty, e.g. "en_US", "zh_CN")
	 * @return String The map key
	 */
	protected static final String getKey(String name, String lang) {
		return name.toUpperCase() + "|" + lang.toUpperCase();
	}

	// --------------------------- Abstract method ----------------------------

	/**
	 * Create a new locale text instance for locale factory.
	 * @param name The locale text name (e.g. "CORE").
	 * @param lang The locale text language key (e.g. "en_US", "zh_CN").
	 * @param texts The text map with key and text value.
	 * @return Locale text object (not null).
	 */
	protected abstract LocaleText createLocaleText(String name, String lang, Map<Object, Object> texts);

}
