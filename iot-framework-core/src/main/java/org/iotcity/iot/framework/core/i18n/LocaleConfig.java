package org.iotcity.iot.framework.core.i18n;

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
	 * Locale configuration text data for languages.
	 */
	public LocaleConfigText[] langs;

}
