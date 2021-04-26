package org.iotcity.iot.framework.core.logging;

import org.iotcity.iot.framework.core.config.Configurable;

/**
 * The logger object interface.
 * @author Ardon
 */
public interface Logger extends Configurable<LogLevel[]> {

	/**
	 * Create a new logger instance to log message (returns not null)
	 * @param name Logger name (required, not null or empty)
	 * @param clazz The Class whose name should be used in message. If null it will default to the calling class.
	 * @param callerDepth The depth from get logger method to the logging message method (0 by default)
	 * @return Logger A logger to log message (not null)
	 */
	Logger newInstance(String name, Class<?> clazz, int callerDepth);

	/**
	 * Logs a message with the <b>LOG</b> level.
	 * @param message The message object to log.
	 */
	void log(Object message);

	/**
	 * Logs a message with the <b>LOG</b> level.
	 * @param message The message object to log.
	 * @param e The exception to log, including its stack trace.
	 */
	void log(Object message, Throwable e);

	/**
	 * Logs a message with the <b>TRACE</b> level.
	 * @param message The message object to log.
	 */
	void trace(Object message);

	/**
	 * Logs a message with the <b>TRACE</b> level.
	 * @param message The message object to log.
	 * @param e The exception to log, including its stack trace.
	 */
	void trace(Object message, Throwable e);

	/**
	 * Logs a message with the <b>DEBUG</b> level.
	 * @param message The message object to log.
	 */
	void debug(Object message);

	/**
	 * Logs a message with the <b>DEBUG</b> level.
	 * @param message The message object to log.
	 * @param e The exception to log, including its stack trace.
	 */
	void debug(Object message, Throwable e);

	/**
	 * Logs a message with the <b>INFO</b> level.
	 * @param message The message object to log.
	 */
	void info(Object message);

	/**
	 * Logs a message with the <b>INFO</b> level.
	 * @param message The message object to log.
	 * @param e The exception to log, including its stack trace.
	 */
	void info(Object message, Throwable e);

	/**
	 * Logs a message with the <b>WARN</b> level.
	 * @param message The message object to log.
	 */
	void warn(Object message);

	/**
	 * Logs a message with the <b>WARN</b> level.
	 * @param message The message object to log.
	 * @param e The exception to log, including its stack trace.
	 */
	void warn(Object message, Throwable e);

	/**
	 * Logs a message with the <b>ERROR</b> level.
	 * @param message The message object to log.
	 */
	void error(Object message);

	/**
	 * Logs a message with the <b>ERROR</b> level.
	 * @param message The message object to log.
	 * @param e The exception to log, including its stack trace.
	 */
	void error(Object message, Throwable e);

	/**
	 * Logs a message with the <b>FATAL</b> level.
	 * @param message The message object to log.
	 */
	void fatal(Object message);

	/**
	 * Logs a message with the <b>FATAL</b> level.
	 * @param message The message object to log.
	 * @param e The exception to log, including its stack trace.
	 */
	void fatal(Object message, Throwable e);

}
