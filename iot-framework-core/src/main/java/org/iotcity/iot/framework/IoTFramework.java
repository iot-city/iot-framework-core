package org.iotcity.iot.framework;

import org.iotcity.iot.framework.core.beans.ClassInstanceFactory;
import org.iotcity.iot.framework.core.beans.DefaultClassInstanceFactory;
import org.iotcity.iot.framework.core.bus.BusEventPublisher;
import org.iotcity.iot.framework.core.hook.HookManager;
import org.iotcity.iot.framework.core.i18n.DefaultLocaleFacotry;
import org.iotcity.iot.framework.core.i18n.LocaleFactory;
import org.iotcity.iot.framework.core.logging.DefaultLoggerFactory;
import org.iotcity.iot.framework.core.logging.LoggerFactory;

/**
 * IoT Framework for smart city system development.
 * 
 * <pre>
 * The configuration file of the framework ("framework.properties" by default) can be configured according to the following parameters:<br/>
 * 1. Full VM parameters configuration:
 * java -jar xxx.jar 
 * 		-Dframework.file=framework.properties 
 * 		-Dframework.file.encoding=UTF-8 
 * 		-Dframework.file.fromPackage=false 
 * 		-Dframework.console.colorful=true 
 * 
 * 2. Simple VM parameter configuration:
 * java -jar xxx.jar -Dframework.file=framework.properties
 * </pre>
 * 
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
	 * The system shutdown hook manager.
	 */
	private static final HookManager hookManager = new HookManager();
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

	// --------------------------- Initialize methods ----------------------------

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
		// Initialize all configuration managers.
		configHandler.init();
	}

	// --------------------------- Instance methods ----------------------------

	/**
	 * Create or get an instance of the declaring class from global instance factory (returns the class instance, it will returns null when no instance for specified class).<br/>
	 * The framework uses {@link DefaultClassInstanceFactory } by default to create an instance with "<b>clazz.getConstructor().newInstance()</b>" method.
	 * @param <T> The instance class type.
	 * @param clazz The class of object instance.
	 * @return An object created by factory.
	 * @throws Exception Throw an exception when an error is encountered.
	 */
	public static final <T> T getInstance(Class<?> clazz) throws Exception {
		return instanceFactory.getInstance(clazz);
	}

	/**
	 * Create or get an instance of the declaring class from global instance factory (returns the class instance, it will returns null when no instance for specified class).<br/>
	 * The framework uses {@link DefaultClassInstanceFactory } by default to create an instance with "<b>clazz.getConstructor(Class<?>... parameterTypes).newInstance(Object ... initargs)</b>" method.
	 * @param <T> The instance class type.
	 * @param clazz The class of object instance.
	 * @param parameterTypes The parameter class array of constructor.
	 * @param initargs The array of objects to be passed as arguments to the constructor call.
	 * @return An object created by factory.
	 * @throws Exception Throw an exception when an error is encountered.
	 */
	public static final <T> T getInstance(Class<?> clazz, Class<?>[] parameterTypes, Object[] initargs) throws Exception {
		return instanceFactory.getInstance(clazz, parameterTypes, initargs);
	}

	// --------------------------- Public object methods ----------------------------

	/**
	 * Gets the system shutdown hook manager (never null).
	 */
	public static final HookManager getHookmanager() {
		return hookManager;
	}

	/**
	 * Gets the configure manager handler of the framework (never null).
	 * @return The configure manager handler.
	 */
	public static final ConfigureHandler getConfigureHandler() {
		return configHandler;
	}

	/**
	 * Gets the bus event publisher of the framework (never null).
	 * @return Bus event publisher.
	 */
	public static final BusEventPublisher getBusEventPublisher() {
		return busEventPublisher;
	}

	/**
	 * Gets the global class instance factory to create or get an instance of specified class (never null).<br/>
	 * The framework uses {@link DefaultClassInstanceFactory } by default.
	 * @return A class instance factory.
	 */
	public static final ClassInstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	/**
	 * Gets the logger factory used in the framework (never null).<br/>
	 * (the default instance object be created with {@link DefaultLoggerFactory }, it can be changed by using init(options) method).
	 * @return Logger factory object.
	 */
	public static final LoggerFactory getLoggerFactory() {
		return loggerFactory;
	}

	/**
	 * Gets the locale factory used in the framework (never null).<br/>
	 * (the default instance object be created with {@link DefaultLocaleFacotry }, it can be changed by using init(options) method).
	 * @return Locale factory object.
	 */
	public static final LocaleFactory getLocaleFactory() {
		return localeFactory;
	}

}
