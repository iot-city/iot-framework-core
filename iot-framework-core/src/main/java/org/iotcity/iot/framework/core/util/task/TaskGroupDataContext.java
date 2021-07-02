package org.iotcity.iot.framework.core.util.task;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * The task group waiting to be executed using multithreading.
 * @author ardon
 * @date 2021-06-30
 */
public abstract class TaskGroupDataContext<T> {

	/**
	 * The array object waiting to be executed using multithreading (not null).
	 */
	private final Object source;
	/**
	 * The length of source array.
	 */
	private final int length;
	/**
	 * The index of the next task in the array.
	 */
	private int nextIndex = 0;

	/**
	 * Constructor for the task group of collection data.
	 * @param source The data collection waiting to be executed using multithreading (required, can not be null).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "source" is null.
	 */
	public TaskGroupDataContext(Collection<T> source) throws IllegalArgumentException {
		if (source == null) throw new IllegalArgumentException("Parameter source can not be null!");
		this.source = source.toArray();
		this.length = source.size();
	}

	/**
	 * Constructor for the task group of array data.
	 * @param source The data array waiting to be executed using multithreading (required, can not be null).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "source" is null.
	 */
	public TaskGroupDataContext(T[] source) throws IllegalArgumentException {
		if (source == null) throw new IllegalArgumentException("Parameter source can not be null!");
		this.source = source;
		this.length = source.length;
	}

	/**
	 * Gets the task size.
	 */
	public int getSize() {
		return length;
	}

	/**
	 * Set the next index of group task data array.
	 * @param index The index of the next task in the array.
	 */
	public void setNextIndex(int index) {
		if (index < 0) index = 0;
		this.nextIndex = index;
	}

	/**
	 * Determines whether there is next task data.
	 */
	public boolean hasNext() {
		return nextIndex < length;
	}

	/**
	 * Gets the next task in this task group (returns null if there is no next task).
	 * @param callback The task execution callback object.
	 * @return The task object of this group.
	 */
	public PriorityRunnable nextTask(TaskGroupTaskCallback callback) {
		// Check the array index.
		if (!hasNext()) return null;
		// Get next data index.
		int index = nextIndex++;
		// Gets the next data object.
		@SuppressWarnings("unchecked")
		final T data = (T) Array.get(source, index);
		// Return the runnable task.
		return new TaskGroupRunnableTask<T>(this, index, data, callback, getPriority(index, data));
	}

	/**
	 * Gets the execution priority of the data task.
	 * @param index The data index in data array.
	 * @param data The current task data object.
	 * @return The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 */
	public abstract int getPriority(int index, T data);

	/**
	 * Execute the current data processing task.
	 * @param index The data index in data array.
	 * @param data The current task data object.
	 * @return Returns whether the task was executed successfully.
	 */
	public abstract boolean run(int index, T data);

}
