package org.iotcity.iot.framework.core.logging;

import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Default logger configure object
 * @author Ardon
 */
public final class DefaultLoggerConfig {

	// --------------------------- Private fields ----------------------------

	/**
	 * The configure name
	 */
	private final String name;
	/**
	 * The configure map for all levels
	 */
	private final Map<String, LevelConfig> levelConfig = new HashMap<>();

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for default logger configure object
	 * @param name The configure name (required, not null or empty)
	 */
	public DefaultLoggerConfig(String name) {
		this.name = name;
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets the configure name
	 * @return String Configure name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets level configures size
	 * @return int Size
	 */
	public int size() {
		return this.levelConfig.size();
	}

	/**
	 * Add a level configure to logger configure (returns null if invalid)
	 * @param level The logger level (reference: LogLevel.XXXX)
	 * @param classTracking Whether enable class tracking
	 * @param methodTracking Whether enable method tracking
	 * @param fontClor Font color of console output (reference: LogLevelColor.FONT_XXXX)
	 * @param bgColor Background color of console output (reference: LogLevelColor.BG_XXXX)
	 * @return LevelConfig The configure be created
	 */
	public LevelConfig addLevel(String level, boolean classTracking, boolean methodTracking, int fontClor, int bgColor) {
		if (StringHelper.isEmpty(level)) return null;
		LevelConfig config = new LevelConfig(classTracking, methodTracking, fontClor, bgColor);
		this.levelConfig.put(level.toUpperCase(), config);
		return config;
	}

	/**
	 * Get a level configure by level key (returns null if not exists)
	 * @param level The logger level (reference: LogLevel.XXXX)
	 * @return LevelConfig The configure for level
	 */
	public LevelConfig getLevel(String level) {
		if (StringHelper.isEmpty(level)) return null;
		return this.levelConfig.get(level.toUpperCase());
	}

	/**
	 * Determine whether the log level configure is exists
	 * @param level The logger level (reference: LogLevel.XXXX)
	 * @return boolean Whether the log level configure is exists
	 */
	public boolean containsLevel(String level) {
		if (StringHelper.isEmpty(level)) return false;
		return this.levelConfig.containsKey(level.toUpperCase());
	}

	/**
	 * Remove a level configure by level key (returns null if not exists)
	 * @param level The logger level (reference: LogLevel.XXXX)
	 * @return LevelConfig The level configure removed (returns null if not exists)
	 */
	public LevelConfig removeLevel(String level) {
		if (StringHelper.isEmpty(level)) return null;
		return this.levelConfig.remove(level.toUpperCase());
	}

	/**
	 * Clear all level configures
	 */
	public void clearLevels() {
		this.levelConfig.clear();
	}

	// --------------------------- Public class ----------------------------

	/**
	 * The level configure
	 * @author Ardon
	 */
	public final class LevelConfig {

		/**
		 * Whether enable class tracking
		 */
		public boolean classTracking;

		/**
		 * Whether enable method tracking
		 */
		public boolean methodTracking;
		/**
		 * Font color of console output (reference: LogLevelColor.FONT_XXXX)
		 */
		public int fontColor;
		/**
		 * Background color of console output (reference: LogLevelColor.BG_XXXX)
		 */
		public int bgColor;

		/**
		 * Constructor for level configure
		 * @param classTracking Whether enable class tracking
		 * @param methodTracking Whether enable method tracking
		 * @param fontClor Font color of console output (reference: LogLevelColor.FONT_XXXX)
		 * @param bgColor Background color of console output (reference: LogLevelColor.BG_XXXX)
		 */
		LevelConfig(boolean classTracking, boolean methodTracking, int fontClor, int bgColor) {
			this.classTracking = classTracking;
			this.methodTracking = methodTracking;
			this.fontColor = fontClor;
			this.bgColor = bgColor;
		}

	}

}
