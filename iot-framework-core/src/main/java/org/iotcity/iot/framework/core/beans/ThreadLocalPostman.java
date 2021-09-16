package org.iotcity.iot.framework.core.beans;

/**
 * A postman who sends thread local data from one thread to another.
 * @author Ardon
 * @date 2021-04-25
 */
public interface ThreadLocalPostman {

	/**
	 * Store the local data of the thread that created the postman to the current thread.
	 */
	void storeToCurrentThread();

	/**
	 * Remove all variables of postman in current thread local.
	 */
	void removeAll();

}
