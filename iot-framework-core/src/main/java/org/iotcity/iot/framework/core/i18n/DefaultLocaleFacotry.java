package org.iotcity.iot.framework.core.i18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.core.util.helper.SystemHelper;

/**
 * Default locale factory
 * @author Ardon
 */
public class DefaultLocaleFacotry implements LocaleFactory {

	// --------------------------- Private fields ----------------------------

	/**
	 * Global language key (e.g. "en_US").
	 */
	private String globalLang;
	/**
	 * The language keys map of current factory, the key is upper case name, the value is language key.
	 */
	private final Map<String, String> langs = new HashMap<>();
	/**
	 * The text map of current factory, the key is name and language key, the value is the locale text object.
	 */
	private final Map<String, LocaleText> texts = new HashMap<>();

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for default locale factory
	 */
	public DefaultLocaleFacotry() {
		this.globalLang = SystemHelper.getSystemLang();
	}

	// --------------------------- Public methods ----------------------------

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
	 * Returns true if this factory contains the locale name.
	 * @param name The locale name (required, not null or empty, e.g. "CORE")
	 * @return Whether this factor contains the locale name.
	 */
	public boolean containsLocale(String name) {
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
	 * @return Whether this factor contains the locale name.
	 */
	public boolean containsLocale(String name, String lang) {
		if (StringHelper.isEmpty(name) || StringHelper.isEmpty(lang)) return false;
		String key = getKey(name, lang);
		return this.texts.containsKey(key);
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
	public boolean config(LocaleConfig[] data, boolean reset) {
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
			LocaleConfigText[] langs = config.langs;
			if (langs == null || langs.length == 0) continue;
			// Set locale text configure
			for (LocaleConfigText textConfig : langs) {
				if (textConfig == null || StringHelper.isEmpty(textConfig.lang)) continue;
				String lang = textConfig.lang;
				LocaleText locale = this.texts.get(getKey(name, lang));
				if (locale == null) {
					locale = new DefaultLocaleText(config.name, textConfig.lang, textConfig.texts);
					this.addLocale(name, lang, locale);
				} else {
					locale.config(textConfig, reset);
				}
			}
		}
		return true;
	}

	@Override
	public String getGlobalLangKey() {
		return this.globalLang;
	}

	@Override
	public void setGlobalLangKey(String lang) {
		if (StringHelper.isEmpty(lang)) return;
		this.globalLang = lang;
	}

	@Override
	public String getDefaultLangKey(String name) {
		if (StringHelper.isEmpty(name)) return this.globalLang;
		String lang = this.langs.get(name.toUpperCase());
		return StringHelper.isEmpty(lang) ? this.globalLang : lang;
	}

	@Override
	public void setDefaultLangKey(String name, String lang) {
		if (StringHelper.isEmpty(name)) return;
		this.langs.put(name.toUpperCase(), lang);
	}

	@Override
	public LocaleText getLocale(String name) {
		String defaultLang = this.getDefaultLangKey(name);
		if (StringHelper.isEmpty(name)) return new DefaultLocaleText(null, defaultLang, null);
		LocaleText text = this.texts.get(getKey(name, defaultLang));
		if (text != null) return text;
		text = this.texts.get(getKey(name, "en_US"));
		return text != null ? text : new DefaultLocaleText(name, defaultLang, null);
	}

	@Override
	public LocaleText getLocale(String name, String lang) {
		if (StringHelper.isEmpty(name)) {
			return new DefaultLocaleText(name, lang, null);
		}
		if (StringHelper.isEmpty(lang)) {
			LocaleText text = this.texts.get(getKey(name, this.getDefaultLangKey(name)));
			if (text != null) return text;
			text = this.texts.get(getKey(name, "en_US"));
			return text != null ? text : new DefaultLocaleText(name, lang, null);
		} else {
			LocaleText text = this.texts.get(getKey(name, lang));
			if (text != null) return text;
			text = this.texts.get(getKey(name, this.getDefaultLangKey(name)));
			if (text != null) return text;
			text = this.texts.get(getKey(name, "en_US"));
			return text != null ? text : new DefaultLocaleText(name, lang, null);
		}
	}

	@Override
	public LocaleText getLocale(String name, String[] langs) {
		if (StringHelper.isEmpty(name)) {
			return new DefaultLocaleText(name, langs != null && langs.length > 0 ? langs[0] : null, null);
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
		return text != null ? text : new DefaultLocaleText(name, langs != null && langs.length > 0 ? langs[0] : null, null);
	}

	// --------------------------- Private methods ----------------------------

	/**
	 * Get a map key for locale text map
	 * @param name The locale name (required, not null or empty, e.g. "CORE")
	 * @param lang Locale text language key (required, not null or empty, e.g. "en_US", "zh_CN")
	 * @return String The map key
	 */
	private static final String getKey(String name, String lang) {
		return name.toUpperCase() + "|" + lang.toUpperCase();
	}

}
