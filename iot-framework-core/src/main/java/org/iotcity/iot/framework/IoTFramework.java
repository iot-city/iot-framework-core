package org.iotcity.iot.framework;

import org.iotcity.iot.framework.core.beans.ClassInstanceFactory;
import org.iotcity.iot.framework.core.beans.DefaultClassInstanceFactory;
import org.iotcity.iot.framework.core.bus.BusEventPublisher;
import org.iotcity.iot.framework.core.i18n.DefaultLocaleFacotry;
import org.iotcity.iot.framework.core.i18n.LocaleFactory;
import org.iotcity.iot.framework.core.logging.DefaultLoggerFactory;
import org.iotcity.iot.framework.core.logging.LoggerFactory;

/**
 * IoT Framework for smart city system development.
 * @author Ardon
 */
public final class IoTFramework {

	// --------------------------- Public static fields ----------------------------

	/**
	 * Framework group name.
	 */
	public static final String NAME = "org.iot-city.iot-framework";
	/**
	 * Framework version.
	 */
	public static final String VERSION = "0.0.1";
	/**
	 * Framework official web site.
	 */
	public static final String SITE = "http://www.iot-city.org";

	// --------------------------- Private static fields ----------------------------

	/**
	 * Whether the framework has initialized.
	 */
	private static boolean initialized = false;
	/**
	 * The synchronized lock.
	 */
	private static final Object lock = new Object();
	/**
	 * Configure manager handler of framework.
	 */
	private static final ConfigureHandler configHandler = new ConfigureHandler();
	/**
	 * Bus event publisher is used to publish bus event data processing.
	 */
	private static final BusEventPublisher busEventPublisher = new BusEventPublisher();
	/**
	 * Global class instance factory to get an instance of specified class.
	 */
	private static ClassInstanceFactory instanceFactory = new DefaultClassInstanceFactory();
	/**
	 * Logger factory used in framework.
	 */
	private static LoggerFactory loggerFactory = new DefaultLoggerFactory();
	/**
	 * Locale factory used in framework.
	 */
	private static LocaleFactory localeFactory = new DefaultLocaleFacotry();

	// --------------------------- Public static methods ----------------------------

	/**
	 * Initialize the framework configuration data.
	 */
	public static final void init() {
		init(null);
	}

	/**
	 * Initialize the framework configuration data.
	 * @param options Framework startup options (optional, null by default).
	 */
	public static final void init(FrameworkOptions options) {
		if (initialized) return;
		synchronized (lock) {
			if (initialized) return;
			initialized = true;
		}
		// Initialize factories
		if (options != null) {
			if (options.instanceFactory != null) instanceFactory = options.instanceFactory;
			if (options.loggerFactory != null) loggerFactory = options.loggerFactory;
			if (options.localeFactory != null) localeFactory = options.localeFactory;
		}
		// Initialize all configure managers
		configHandler.init(options);
	}

	/**
	 * Gets the configure manager handler of the framework (returns not null).
	 * @return The configure manager handler.
	 */
	public static final ConfigureHandler getConfigureHandler() {
		return configHandler;
	}

	/**
	 * Gets the bus event publisher of the framework (returns not null).
	 * @return Bus event publisher.
	 */
	public static final BusEventPublisher getBusEventPublisher() {
		return busEventPublisher;
	}

	/**
	 * Gets the global class instance factory to create or get an instance of specified class (returns not null).<br/>
	 * The framework uses {@link DefaultClassInstanceFactory } by default to create an instance with "<b>clazz.getDeclaredConstructor().newInstance()</b>" method.
	 * @return A class instance factory.
	 */
	public static ClassInstanceFactory getGlobalInstanceFactory() {
		return instanceFactory;
	}

	/**
	 * Gets the logger factory used in the framework (returns not null).<br/>
	 * (the default instance object be created with {@link DefaultLoggerFactory }, it can be changed by using init(options) method).
	 * @return Logger factory object.
	 */
	public static final LoggerFactory getLoggerFactory() {
		return loggerFactory;
	}

	/**
	 * Gets the locale factory used in the framework (returns not null).<br/>
	 * (the default instance object be created with {@link DefaultLocaleFacotry }, it can be changed by using init(options) method).
	 * @return Locale factory object.
	 */
	public static final LocaleFactory getLocaleFactory() {
		return localeFactory;
	}

}
