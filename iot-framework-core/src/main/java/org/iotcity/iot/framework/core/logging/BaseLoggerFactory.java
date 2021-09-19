package org.iotcity.iot.framework.core.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.iotcity.iot.framework.FrameworkConfiguration;
import org.iotcity.iot.framework.core.util.config.PropertiesMap;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * The base logger factory object, used to provide the common logger factory support.
 * @author ardon
 * @date 2021-08-10
 */
public abstract class BaseLoggerFactory implements LoggerFactory {

	// --------------------------- Static fields ----------------------------

	/**
	 * Indicates whether the log is printed in color (this is a global option for all loggers in this factory).
	 */
	private static final boolean loggingColorful = getLoggingColorful();

	/**
	 * Gets the logging colorful configuration.
	 */
	private static final boolean getLoggingColorful() {
		boolean colorful = ConvertHelper.toBoolean(System.getProperty("framework.console.colorful"), false);
		return ConvertHelper.toBoolean(FrameworkConfiguration.getFrameworkConfiguration().getProperty("iot.framework.global.console.colorful"), colorful);
	}

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
	 * Constructor for base logger factory.
	 * @param createRoot Specify whether the root logger object needs to be created. If it is set to false, it will not be created. If it is set to true, it will be created automatically.
	 */
	protected BaseLoggerFactory(boolean createRoot) {
		// Create root logger.
		if (createRoot) {
			Logger logger = createLogger("ROOT", loggingColorful, null, 0, null);
			logger.config(LogLevel.getDefaultLevels(), false);
			setRootLogger(logger);
		}
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Set the root logger object.
	 * @param logger Logger object (required, not null).
	 */
	public final void setRootLogger(Logger logger) {
		if (logger == null) return;
		this.root = logger;
	}

	/**
	 * Gets the root logger object.
	 * @return Logger object.
	 */
	public final Logger getRootLogger() {
		return root;
	}

	/**
	 * Gets loggers size in factory (excluding root logger).
	 * @return Loggers size.
	 */
	public final int size() {
		return this.loggers.size();
	}

	/**
	 * Add a logger to factory.
	 * @param name Logger name (required, not null or empty).
	 * @param logger Logger object (required, not null).
	 */
	public final void addLogger(String name, Logger logger) {
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
	public final boolean containsLogger(String name) {
		if (StringHelper.isEmpty(name)) return false;
		return loggers.containsKey(name);
	}

	/**
	 * Remove a logger from factory.
	 * @param name Logger name (required, not null or empty).
	 * @return Logger object (returns null if not exists).
	 */
	public final Logger removeLogger(String name) {
		if (StringHelper.isEmpty(name)) return null;
		synchronized (loggers) {
			return loggers.remove(name.toUpperCase());
		}
	}

	/**
	 * Clear all loggers (excluding root logger).
	 */
	public final void clearLoggers() {
		synchronized (loggers) {
			loggers.clear();
		}
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public final boolean config(LoggerConfig[] data, boolean reset) {
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
			Logger logger = createLogger(config.name, loggingColorful ? config.colorful : false, null, 0, levels);
			if (config.forRoot) {
				this.setRootLogger(logger);
			} else {
				this.addLogger(config.name, logger);
			}
		}
		return true;
	}

	@Override
	public final Logger getLogger() {
		return this.root.newInstance("", null, 0);
	}

	@Override
	public final Logger getLogger(String name) {
		return this.getLogger(name, null, 0);
	}

	@Override
	public final Logger getLogger(String name, Class<?> clazz) {
		return this.getLogger(name, clazz, 0);
	}

	@Override
	public final Logger getLogger(String name, Class<?> clazz, int callerDepth) {
		if (StringHelper.isEmpty(name)) return root.newInstance(name, clazz, callerDepth);
		Logger logger = loggers.get(name.toUpperCase());
		if (logger == null) return root.newInstance(name, clazz, callerDepth);
		return logger.newInstance(name, clazz, callerDepth);
	}

	// --------------------------- Abstract method ----------------------------

	/**
	 * Create a new logger instance for logger factory.
	 * @param name Logger name.
	 * @param colorful Whether to use multiple colors to display log information.
	 * @param clazz The Class whose name should be used in message. If null it will default to the calling class.
	 * @param callerDepth The depth from get logger method to the logging message method (0 by default).
	 * @param levels The configure map for all levels, the key is level name with upper case, the value is level configure (required, not null).
	 * @return A logger to log message (not null).
	 */
	protected abstract Logger createLogger(String name, boolean colorful, Class<?> clazz, int callerDepth, Map<String, LogLevel> levels);

}
