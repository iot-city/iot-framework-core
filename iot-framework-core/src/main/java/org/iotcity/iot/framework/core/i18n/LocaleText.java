package org.iotcity.iot.framework.core.i18n;

/**
 * Locale text object
 * @author Ardon
 */
public interface LocaleText {

	/**
	 * Gets the locale text name (returns not null)
	 * @return String Locale text name
	 */
	String getName();

	/**
	 * Gets the locale text language key (returns not null, e.g. "en_US", "zh_CN")
	 * @return String Locale text language key
	 */
	String getLang();

	/**
	 * Gets the text by key (returns not null)
	 * @param key The text key (e.g. "locale.text.name")
	 * @param params Parameters to the text (an array of parameters to replace {0}.. {n})
	 * @return String The text string of current language and the key
	 */
	String text(String key, Object... params);

}
