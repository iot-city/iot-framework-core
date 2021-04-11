package org.iotcity.iot.framework.core.i18n;

/**
 * Use the locale factory to get locale text objects
 * @author Ardon
 */
public interface LocaleFactory {

	/**
	 * Set default language key
	 * @param lang Language key (required, not null or empty, e.g. "en_US")
	 */
	void setDefaultLang(String lang);

	/**
	 * Gets a default language key (returns not null, e.g. "en_US")
	 * @return String Language key (not null or empty)
	 */
	String getDefaultLang();

	/**
	 * Gets a default language locale text object (returns not null)
	 * @param name The locale name (required, not null or empty, e.g. "CORE")
	 * @return LocaleText Locale text object (not null)
	 */
	LocaleText getDefaultLocale(String name);

	/**
	 * Gets a locale text object by specified name and language key (returns not null)
	 * @param name The locale name (required, not null or empty, e.g. "CORE")
	 * @param lang Locale text language key (required, not null or empty, e.g. "en_US", "zh_CN")
	 * @return LocaleText Locale text object (not null)
	 */
	LocaleText getLocale(String name, String lang);

}
