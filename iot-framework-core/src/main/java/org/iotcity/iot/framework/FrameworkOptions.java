package org.iotcity.iot.framework;

import org.iotcity.iot.framework.core.beans.ClassInstanceFactory;
import org.iotcity.iot.framework.core.beans.DefaultClassInstanceFactory;
import org.iotcity.iot.framework.core.i18n.DefaultLocaleFacotry;
import org.iotcity.iot.framework.core.i18n.LocaleFactory;
import org.iotcity.iot.framework.core.logging.DefaultLoggerFactory;
import org.iotcity.iot.framework.core.logging.LoggerFactory;

/**
 * Framework startup options data object.
 * @author Ardon
 * @date 2021-04-26
 */
public final class FrameworkOptions {

	/**
	 * Global class instance factory to create or get an instance of specified class (optional, if set it to null value, the framework will use an instance of {@link DefaultClassInstanceFactory } by default to create an instance with "<b>clazz.getDeclaredConstructor().newInstance()</b>" method).
	 */
	public ClassInstanceFactory instanceFactory;
	/**
	 * Logger factory used in framework (optional, an instance of {@link DefaultLoggerFactory } by default).
	 */
	public LoggerFactory loggerFactory;
	/**
	 * Locale factory used in framework (optional, an instance of {@link DefaultLocaleFacotry } by default).
	 */
	public LocaleFactory localeFactory;

	/**
	 * Constructor for framework startup options.
	 */
	public FrameworkOptions() {
	}

	/**
	 * Constructor for framework startup options.
	 * @param instanceFactory Global class instance factory to create or get an instance of specified class (optional, if set it to null value, the framework will use an instance of {@link DefaultClassInstanceFactory } by default to create an instance with "<b>clazz.getDeclaredConstructor().newInstance()</b>" method).
	 * @param loggerFactory Logger factory used in framework (optional, an instance of {@link DefaultLoggerFactory } by default).
	 * @param localeFactory Locale factory used in framework (optional, an instance of {@link DefaultLocaleFacotry } by default).
	 */
	public FrameworkOptions(ClassInstanceFactory instanceFactory, LoggerFactory loggerFactory, LocaleFactory localeFactory) {
		this.instanceFactory = instanceFactory;
		this.loggerFactory = loggerFactory;
		this.localeFactory = localeFactory;
	}

}
