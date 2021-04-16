package org.iotcity.iot.framework;

import org.iotcity.iot.framework.core.i18n.DefaultLocaleFacotry;
import org.iotcity.iot.framework.core.i18n.LocaleFactory;
import org.iotcity.iot.framework.core.logging.DefaultLoggerFactory;
import org.iotcity.iot.framework.core.logging.LoggerFactory;

/**
 * IoT Framework for smart city system development
 * @author Ardon
 */
public final class IoTFramework {

	// --------------------------- Public static fields ----------------------------

	/**
	 * Framework group name
	 */
	public static final String NAME = "org.iot-city.iot-framework";
	/**
	 * Framework version
	 */
	public static final String VERSION = "0.0.1";

	// --------------------------- Private static fields ----------------------------

	/**
	 * Default logger factory in framework.
	 */
	private static LoggerFactory loggerFactory = new DefaultLoggerFactory();
	/**
	 * Default locale factory in framework.
	 */
	private static LocaleFactory localeFactory = new DefaultLocaleFacotry();

	// --------------------------- Public static methods ----------------------------

	/**
	 * Set a logger factory used in the framework.<br/>
	 * (the default instance object be created with {@link DefaultLoggerFactory }).
	 * @param factory Logger factory object.
	 */
	public static final void setLoggerFactory(LoggerFactory factory) {
		loggerFactory = factory;
	}

	/**
	 * Get a logger factory used in the framework.<br/>
	 * (the default instance object be created with {@link DefaultLoggerFactory }).
	 * @return Logger factory object.
	 */
	public static final LoggerFactory getLoggerFactory() {
		return loggerFactory;
	}

	/**
	 * Set a locale factory used in the framework.<br/>
	 * (the default instance object be created with {@link DefaultLocaleFacotry }).
	 * @param factory Locale factory object.
	 */
	public static final void setLocaleFactory(LocaleFactory factory) {
		localeFactory = factory;
	}

	/**
	 * Get a locale factory used in the framework.<br/>
	 * (the default instance object be created with {@link DefaultLocaleFacotry }).
	 * @return Locale factory object.
	 */
	public static final LocaleFactory getLocaleFactory() {
		return localeFactory;
	}

}
