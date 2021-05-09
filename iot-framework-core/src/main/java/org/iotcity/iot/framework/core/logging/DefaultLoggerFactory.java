package org.iotcity.iot.framework.core.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.iotcity.iot.framework.core.util.config.PropertiesMap;
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
		DefaultLogger logger = new DefaultLogger("ROOT", true, null, 0, null);
		logger.config(LogLevel.getDefaultLevels(), false);
		setRootLogger(logger);
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
	 * @return Returns true if this factor contains the logger name; otherwise, returns false.
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
	public boolean config(LoggerConfig[] data, boolean reset) {
		if (data == null) return false;
		if (reset) this.clearLoggers();
		// Traverse all configures
		for (LoggerConfig config : data) {
			if (config == null || StringHelper.isEmpty(config.name)) continue;
			// Get config info
			PropertiesMap<LoggerConfigLevel> map = config.levels;
			if (map == null || map.size() == 0) continue;
			// Create map
			Map<String, LogLevel> levels = new HashMap<>();
			// Check for all
			if (map.containsKey("all") || map.containsKey("ALL")) {
				LogLevel[] lvs = LogLevel.getDefaultLevels();
				for (LogLevel level : lvs) {
					levels.put(level.name.toUpperCase(), level);
				}
			}
			// Check for others
			for (Entry<String, LoggerConfigLevel> kv : map.entrySet()) {
				String name = kv.getKey();
				if ("all".equalsIgnoreCase(name)) continue;
				LogLevel level = LogLevel.getLogLevel(name, kv.getValue());
				if (level == null) continue;
				levels.put(name.toUpperCase(), level);
			}
			// Create logger
			DefaultLogger logger = new DefaultLogger(config.name, config.colorful, null, 0, levels);
			if (config.forRoot) {
				this.setRootLogger(logger);
			} else {
				this.addLogger(config.name, logger);
			}
		}
		return true;
	}

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
