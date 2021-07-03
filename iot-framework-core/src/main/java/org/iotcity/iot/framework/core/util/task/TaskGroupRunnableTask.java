package org.iotcity.iot.framework.core.util.task;

import org.iotcity.iot.framework.core.FrameworkCore;

/**
 * The task group runnable task object.
 * @author ardon
 * @date 2021-07-01
 */
public class TaskGroupRunnableTask<T> extends PriorityRunnable {

	/**
	 * The task group waiting to be executed using multithreading.
	 */
	private final TaskGroupDataContext<T> group;
	/**
	 * The task execution callback object.
	 */
	private final TaskGroupTaskCallback callback;
	/**
	 * The data index in data array.
	 */
	private final int index;
	/**
	 * The current task data object.
	 */
	private final T data;

	/**
	 * Constructor for task group runnable task object.
	 * @param group The task group waiting to be executed using multithreading.
	 * @param index The data index in data array.
	 * @param data The current task data object.
	 * @param callback The task execution callback object.
	 * @param priority The runnable execution priority.
	 */
	public TaskGroupRunnableTask(TaskGroupDataContext<T> group, int index, T data, TaskGroupTaskCallback callback, int priority) {
		super(priority);
		this.group = group;
		this.index = index;
		this.data = data;
		this.callback = callback;
	}

	@Override
	public void run() {
		// Run task and callback the execution result.
		boolean success;
		try {
			success = group.run(index, data);
		} catch (Exception e) {
			FrameworkCore.getLogger().error(e);
			success = false;
		}
		callback.onExecuted(success);
	}

}
