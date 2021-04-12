package org.iotcity.iot.framework.core.logging;

/**
 * Use the logger factory to create logging objects
 * @author Ardon
 */
public interface LoggerFactory {

	/**
	 * Get a logger to log message (returns not null)<br/>
	 * This logger object will use global configuration
	 * @return Logger A logger to log message (not null)
	 */
	Logger getLogger();

	/**
	 * Get a logger to log message (returns not null)
	 * @param name Logger name (required, not null or empty)
	 * @return Logger A logger to log message (not null)
	 */
	Logger getLogger(String name);

	/**
	 * Get a logger to log message (returns not null)
	 * @param name Logger name (required, not null or empty)
	 * @param clazz The Class whose name should be used in message. If null it will default to the calling class.
	 * @return Logger A logger to log message (not null)
	 */
	Logger getLogger(String name, Class<?> clazz);

	/**
	 * Get a logger to log message (returns not null)
	 * @param name Logger name (required, not null or empty)
	 * @param clazz The Class whose name should be used in message. If null it will default to the calling class.
	 * @param callerDepth The depth from get logger method to the logging message method (0 by default)
	 * @return Logger A logger to log message (not null)
	 */
	Logger getLogger(String name, Class<?> clazz, int callerDepth);

}
