package org.iotcity.iot.framework.core.logging;

/**
 * Logger
 * @author Ardon
 */
public interface Logger {

	/**
	 * Gets the logger name
	 * @return String Logger name
	 */
	String getName();

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
