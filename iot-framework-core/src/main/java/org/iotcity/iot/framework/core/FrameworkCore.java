package org.iotcity.iot.framework.core;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.i18n.LocaleConfigure;
import org.iotcity.iot.framework.core.i18n.LocaleText;
import org.iotcity.iot.framework.core.logging.Logger;

/**
 * IoT Framework core.
 * @author Ardon
 */
public final class FrameworkCore {

	// --------------------------- Private static fields ----------------------------

	/**
	 * The logger configure name.
	 */
	private static final String LOGGER_NAME = "CORE";
	/**
	 * The locale configure name.
	 */
	private static final String LOCALE_NAME = "CORE";

	// --------------------------- Default static block ----------------------------

	static {
		// Do configure
		config();
	}

	// --------------------------- Public static methods ----------------------------

	/**
	 * Configure or reconfigure locale data.
	 */
	public static final void config() {
		// Configure the locale text
		new LocaleConfigure("org/iotcity/iot/framework/core/resources/i18n-core-config.properties", true).config(IoTFramework.getLocaleFactory(), false);
	}

	/**
	 * Gets a default logger object of framework core (returns not null).
	 * @return A logger to log message (not null).
	 */
	public static final Logger getLogger() {
		return IoTFramework.getLoggerFactory().getLogger(LOGGER_NAME);
	}

	/**
	 * Gets a default language locale object of framework core (returns not null).
	 * @return A locale text object (not null).
	 */
	public static final LocaleText getLocale() {
		return IoTFramework.getLocaleFactory().getLocale(LOCALE_NAME);
	}

	/**
	 * Gets a locale text object by specified language key of framework core (returns not null).
	 * @param lang Locale text language key (optional, set a null value to use default language key by default, e.g. "en_US", "zh_CN").
	 * @return Locale text object (not null).
	 */
	public static final LocaleText getLocale(String lang) {
		return IoTFramework.getLocaleFactory().getLocale(LOCALE_NAME, lang);
	}

	/**
	 * Gets a locale text object by specified language keys of framework core (returns not null).
	 * @param langs Locale text language keys (optional, set a null value to use default language key by default, e.g. ["en_US", "zh_CN"]).
	 * @return Locale text object (not null).
	 */
	public static final LocaleText getLocale(String[] langs) {
		return IoTFramework.getLocaleFactory().getLocale(LOCALE_NAME, langs);
	}

}
