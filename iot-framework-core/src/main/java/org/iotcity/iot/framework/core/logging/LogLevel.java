package org.iotcity.iot.framework.core.logging;

import java.util.ArrayList;
import java.util.List;

import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Log levels used for identifying the severity of an event.
 * @author Ardon
 */
public class LogLevel {

	// --------------------------- Static fields ----------------------------

	/**
	 * A fine-grained debug message, typically capturing the flow through the application.
	 */
	public static final String TRACE = "TRACE";
	/**
	 * A general debugging event.
	 */
	public static final String DEBUG = "DEBUG";
	/**
	 * An event for informational purposes.
	 */
	public static final String INFO = "INFO";
	/**
	 * An event that might possible lead to an error.
	 */
	public static final String WARN = "WARN";
	/**
	 * An error in the application, possibly recoverable.
	 */
	public static final String ERROR = "ERROR";
	/**
	 * A severe error that will prevent the application from continuing.
	 */
	public static final String FATAL = "FATAL";

	/**
	 * All logger levels in lower case.
	 */
	private static final String[] ALL_IN_LOWER_CASE = new String[] {
		"trace",
		"debug",
		"info",
		"warn",
		"error",
		"fatal"
	};

	// --------------------------- Font-Color ---------------------------

	/**
	 * Font color: DEFAULT
	 */
	public static final int COLOR_FONT_DEFAULT = 0;
	/**
	 * Font color: BLACK
	 */
	public static final int COLOR_FONT_BLACK = 30;
	/**
	 * Font color: RED
	 */
	public static final int COLOR_FONT_RED = 31;
	/**
	 * Font color: GREEN
	 */
	public static final int COLOR_FONT_GREEN = 32;
	/**
	 * Font color: YELLOW
	 */
	public static final int COLOR_FONT_YELLOW = 33;
	/**
	 * Font color: BLUE
	 */
	public static final int COLOR_FONT_BLUE = 34;
	/**
	 * Font color: PURPLE
	 */
	public static final int COLOR_FONT_PURPLE = 35;
	/**
	 * Font color: CYAN
	 */
	public static final int COLOR_FONT_CYAN = 36;
	/**
	 * Font color: WHITE
	 */
	public static final int COLOR_FONT_WHITE = 37;

	// --------------------------- Background-Color ---------------------------

	/**
	 * Background color: DEFAULT
	 */
	public static final int COLOR_BG_DEFAULT = 0;
	/**
	 * Background color: BLACK
	 */
	public static final int COLOR_BG_BLACK = 40;
	/**
	 * Background color: RED
	 */
	public static final int COLOR_BG_RED = 41;
	/**
	 * Background color: GREEN
	 */
	public static final int COLOR_BG_GREEN = 42;
	/**
	 * Background color: YELLOW
	 */
	public static final int COLOR_BG_YELLOW = 43;
	/**
	 * Background color: BLUE
	 */
	public static final int COLOR_BG_BLUE = 44;
	/**
	 * Background color: PURPLE
	 */
	public static final int COLOR_BG_PURPLE = 45;
	/**
	 * Background color: CYAN
	 */
	public static final int COLOR_BG_CYAN = 46;
	/**
	 * Background color: WHITE
	 */
	public static final int COLOR_BG_WHITE = 47;

	// --------------------------- Object fields ----------------------------

	/**
	 * The level name.
	 */
	public String name;
	/**
	 * Whether enable class tracking.
	 */
	public boolean classTracking;
	/**
	 * Whether enable method tracking.
	 */
	public boolean methodTracking;
	/**
	 * Font color of console output (reference: LogLevel.COLOR_FONT_XXXX).
	 */
	public int fontColor;
	/**
	 * Background color of console output (reference: LogLevel.COLOR_BG_XXXX).
	 */
	public int bgColor;

	// --------------------------- Public static methods ----------------------------

	/**
	 * Gets all default levels.
	 * @return Default levels.
	 */
	public static final LogLevel[] getDefaultLevels() {
		// Create list
		List<LogLevel> list = new ArrayList<>();
		// Traverse all levels
		for (String name : ALL_IN_LOWER_CASE) {
			// Create level
			LogLevel level = new LogLevel();
			level.name = name;
			boolean tracking = getTrackingDefault(name);
			level.classTracking = tracking;
			level.methodTracking = tracking;
			level.fontColor = getDefaultColor(name, true);
			level.bgColor = getDefaultColor(name, false);
			// Add to list
			list.add(level);
		}
		// Return levels
		return list.toArray(new LogLevel[0]);
	}

