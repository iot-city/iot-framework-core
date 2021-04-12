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
		for (String level : LogLevel.ALL_LOWER_CASE) {
			// Add level to configure
			boolean tracking = this.getTrackingDefault(level);
			config.addLevel(level, tracking, tracking, this.getLevelDefaultColor(level, true), this.getLevelDefaultColor(level, false));
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
	 * @return DefaultLoggerConfig Configure object
	 */
	private DefaultLoggerConfig createConfig(Properties props, String fkey, String defaultName) {
		// iot.framework.core.logging.name=GLOBAL
		String name = props.getProperty(fkey + "name", defaultName).trim();
		if (StringHelper.isEmpty(name)) name = defaultName;
		DefaultLoggerConfig config = new DefaultLoggerConfig(name);

		// iot.framework.core.logging.levels=all, trace, debug, info, warn, error, fatal
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
					// Get level config
					boolean classTracking = this.getBooleanConfig(props.getProperty(fkey + level + ".classTracking"), this.getTrackingDefault(level), false);
					boolean methodTracking = this.getBooleanConfig(props.getProperty(fkey + level + ".methodTracking"), this.getTrackingDefault(level), false);
					boolean fontHighlight = this.getBooleanConfig(props.getProperty(fkey + level + ".fontHighlight"), false, false);
					boolean bgHighlight = this.getBooleanConfig(props.getProperty(fkey + level + ".bgHighlight"), false, false);
					int fontColor = this.getColorFromString(level, props.getProperty(fkey + level + ".fontColor"), true, fontHighlight);
					int bgColor = this.getColorFromString(level, props.getProperty(fkey + level + ".bgColor"), false, bgHighlight);
					config.addLevel(level, classTracking, methodTracking, fontColor, bgColor);
				}
				return config;
			} else {
				// Get level config
				boolean classTracking = this.getBooleanConfig(props.getProperty(fkey + levelKey + ".classTracking"), this.getTrackingDefault(levelKey), false);
				boolean methodTracking = this.getBooleanConfig(props.getProperty(fkey + levelKey + ".methodTracking"), this.getTrackingDefault(levelKey), false);
				boolean fontHighlight = this.getBooleanConfig(props.getProperty(fkey + levelKey + ".fontHighlight"), false, false);
				boolean bgHighlight = this.getBooleanConfig(props.getProperty(fkey + levelKey + ".bgHighlight"), false, false);
				int fontColor = this.getColorFromString(levelKey, props.getProperty(fkey + levelKey + ".fontColor"), true, fontHighlight);
				int bgColor = this.getColorFromString(levelKey, props.getProperty(fkey + levelKey + ".bgColor"), false, bgHighlight);
				config.addLevel(levelKey, classTracking, methodTracking, fontColor, bgColor);
			}

		}

		return config;
	}

	/**
	 * Get tracking default value
	 */
	private boolean getTrackingDefault(String level) {
		return !("trace".equals(level) || "info".equals(level));
	}

	/**
	 * Get configure boolean value
	 */
	private boolean getBooleanConfig(String value, boolean nullValue, boolean defaultValue) {
		if (value == null) return nullValue;
		return ConvertHelper.toBoolean(value, defaultValue);
	}

	/**
	 * Get a color number from color string
	 */
	private int getColorFromString(String level, String color, boolean isFontColor, boolean highLight) {
		// Set as default color if no config
		if (color == null) return this.getLevelDefaultColor(level, isFontColor);
		color = color.trim().toLowerCase();
		if (color.length() == 0 || "default".equals(color)) {
			return isFontColor ? LogLevelColor.FONT_DEFAULT : LogLevelColor.BG_DEFAULT;
		} else {
			// Available font/background colors: black, red, green, yellow, blue, purple, cyan, white, default
			int colorNum;
			switch (color) {
			case "black":
				colorNum = isFontColor ? LogLevelColor.FONT_BLACK : LogLevelColor.BG_BLACK;
				break;
			case "red":
				colorNum = isFontColor ? LogLevelColor.FONT_RED : LogLevelColor.BG_RED;
				break;
			case "green":
				colorNum = isFontColor ? LogLevelColor.FONT_GREEN : LogLevelColor.BG_GREEN;
				break;
			case "yellow":
				colorNum = isFontColor ? LogLevelColor.FONT_YELLOW : LogLevelColor.BG_YELLOW;
				break;
			case "blue":
				colorNum = isFontColor ? LogLevelColor.FONT_BLUE : LogLevelColor.BG_BLUE;
				break;
			case "purple":
				colorNum = isFontColor ? LogLevelColor.FONT_PURPLE : LogLevelColor.BG_PURPLE;
				break;
			case "cyan":
				colorNum = isFontColor ? LogLevelColor.FONT_CYAN : LogLevelColor.BG_CYAN;
				break;
			case "white":
				colorNum = isFontColor ? LogLevelColor.FONT_WHITE : LogLevelColor.BG_WHITE;
				break;
			default:
				colorNum = isFontColor ? LogLevelColor.FONT_DEFAULT : LogLevelColor.BG_DEFAULT;
				break;
			}
			// Set highlight color
			if (highLight && colorNum > 0) colorNum += 60;
			return colorNum;
		}
	}

	/**
	 * Get the default color of current level
	 */
	private int getLevelDefaultColor(String level, boolean isFontColor) {
		// Available logger levels: trace, debug, info, warn, error, fatal
		int color;
		switch (level) {
		case "trace":
			color = isFontColor ? LogLevelColor.FONT_BLACK + 60 : LogLevelColor.BG_DEFAULT;
			break;
		case "debug":
			color = isFontColor ? LogLevelColor.FONT_DEFAULT : LogLevelColor.BG_DEFAULT;
			break;
		case "info":
			color = isFontColor ? LogLevelColor.FONT_GREEN : LogLevelColor.BG_DEFAULT;
			break;
		case "warn":
			color = isFontColor ? LogLevelColor.FONT_PURPLE + 60 : LogLevelColor.BG_DEFAULT;
			break;
		case "error":
			color = isFontColor ? LogLevelColor.FONT_RED + 60 : LogLevelColor.BG_DEFAULT;
			break;
		case "fatal":
			color = isFontColor ? LogLevelColor.FONT_WHITE + 60 : LogLevelColor.BG_RED + 60;
			break;
		default:
			color = isFontColor ? LogLevelColor.FONT_DEFAULT : LogLevelColor.BG_DEFAULT;
			break;
		}
		return color;
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
		if (this.globalConfig.size() == 0) this.initAllLevels(this.globalConfig);

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
			this.configs.put(config.getName().toUpperCase(), config);

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
	 * Gets logger configures size
	 * @return int Size
	 */
	public int size() {
		return this.configs.size();
	}

	/**
	 * Add a logger configure to factory
	 * @param config Logger configure object (required, not null)
	 */
	public void addLoggerConfig(DefaultLoggerConfig config) {
		if (config == null || StringHelper.isEmpty(config.getName())) return;
		synchronized (configs) {
			this.configs.put(config.getName().toUpperCase(), config);
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
	public Logger getLogger() {
		return new DefaultLogger("", null, 0, this.globalConfig);
	}

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
