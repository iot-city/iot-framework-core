package org.iotcity.iot.framework.core.beans;

/**
 * The thread pool factory is used to provide thread support objects for multithreading calls.
 * @param <T> The data type used to determine which thread support object can be obtained.
 * @author Ardon
 * @date 2021-04-24
 */
public interface ThreadPoolFactory<T> {

	/**
	 * Gets a thread pool support object by specified data.
	 * @param data Used to determine which thread support object can be obtained.
	 * @return Thread pool support object.
	 */
	ThreadPoolSupport getPoolSupport(T data);

}
