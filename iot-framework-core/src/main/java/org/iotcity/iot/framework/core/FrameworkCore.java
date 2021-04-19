package org.iotcity.iot.framework.core;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.i18n.LocaleConfigure;
import org.iotcity.iot.framework.core.i18n.LocaleText;
import org.iotcity.iot.framework.core.logging.Logger;

/**
 * IoT Framework core
 * @author Ardon
 */
public final class FrameworkCore {

	// --------------------------- Private static fields ----------------------------

	/**
	 * The framework logger configure name
	 */
	private static final String CORE_LOGGER_NAME = "CORE";
	/**
	 * The framework locale configure name
	 */
	private static final String CORE_LOCALE_NAME = "CORE";

	// --------------------------- Default static block ----------------------------

	static {
		// Do configure
		config();
	}

	// --------------------------- Public static methods ----------------------------

	/**
	 * Configure or reconfigure framework core data.
	 */
	public static final void config() {
		// Configure core locale text
		new LocaleConfigure("org/iotcity/iot/framework/core/resources/i18n-core-config.properties", true).config(IoTFramework.getLocaleFactory(), false);
	}

	/**
	 * Gets a framework core default logger object.
	 * @return Logger A logger to log message (not null)
	 */
	public static final Logger getLogger() {
		return IoTFramework.getLoggerFactory().getLogger(CORE_LOGGER_NAME);
	}

	/**
	 * Gets a framework core default language locale object.
	 * @return Locale text object (not null).
	 */
	public static final LocaleText getLocale() {
		return IoTFramework.getLocaleFactory().getLocale(CORE_LOCALE_NAME);
	}

}
