package org.iotcity.iot.framework.core.beans;

/**
 * Thread pool support interface.
 * @author Ardon
 * @date 2021-04-24
 */
public interface ThreadPoolSupport {

	/**
	 * Use the thread pool to execute runnable object immediately.
	 * @param runnable Runnable object to be execute.
	 * @param postmen Who send thread local data from current thread to the runnable thread.
	 * @return If runnable object cannot be submitted for execution, it returns false; otherwise, it returns true.
	 */
	boolean run(final Runnable runnable, final ThreadLocalPostman... postmen);

}
