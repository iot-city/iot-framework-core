package org.iotcity.iot.framework.core.config;

/**
 * The configuration manager interface is used to manage global configuration data.
 * @author Ardon
 * @date 2021-04-25
 */
public interface ConfigureManager {

	/**
	 * Perform data configuration immediately.
	 * @return Whether configurations are successful.
	 */
	boolean perform();

}
