package org.iotcity.iot.framework.core.logging;

/**
 * Logger level configure data
 * @author Ardon
 */
public class LoggerConfigLevel {

	/**
	 * Whether enable class tracking (String boolean type to fix default status).
	 */
	public String classTracking;
	/**
	 * Whether enable method tracking (String boolean type to fix default status).
	 */
	public String methodTracking;
	/**
	 * Font color of console output.<br/>
	 * Available colors: "black", "red", "green", "yellow", "blue", "purple", "cyan", "white", "default".
	 */
	public String fontColor;
	/**
	 * Whether to use the highlight color (true by default).
	 */
	public boolean fontHighlight = true;
	/**
	 * Background color of console output.<br/>
	 * Available colors: "black", "red", "green", "yellow", "blue", "purple", "cyan", "white", "default".
	 */
	public String bgColor;
	/**
	 * Whether to use the highlight color (false by default).
	 */
	public boolean bgHighlight;

}
