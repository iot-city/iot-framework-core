package org.iotcity.iot.framework.core.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.iotcity.iot.framework.core.util.config.PropertiesLoader;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Default logger factory using console to output information
 * @author Ardon
 */
public class DefaultLoggerFactory implements LoggerFactory {

	// --------------------------- Private fields ----------------------------

	/**
	 * Global logger configure object
	 */
	private DefaultLoggerConfig globalConfig;
	/**
	 * The configures of current factory, the key is name, the value is the configure object
	 */
	private final Map<String, DefaultLoggerConfig> configs = new HashMap<>();

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for default logger factory
	 */
	public DefaultLoggerFactory() {
		this.globalConfig = new DefaultLoggerConfig("GLOBAL");
		this.initAllLevels(this.globalConfig);
	}

	/**
	 * Constructor for default logger factory<br/>
	 * <b>(Default configure template file: "org/iotcity/iot/framework/core/logging/iot-logger-template.properties")</b>
	 * @param configFile The logger configure properties file for this factory (required, not null or empty)
	 * @param fromPackage Whether load the file from package
	 */
	public DefaultLoggerFactory(String configFile, boolean fromPackage) {
		// Load configure file
		if (!this.config(configFile, fromPackage)) {
			throw new IllegalArgumentException("Load logger configure file error: " + configFile);
		}
	}

	// --------------------------- Private methods ----------------------------

	/**
	 * Initialize all configure levels
	 * @param config The logger configure object
	 */
	private void initAllLevels(DefaultLoggerConfig config) {
		// Clear levels
		config.clearLevels();
		// Traverse all levels
		for (String level : LogLevel.ALL) {
			// Add level to configure
			config.addLevel(level, true, true);
		}
	}

	/**
	 * Gets a logger configure object (returns not null)
	 * @param name Logger name (required, not null or empty)
	 * @return DefaultLoggerConfig Logger configure object
	 */
	private DefaultLoggerConfig getConfig(String name) {
		if (StringHelper.isEmpty(name)) return globalConfig;
		DefaultLoggerConfig config = this.configs.get(name.toUpperCase());
		return config == null ? globalConfig : config;
	}

	/**
	 * Create logger configure from properties (returns not null)
	 * @param props Properties object
	 * @param fkey The front key (e.g. "iot.framework.core.logging." or ""iot.framework.core.logging.core."")
	 * @param defaultName Default configure name (not null)
	 * @return DefaultLoggerConfig Configure object (not null)
	 */
	private DefaultLoggerConfig createConfig(Properties props, String fkey, String defaultName) {
		// iot.framework.core.logging.name=GLOBAL
		String name = props.getProperty(fkey + "name", defaultName);
		DefaultLoggerConfig config = new DefaultLoggerConfig(name);

		// iot.framework.core.logging.levels=trace, debug, info, warn, error, fatal
		// # Available logger levels: all, trace, debug, info, warn, error, fatal (all by default)
		String levels = props.getProperty(fkey + "levels");
		if (levels == null || levels.trim().length() == 0) return config;

		levels = levels.toLowerCase();
		String[] keys = levels.split("[,;]");
		if (keys == null || keys.length == 0) return config;

		// Traverse all level keys
		for (int i = 0, c = keys.length; i < c; i++) {

			// Get a level key (lower case)
			String levelKey = keys[i].trim();
			if (levelKey.length() == 0) continue;

			if ("all".equals(levelKey)) {
				// Traverse all levels
				for (String level : LogLevel.ALL_LOWER_CASE) {
					// iot.framework.core.logging.trace.classTracking=true
					// iot.framework.core.logging.trace.methodTracking=true
					boolean classTracking = ConvertHelper.toBoolean(props.getProperty(fkey + level + ".classTracking"), true);
					boolean methodTracking = ConvertHelper.toBoolean(props.getProperty(fkey + level + ".methodTracking"), true);
					config.addLevel(level, classTracking, methodTracking);
				}
				return config;
			} else {
				// Get level config
				boolean classTracking = ConvertHelper.toBoolean(props.getProperty(fkey + levelKey + ".classTracking"), true);
				boolean methodTracking = ConvertHelper.toBoolean(props.getProperty(fkey + levelKey + ".methodTracking"), true);
				config.addLevel(levelKey, classTracking, methodTracking);
			}

		}

		return config;
	}

