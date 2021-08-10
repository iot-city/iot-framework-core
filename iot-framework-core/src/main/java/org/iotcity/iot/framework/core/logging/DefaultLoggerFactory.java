package org.iotcity.iot.framework.core.logging;

import java.util.Map;

/**
 * Default logger factory using console to output information.
 * @author Ardon
 */
public final class DefaultLoggerFactory extends BaseLoggerFactory {

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for default logger factory. <br/>
	 * <b>This constructor will automatically create the root logger object.</b>
	 */
	public DefaultLoggerFactory() {
		super(true);
	}

	// --------------------------- Inner methods ----------------------------

	@Override
	protected final Logger createLogger(String name, boolean colorful, Class<?> clazz, int callerDepth, Map<String, LogLevel> levels) {
		return new DefaultLogger(name, colorful, clazz, callerDepth, levels);
	}

	// --------------------------- Default logger class ----------------------------

	/**
	 * The default logger using console to output information.<br/>
	 * Use System.out.println() and System.err.println() to log messages.
	 * @author Ardon
	 */
	final class DefaultLogger extends BaseLogger {

		/**
		 * Constructor for default logger.
		 * @param name Logger name.
		 * @param colorful Whether to use multiple colors to display log information.
		 * @param clazz The Class whose name should be used in message. If null it will default to the calling class.
		 * @param callerDepth The depth from get logger method to the logging message method (0 by default).
		 * @param levels The configure map for all levels, the key is level name with upper case, the value is level configure (required, not null).
		 */
		DefaultLogger(String name, boolean colorful, Class<?> clazz, int callerDepth, Map<String, LogLevel> levels) {
			super(name, colorful, clazz, callerDepth, levels);
		}

		@Override
		public final Logger newInstance(String name, Class<?> clazz, int callerDepth) {
			return new DefaultLogger(name, colorful, clazz, callerDepth, levels);
		}

	}

}
