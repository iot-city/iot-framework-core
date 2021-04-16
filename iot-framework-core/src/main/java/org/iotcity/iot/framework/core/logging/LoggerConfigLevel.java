package org.iotcity.iot.framework.core.logging;

/**
 * Logger level configure data
 * @author Ardon
 */
public class LoggerConfigLevel {

	/**
	 * The level name
	 */
	public String name;
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

}