	// --------------------------- Config methods ----------------------------

	/**
	 * Load and configure the specified properties file to the current factory<br/>
	 * <b>(Default configure template file: "org/iotcity/iot/framework/core/logging/iot-logger-template.properties")</b>
	 * @param configFile The logger configure properties file for this factory (required, not null or empty)
	 * @param fromPackage Whether load the file from package
	 * @return boolean Whether config successfully
	 */
	public boolean config(String configFile, boolean fromPackage) {
		if (StringHelper.isEmpty(configFile)) return false;

		// Load file properties
		Properties props = PropertiesLoader.loadProperties(configFile, "UTF-8", fromPackage);
		if (props == null) return false;

		// Get global configure
		this.globalConfig = this.createConfig(props, "iot.framework.core.logging.", "GLOBAL");

		// iot.framework.core.logging=core, actor
		String loggers = props.getProperty("iot.framework.core.logging");
		if (loggers == null || loggers.length() == 0) return false;
		String[] keys = loggers.split("[,;]");
		if (keys == null || keys.length == 0) return false;

		// Traverse all logger configurations
		for (int i = 0, c = keys.length; i < c; i++) {
			// (e.g. "core", "actor")
			String loggerKey = keys[i].trim();
			if (loggerKey.length() == 0) continue;

			// iot.framework.core.logging.core.
			String fkey = "iot.framework.core.logging." + loggerKey + ".";
			DefaultLoggerConfig config = this.createConfig(props, fkey, loggerKey);
			this.configs.put(loggerKey.toUpperCase(), config);

		}
		return true;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Set the global logger configure
	 * @param config Logger configure
	 */
	public void setGlobalConfig(DefaultLoggerConfig config) {
		if (config == null) return;
		this.globalConfig = config;
	}

	/**
	 * Gets the global logger configure
	 * @return DefaultLoggerConfig Logger configure
	 */
	public DefaultLoggerConfig getGlobalConfig() {
		return this.globalConfig;
	}

	/**
	 * Add a logger configure to factory
	 * @param name Logger name (required, not null or empty)
	 * @param config Logger configure object (required, not null)
	 */
	public void addLoggerConfig(String name, DefaultLoggerConfig config) {
		if (StringHelper.isEmpty(name) || config == null) return;
		synchronized (configs) {
			this.configs.put(name.toUpperCase(), config);
		}
	}

	/**
	 * Gets a logger configure by name (returns null if not exists)
	 * @param name Logger name (required, not null or empty)
	 * @return DefaultLoggerConfig Logger configure object (returns null if not exists)
	 */
	public DefaultLoggerConfig getLoggerConfig(String name) {
		if (StringHelper.isEmpty(name)) return null;
		return this.configs.get(name.toUpperCase());
	}

	/**
	 * Remove a logger configure from factory
	 * @param name Logger name (required, not null or empty)
	 * @return DefaultLoggerConfig Logger configure object (returns null if not exists)
	 */
	public DefaultLoggerConfig removeLoggerConfig(String name) {
		if (StringHelper.isEmpty(name)) return null;
		synchronized (configs) {
			return this.configs.remove(name.toUpperCase());
		}
	}

	/**
	 * Clear all logger configures (excluding global configuration)
	 */
	public void clearLoggerConfigs() {
		synchronized (configs) {
			this.configs.clear();
		}
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public Logger getLogger(String name) {
		return new DefaultLogger(name, null, 0, this.getConfig(name));
	}

	@Override
	public Logger getLogger(String name, Class<?> clazz) {
		return new DefaultLogger(name, clazz, 0, this.getConfig(name));
	}

	@Override
	public Logger getLogger(String name, Class<?> clazz, int callerDepth) {
		return new DefaultLogger(name, clazz, callerDepth, this.getConfig(name));
	}

}
