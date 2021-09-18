package org.iotcity.iot.framework.core.hook;

/**
 * The system shutting down listener.
 * @author ardon
 * @date 2021-09-18
 */
public interface HookListener {

	/**
	 * Called when the system is shutting down.
	 */
	void onShuttingDown();

}
