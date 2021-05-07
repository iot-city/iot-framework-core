package org.iotcity.iot.framework.core.logging;

import org.iotcity.iot.framework.core.util.config.PropertiesMap;

/**
 * Logger configure data.
 * @author Ardon
 */
public class LoggerConfig {

	/**
	 * Logger name.
	 */
	public String name;
	/**
	 * Whether to use multiple colors to display log information.
	 */
	public boolean colorful;
	/**
	 * Whether use for root logger.
	 */
	public boolean forRoot;
	/**
	 * All logger levels configuration map (the key is level name, the value is LoggerConfigLevel object).<br/>
	 * Available level names: "all", "log", "trace", "debug", "info", "warn", "error", "fatal".
	 */
	public PropertiesMap<LoggerConfigLevel> levels;

}
