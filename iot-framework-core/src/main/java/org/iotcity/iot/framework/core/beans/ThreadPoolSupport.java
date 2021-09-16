package org.iotcity.iot.framework.core.beans;

import java.util.concurrent.ThreadPoolExecutor;

import org.iotcity.iot.framework.core.util.task.PriorityRunnable;

/**
 * Thread pool support interface.
 * @author Ardon
 * @date 2021-04-24
 */
public interface ThreadPoolSupport {

	/**
	 * Gets the thread pool execution object.
	 */
	ThreadPoolExecutor getThreadPoolExecutor();

	/**
	 * Use the thread pool to execute runnable object immediately.
	 * @param runnable Runnable object to be execute, e.g. an instance of {@link PriorityRunnable }.
	 * @return If runnable object cannot be submitted for execution, it returns false; otherwise, it returns true.
	 */
	boolean run(Runnable runnable);

	/**
	 * Use the thread pool to execute runnable object immediately.
	 * @param runnable Runnable object to be execute, e.g. an instance of {@link PriorityRunnable }.
	 * @param priority The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 * @return If runnable object cannot be submitted for execution, it returns false; otherwise, it returns true.
	 */
	boolean run(Runnable runnable, int priority);

	/**
	 * Use the thread pool to execute runnable object immediately.
	 * @param runnable Runnable object to be execute, e.g. an instance of {@link PriorityRunnable }.
	 * @param postmen Who send thread local data from current thread to the runnable thread.
	 * @return If runnable object cannot be submitted for execution, it returns false; otherwise, it returns true.
	 */
	boolean run(Runnable runnable, ThreadLocalPostman[] postmen);

	/**
	 * Use the thread pool to execute runnable object immediately.
	 * @param runnable Runnable object to be execute, e.g. an instance of {@link PriorityRunnable }.
	 * @param priority The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 * @param postmen Who send thread local data from current thread to the runnable thread.
	 * @return If runnable object cannot be submitted for execution, it returns false; otherwise, it returns true.
	 */
	boolean run(Runnable runnable, int priority, ThreadLocalPostman[] postmen);

}
