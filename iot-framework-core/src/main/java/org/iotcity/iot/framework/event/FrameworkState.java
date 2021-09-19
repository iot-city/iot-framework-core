package org.iotcity.iot.framework.event;

import org.iotcity.iot.framework.IoTFramework;

/**
 * The framework state definition enumeration.
 * @author ardon
 * @date 2021-09-19
 */
public enum FrameworkState {

	/**
	 * Step 1, all configuration data has been configured after calling {@link IoTFramework#init()} and initializing completed.
	 */
	INITIALIZED,
	/**
	 * Step 2, the system is scheduling shutdown before releasing resources and exiting.
	 */
	SHUTTINGDOWN,
	/**
	 * Step 3, exit the system, this is the final state of the framework.
	 */
	EXIT,

}
