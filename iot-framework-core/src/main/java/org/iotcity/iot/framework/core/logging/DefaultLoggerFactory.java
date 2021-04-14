package org.iotcity.iot.framework.core.logging;

import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Default logger factory using console to output information.
 * @author Ardon
 */
public class DefaultLoggerFactory implements LoggerFactory {

	// --------------------------- Private fields ----------------------------

	/**
	 * Root logger object.
	 */
	private Logger root;
	/**
	 * The loggers of current factory, the key is name, the value is the logger object.
	 */
	private final Map<String, Logger> loggers = new HashMap<>();

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for default logger factory. <br/>
	 * <b>This constructor will automatically create the root logger object.</b>
	 */
	public DefaultLoggerFactory() {
		this(true);
	}

	/**
	 * Constructor for default logger factory.
	 * @param createRoot Specify whether the root logger object needs to be created. If it is set to false, it will not be created. If it is set to true, it will be created automatically.
	 */
	public DefaultLoggerFactory(boolean createRoot) {
		if (!createRoot) return;
		DefaultLogger<DefaultLoggerLevel> logger = new DefaultLogger<>("GLOBAL", null, 0, new HashMap<>());
		DefaultLoggerConfigure.initDefaultLevels(DefaultLoggerLevel.class, logger.levels);
		this.root = logger;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Set the root logger object.
	 * @param logger Logger object (required, not null).
	 */
	public void setRootLogger(Logger logger) {
		if (logger == null) return;
		this.root = logger;
	}

	/**
	 * Gets the root logger object.
	 * @return Logger object.
	 */
	public Logger getRootLogger() {
		return root;
	}

	/**
	 * Gets loggers size in factory (excluding root logger).
	 * @return Loggers size.
	 */
	public int size() {
		return this.loggers.size();
	}

	/**
	 * Add a logger to factory.
	 * @param name Logger name (required, not null or empty).
	 * @param logger Logger object (required, not null).
	 */
	public void addLogger(String name, Logger logger) {
		if (logger == null || StringHelper.isEmpty(name)) return;
		synchronized (loggers) {
			loggers.put(name.toUpperCase(), logger);
		}
	}

	/**
	 * Returns true if this factory contains the logger name.
	 * @param name Logger name (required, not null or empty).
	 * @return Whether this factor contains the logger name.
	 */
	public boolean containsLogger(String name) {
		if (StringHelper.isEmpty(name)) return false;
		return loggers.containsKey(name);
	}

	/**
	 * Remove a logger from factory.
	 * @param name Logger name (required, not null or empty).
	 * @return Logger object (returns null if not exists).
	 */
	public Logger removeLogger(String name) {
		if (StringHelper.isEmpty(name)) return null;
		synchronized (loggers) {
			return loggers.remove(name.toUpperCase());
		}
	}

	/**
	 * Clear all loggers (excluding root logger).
	 */
	public void clearLoggers() {
		synchronized (loggers) {
			loggers.clear();
		}
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public Logger getLogger() {
		return this.root.newInstance("", null, 0);
	}

	@Override
	public Logger getLogger(String name) {
		return this.getLogger(name, null, 0);
	}

	@Override
	public Logger getLogger(String name, Class<?> clazz) {
		return this.getLogger(name, clazz, 0);
	}

	@Override
	public Logger getLogger(String name, Class<?> clazz, int callerDepth) {
		if (StringHelper.isEmpty(name)) return root.newInstance(name, clazz, callerDepth);
		Logger logger = loggers.get(name.toUpperCase());
		if (logger == null) return root.newInstance(name, clazz, callerDepth);
		return logger.newInstance(name, clazz, callerDepth);
	}

}
