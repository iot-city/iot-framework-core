package org.iotcity.iot.framework.core.i18n;

import org.iotcity.iot.framework.core.config.Configurable;

/**
 * Use the locale factory to get locale text objects
 * @author Ardon
 */
public interface LocaleFactory extends Configurable<LocaleConfig[]> {

	/**
	 * Gets a global default language key (returns not null, e.g. "en_US").
	 * @return Language key (not null or empty).
	 */
	String getGlobalLangKey();

	/**
	 * Set a global default language key.
	 * @param lang Language key (required, not null or empty, e.g. "en_US").
	 */
	void setGlobalLangKey(String lang);

	/**
	 * Gets a default language key for specified locale name (returns not null, e.g. "en_US").
	 * @param name The locale name (required, not null or empty, e.g. "CORE").
	 * @return Language key (not null or empty).
	 */
	String getDefaultLangKey(String name);

	/**
	 * Set a default language key for specified locale name.
	 * @param name The locale name (required, not null or empty, e.g. "CORE").
	 * @param lang Default language key for locale name (set a null value to use global language key by default, e.g. "en_US").
	 */
	void setDefaultLangKey(String name, String lang);

	/**
	 * Gets a default language locale text object (returns not null).
	 * @param name The locale name (required, not null or empty, e.g. "CORE").
	 * @return Locale text object (not null).
	 */
	LocaleText getLocale(String name);

	/**
	 * Gets a locale text object by specified name and language key (returns not null).
	 * @param name The locale name (required, not null or empty, e.g. "CORE").
	 * @param lang Locale text language key (optional, set a null value to use default language key by default, e.g. "en_US", "zh_CN").
	 * @return Locale text object (not null).
	 */
	LocaleText getLocale(String name, String lang);

	/**
	 * Gets a locale text object by specified name and language keys (returns not null).
	 * @param name The locale name (required, not null or empty, e.g. "CORE").
	 * @param langs Locale text language keys (optional, set a null value to use default language key by default, e.g. ["en_US", "zh_CN"]).
	 * @return Locale text object (not null).
	 */
	LocaleText getLocale(String name, String[] langs);

}
