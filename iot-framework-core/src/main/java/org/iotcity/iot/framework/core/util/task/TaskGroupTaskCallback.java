package org.iotcity.iot.framework.core.util.task;

/**
 * The task execution callback object.
 * @author ardon
 * @date 2021-07-01
 */
public interface TaskGroupTaskCallback {

	/**
	 * Task execution completion notification method.
	 * @param success Whether the execution was successful or not.
	 */
	void onExecuted(boolean success);

}
