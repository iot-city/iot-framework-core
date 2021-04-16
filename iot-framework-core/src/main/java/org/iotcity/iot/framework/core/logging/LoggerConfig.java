package org.iotcity.iot.framework.core.logging;

/**
 * Logger configure data
 * @author Ardon
 */
public class LoggerConfig {

	/**
	 * Logger name
	 */
	String name;
	/**
	 * Whether use for root logger
	 */
	boolean forRoot;
	/**
	 * All logger level configuration data
	 */
	LoggerConfigLevel[] levels;

}
