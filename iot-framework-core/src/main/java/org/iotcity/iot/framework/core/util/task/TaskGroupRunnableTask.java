package org.iotcity.iot.framework.core.util.task;

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
	 * The current task data object.
	 */
	private final T data;

	/**
	 * Constructor for task group runnable task object.
	 * @param group The task group waiting to be executed using multithreading.
	 * @param data The current task data object.
	 * @param callback The task execution callback object.
	 * @param priority The runnable execution priority.
	 */
	public TaskGroupRunnableTask(TaskGroupDataContext<T> group, T data, TaskGroupTaskCallback callback, int priority) {
		super(priority);
		this.group = group;
		this.data = data;
		this.callback = callback;
	}

	@Override
	public void run() {
		// Run task and callback the execution result.
		boolean success;
		try {
			success = group.run(data);
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		callback.onExecuted(success);
	}

}
