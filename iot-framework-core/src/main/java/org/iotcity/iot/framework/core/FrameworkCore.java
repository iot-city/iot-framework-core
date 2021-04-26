package org.iotcity.iot.framework.core;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.i18n.LocaleText;
import org.iotcity.iot.framework.core.logging.Logger;

/**
 * IoT Framework core.
 * @author Ardon
 */
public final class FrameworkCore {

	// --------------------------- Private static fields ----------------------------

	/**
	 * The framework core name.
	 */
	private static final String CORE_NAME = "CORE";

	// --------------------------- Public static methods ----------------------------

	/**
	 * Gets a default logger object of framework core (returns not null).
	 * @return A logger to log message (not null).
	 */
	public static final Logger getLogger() {
		return IoTFramework.getLoggerFactory().getLogger(CORE_NAME);
	}

	/**
	 * Gets a default language locale object of framework core (returns not null).
	 * @return A locale text object (not null).
	 */
	public static final LocaleText getLocale() {
		return IoTFramework.getLocaleFactory().getLocale(CORE_NAME);
	}

	/**
	 * Gets a locale text object by specified language key of framework core (returns not null).
	 * @param lang Locale text language key (optional, set a null value to use default language key by default, e.g. "en_US", "zh_CN").
	 * @return Locale text object (not null).
	 */
	public static final LocaleText getLocale(String lang) {
		return IoTFramework.getLocaleFactory().getLocale(CORE_NAME, lang);
	}

	/**
	 * Gets a locale text object by specified language keys of framework core (returns not null).
	 * @param langs Locale text language keys (optional, set a null value to use default language key by default, e.g. ["en_US", "zh_CN"]).
	 * @return Locale text object (not null).
	 */
	public static final LocaleText getLocale(String[] langs) {
		return IoTFramework.getLocaleFactory().getLocale(CORE_NAME, langs);
	}

}