	/**
	 * Gets a logger level data.
	 * @param name Level name (required, can not be null or empty).
	 * @param config Logger configure data.
	 * @return Logger level data.
	 */
	public static final LogLevel getLogLevel(String name, LoggerConfigLevel config) {
		if (StringHelper.isEmpty(name)) return null;
		name = name.toLowerCase();
		LogLevel level = new LogLevel();
		level.name = name;
		boolean tracking = getTrackingDefault(name);
		if (config == null) {
			level.classTracking = tracking;
			level.methodTracking = tracking;
			level.fontColor = getDefaultColor(name, true);
			level.bgColor = getDefaultColor(name, false);
		} else {
			level.classTracking = ConvertHelper.toBoolean(config.classTracking, tracking);
			level.methodTracking = ConvertHelper.toBoolean(config.methodTracking, tracking);
			level.fontColor = getColorFromString(name, config.fontColor, true, config.fontHighlight);
			level.bgColor = getColorFromString(name, config.bgColor, false, config.bgHighlight);
		}
		return level;
	}

	// --------------------------- Private static methods ----------------------------

	/**
	 * Get tracking default value.
	 * @param name Level name.
	 */
	private static final boolean getTrackingDefault(String name) {
		return !("trace".equals(name) || "info".equals(name));
	}

	/**
	 * Get a color number from color string.
	 */
	private static final int getColorFromString(String name, String color, boolean isFontColor, boolean highLight) {
		// Set as default color if no config
		if (StringHelper.isEmpty(color)) return getDefaultColor(name, isFontColor);
		// Available font/background colors: black, red, green, yellow, blue, purple, cyan, white, default
		color = color.trim().toLowerCase();
		// Get int color
		int colorNum;
		switch (color) {
		case "black":
			colorNum = isFontColor ? COLOR_FONT_BLACK : COLOR_BG_BLACK;
			break;
		case "red":
			colorNum = isFontColor ? COLOR_FONT_RED : COLOR_BG_RED;
			break;
		case "green":
			colorNum = isFontColor ? COLOR_FONT_GREEN : COLOR_BG_GREEN;
			break;
		case "yellow":
			colorNum = isFontColor ? COLOR_FONT_YELLOW : COLOR_BG_YELLOW;
			break;
		case "blue":
			colorNum = isFontColor ? COLOR_FONT_BLUE : COLOR_BG_BLUE;
			break;
		case "purple":
			colorNum = isFontColor ? COLOR_FONT_PURPLE : COLOR_BG_PURPLE;
			break;
		case "cyan":
			colorNum = isFontColor ? COLOR_FONT_CYAN : COLOR_BG_CYAN;
			break;
		case "white":
			colorNum = isFontColor ? COLOR_FONT_WHITE : COLOR_BG_WHITE;
			break;
		default:
			colorNum = isFontColor ? COLOR_FONT_DEFAULT : COLOR_BG_DEFAULT;
			break;
		}
		// Set highlight color
		if (highLight && colorNum > 0) colorNum += 60;
		return colorNum;
	}

	/**
	 * Get the default color of current level.
	 */
	private static final int getDefaultColor(String name, boolean isFontColor) {
		// Available logger levels: trace, debug, info, warn, error, fatal
		int color;
		switch (name) {
		case "trace":
			color = isFontColor ? COLOR_FONT_BLACK + 60 : COLOR_BG_DEFAULT;
			break;
		case "debug":
			color = isFontColor ? COLOR_FONT_DEFAULT : COLOR_BG_DEFAULT;
			break;
		case "info":
			color = isFontColor ? COLOR_FONT_GREEN : COLOR_BG_DEFAULT;
			break;
		case "warn":
			color = isFontColor ? COLOR_FONT_PURPLE + 60 : COLOR_BG_DEFAULT;
			break;
		case "error":
			color = isFontColor ? COLOR_FONT_RED + 60 : COLOR_BG_DEFAULT;
			break;
		case "fatal":
			color = isFontColor ? COLOR_FONT_WHITE + 60 : COLOR_BG_RED + 60;
			break;
		default:
			color = isFontColor ? COLOR_FONT_DEFAULT : COLOR_BG_DEFAULT;
			break;
		}
		return color;
	}

}
