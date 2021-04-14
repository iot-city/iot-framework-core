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
