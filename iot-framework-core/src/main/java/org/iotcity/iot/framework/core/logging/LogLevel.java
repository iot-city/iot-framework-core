package org.iotcity.iot.framework.core.logging;

/**
 * Log levels used for identifying the severity of an event
 * @author Ardon
 */
public final class LogLevel {

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
	 * All logger levels
	 */
	public static final String[] ALL = new String[] {
		TRACE,
		DEBUG,
		INFO,
		WARN,
		ERROR,
		FATAL
	};

	/**
	 * All logger levels
	 */
	public static final String[] ALL_LOWER_CASE = new String[] {
		"trace",
		"debug",
		"info",
		"warn",
		"error",
		"fatal"
	};

}
