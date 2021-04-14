package org.iotcity.iot.framework.core.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.iotcity.iot.framework.core.config.PropertiesConfigure;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Use this configuration loader to load the default logger configuration.<br/>
 * <b>(Default configure template file: "org/iotcity/iot/framework/core/logging/iot-logger-template.properties")</b>
 * @author Ardon
 */
public class DefaultLoggerConfigure implements PropertiesConfigure<DefaultLoggerFactory> {

	/**
	 * Load and configure the specified properties file to the current factory.<br/>
	 * <b>(Default configure template file: "org/iotcity/iot/framework/core/logging/iot-logger-template.properties")</b>
	 */
	@Override
	public boolean config(DefaultLoggerFactory configurable, String configFile, boolean fromPackage) {
		// Parameters verification
		if (configurable == null || StringHelper.isEmpty(configFile)) {
			throw new IllegalArgumentException("Parameters configurable and configFile can not be null or empty!");
		}

		// Load file properties
		Properties props = PropertiesLoader.loadProperties(configFile, "UTF-8", fromPackage);
		if (props == null) return false;

		// Create root logger
		DefaultLogger<DefaultLoggerLevel> root = createLogger(DefaultLoggerLevel.class, props, "iot.framework.core.logging.", "GLOBAL");
		if (root.levels == null || root.levels.size() == 0) initDefaultLevels(DefaultLoggerLevel.class, root.levels);
		// Set root logger
		configurable.setRootLogger(root);

		// iot.framework.core.logging=core, actor
		String loggers = props.getProperty("iot.framework.core.logging");
		if (loggers != null && loggers.length() > 0) {
			String[] keys = loggers.split("[,;]");
			if (keys != null && keys.length > 0) {

				// Traverse all logger configurations
				for (int i = 0, c = keys.length; i < c; i++) {
					// (e.g. "core", "actor")
					String loggerKey = keys[i].trim();
					if (loggerKey.length() == 0) continue;

					// iot.framework.core.logging.core.
					String fkey = "iot.framework.core.logging." + loggerKey + ".";
					// Create loggers
					DefaultLogger<DefaultLoggerLevel> logger = createLogger(DefaultLoggerLevel.class, props, fkey, loggerKey);
					// Add logger to factory
					configurable.addLogger(logger.name, logger);
				}
			}
		}
		// Return status
		return true;
	}

	// --------------------------- Private static methods ----------------------------

	/**
	 * Initialize all default level configures
	 * @param levelClass Level config data class
	 */
	protected final static <T extends DefaultLoggerLevel> void initDefaultLevels(Class<T> levelClass, Map<String, T> levels) {
		try {
			// Traverse all levels
			for (String level : LogLevel.ALL_LOWER_CASE) {
				// Add level to configure
				T config = levelClass.newInstance();
				config.name = level;
				boolean tracking = getTrackingDefault(level);
				config.classTracking = tracking;
				config.methodTracking = tracking;
				config.fontColor = getLevelDefaultColor(level, true);
				config.bgColor = getLevelDefaultColor(level, false);
				// Add to map
				levels.put(level.toUpperCase(), config);
			}
		} catch (Exception e) {
			System.err.println("Initialize level configure error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Create logger from properties (returns not null)
	 * @param levelClass Level config data class
	 * @param props Properties object
	 * @param fkey The front key (e.g. "iot.framework.core.logging." or ""iot.framework.core.logging.core."")
	 * @param defaultName Default configure name (not null)
	 * @return Logger object
	 */
	protected static final <T extends DefaultLoggerLevel> DefaultLogger<T> createLogger(Class<T> levelClass, Properties props, String fkey, String defaultName) {
		// iot.framework.core.logging.name=GLOBAL
		String name = props.getProperty(fkey + "name", defaultName).trim();
		if (StringHelper.isEmpty(name)) name = defaultName;
		Map<String, T> levelMap = new HashMap<>();
		DefaultLogger<T> logger = new DefaultLogger<>(name, null, 0, levelMap);

		// iot.framework.core.logging.levels=all, trace, debug, info, warn, error, fatal
		String levels = props.getProperty(fkey + "levels");
		if (levels == null || levels.trim().length() == 0) return logger;

		levels = levels.toLowerCase();
		String[] keys = levels.split("[,;]");
		if (keys == null || keys.length == 0) return logger;

		try {
			// Traverse all level keys
			for (int i = 0, c = keys.length; i < c; i++) {

				// Get a level key (lower case)
				String levelKey = keys[i].trim();
				if (levelKey.length() == 0) continue;

				if ("all".equals(levelKey)) {
					// Traverse all levels
					for (String level : LogLevel.ALL_LOWER_CASE) {
						// Get level config
						T levelConfig = levelClass.newInstance();
						levelConfig.name = level;
						levelConfig.classTracking = getBooleanConfig(props.getProperty(fkey + level + ".classTracking"), getTrackingDefault(level), false);
						levelConfig.methodTracking = getBooleanConfig(props.getProperty(fkey + level + ".methodTracking"), getTrackingDefault(level), false);
						boolean fontHighlight = getBooleanConfig(props.getProperty(fkey + level + ".fontHighlight"), false, false);
						boolean bgHighlight = getBooleanConfig(props.getProperty(fkey + level + ".bgHighlight"), false, false);
						levelConfig.fontColor = getColorFromString(level, props.getProperty(fkey + level + ".fontColor"), true, fontHighlight);
						levelConfig.bgColor = getColorFromString(level, props.getProperty(fkey + level + ".bgColor"), false, bgHighlight);
						// Add to map
						levelMap.put(level.toUpperCase(), levelConfig);
					}
					// Return logger
					return logger;
				} else {
					// Get level config
					T levelConfig = levelClass.newInstance();
					levelConfig.name = levelKey;
					levelConfig.classTracking = getBooleanConfig(props.getProperty(fkey + levelKey + ".classTracking"), getTrackingDefault(levelKey), false);
					levelConfig.methodTracking = getBooleanConfig(props.getProperty(fkey + levelKey + ".methodTracking"), getTrackingDefault(levelKey), false);
					boolean fontHighlight = getBooleanConfig(props.getProperty(fkey + levelKey + ".fontHighlight"), false, false);
					boolean bgHighlight = getBooleanConfig(props.getProperty(fkey + levelKey + ".bgHighlight"), false, false);
					levelConfig.fontColor = getColorFromString(levelKey, props.getProperty(fkey + levelKey + ".fontColor"), true, fontHighlight);
					levelConfig.bgColor = getColorFromString(levelKey, props.getProperty(fkey + levelKey + ".bgColor"), false, bgHighlight);
					// Add to map
					levelMap.put(levelKey.toUpperCase(), levelConfig);
				}
			}
		} catch (Exception e) {
			System.err.println("Create logger error: " + e.getMessage());
			e.printStackTrace();
		}
		// Return logger
		return logger;
	}

	/**
	 * Get tracking default value
	 */
	private static final boolean getTrackingDefault(String level) {
		return !("trace".equals(level) || "info".equals(level));
	}

	/**
	 * Get configure boolean value
	 */
	private static final boolean getBooleanConfig(String value, boolean nullValue, boolean defaultValue) {
		if (value == null) return nullValue;
		return ConvertHelper.toBoolean(value, defaultValue);
	}

	/**
	 * Get a color number from color string
	 */
	private static final int getColorFromString(String level, String color, boolean isFontColor, boolean highLight) {
		// Set as default color if no config
		if (color == null) return getLevelDefaultColor(level, isFontColor);
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
	private static final int getLevelDefaultColor(String level, boolean isFontColor) {
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

}
