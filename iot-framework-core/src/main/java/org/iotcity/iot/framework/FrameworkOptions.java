package org.iotcity.iot.framework;

import org.iotcity.iot.framework.core.bus.BusEventListenerFactory;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
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
	 * The framework configuration properties file (optional, external "framework.properties" by default).
	 */
	public PropertiesConfigFile frameworkFile;
	/**
	 * Bus event listener factory to get a listener of specified listener class type (optional, it can be set to null when using <b>new</b> to create an instance).
	 */
	public BusEventListenerFactory busEventListenerFactory;
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
	 * @param frameworkFile The framework configuration properties file (optional, external "framework.properties" by default).
	 */
	public FrameworkOptions(PropertiesConfigFile frameworkFile) {
		this.frameworkFile = frameworkFile;
	}

	/**
	 * Constructor for framework startup options.
	 * @param frameworkFile The framework configuration properties file (optional, external "framework.properties" by default).
	 * @param busEventListenerFactory Bus event listener factory to get a listener of specified listener class type (optional, it can be set to null when using <b>new</b> to create an instance).
	 * @param loggerFactory Logger factory used in framework (optional, an instance of {@link DefaultLoggerFactory } by default).
	 * @param localeFactory Locale factory used in framework (optional, an instance of {@link DefaultLocaleFacotry } by default).
	 */
	public FrameworkOptions(PropertiesConfigFile frameworkFile, BusEventListenerFactory busEventListenerFactory, LoggerFactory loggerFactory, LocaleFactory localeFactory) {
		this.frameworkFile = frameworkFile;
		this.busEventListenerFactory = busEventListenerFactory;
		this.loggerFactory = loggerFactory;
		this.localeFactory = localeFactory;
	}

}
