package org.iotcity.iot.framework.core.util.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Timer task queue object.
 * @author Ardon
 */
final class TimerTaskQueue {

	/**
	 * The maximum waiting time for main loop
	 */
	private final static long MAX_WAIT_TIME = 60 * 60 * 1000;

	// --------------------------- Private fields ----------------------------

	/**
	 * The task atomic id.
	 */
	private final AtomicLong atoID = new AtomicLong(0);
	/**
	 * The task map.
	 */
	private final Map<Long, TimerTask> tasks = new HashMap<>();
	/**
	 * Whether the task map has changed
	 */
	private boolean taskChanged = false;
	/**
	 * The tasks ready to be executed.
	 */
	private TimerTask[] readys = new TimerTask[0];
	/**
	 * The next execution time of schedule tasks.
	 */
	private long scheduleTime = Long.MAX_VALUE;
	/**
	 * The next execution time for new task.
	 */
	private long nextTimeForAdding = Long.MAX_VALUE;

	// --------------------------- Ready tasks methods ----------------------------

	/**
	 * Get the task data to be executed.
	 * @param currentTime Current system time.
	 * @return The timer task array to be executed.
	 */
	TimerTask[] getReadyTasks(long currentTime) {

		// Check whether the tasks has been changed
		if (taskChanged) {
			// Rebuild ready tasks
			synchronized (tasks) {
				if (taskChanged) {
					// Reset change mark
					taskChanged = false;
					// Get new task array for loop
					readys = tasks.values().toArray(new TimerTask[tasks.size()]);
					// Reset next time for adding task after get the new ready tasks
					nextTimeForAdding = Long.MAX_VALUE;
				}
			}
		}

		// Get a copy of ready tasks
		TimerTask[] redyTasks = readys;
		// Create a execution list
		List<TimerTask> list = new ArrayList<>();

		// Reset schedule time (this variable is safety in main loop)
		scheduleTime = Long.MAX_VALUE;
		// Traverse tasks
		for (TimerTask task : redyTasks) {
			if (task.isReady(currentTime)) {
				// Add ready task to list
				list.add(task);
			} else {
				// Get next minimum time of other tasks
				long nextTime = task.getNextTime();
				if (nextTime < scheduleTime) {
					scheduleTime = nextTime;
				}
			}
		}

		// Return tasks to be executed
		return list.toArray(new TimerTask[list.size()]);

	}

	/**
	 * Update the schedule time to minimum time.
	 * @param task Timer task object.
	 */
	void updateNextRunTime(TimerTask task) {
		// Get the next execution time
		long nextTime = task.getNextTime();
		// Get next minimum time of task
		if (nextTime < scheduleTime) {
			// Set to minimum time (this variable is safety in main loop)
			scheduleTime = nextTime;
		}
	}

	/**
	 * The next execution time of all tasks.
	 * @param currentTime Current system time.
	 * @return Wait time for the lock.
	 */
	long getWaitTime() {
		// Lock for add task method
		synchronized (tasks) {
			// Set schedule time to new task time
			if (nextTimeForAdding < scheduleTime) {
				scheduleTime = nextTimeForAdding;
			}
		}
		// Wait for notify if no task
		if (scheduleTime == Long.MAX_VALUE) return 0;
		// Get current time
		long currentTime = System.currentTimeMillis();
		// Returns the waiting time
		if (scheduleTime > currentTime) {
			// Get waiting time
			long schedule = scheduleTime - currentTime;
			// Return the fix time using maximum time
			return schedule > MAX_WAIT_TIME ? MAX_WAIT_TIME : schedule;
		} else {
			// Lock for 1 milliseconds
			return 1;
		}
	}

	// --------------------------- Task management methods ----------------------------

	/**
	 * Add a task to be executed after the specified delay time, and then execute according to each specified period.<br/>
	 * The maximum number of times the task runs does not exceed the number of executions.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param period Time in milliseconds between successive task executions (greater than 0).
	 * @param executions Maximum number of tasks executed (greater than 0).
	 * @return long Returns a task ID (sequence number).
	 */
	long add(Runnable task, long delay, long period, long executions) {

		// Get next task id
		long id = atoID.incrementAndGet();
		long currentTime = System.currentTimeMillis();
		// Create the timer task object
		TimerTask ttask = new TimerTask(this, id, task, delay, period, executions, currentTime);

		// Whether need to notify main loop
		boolean notify = false;
		// Get a task lock
		synchronized (tasks) {
			// Put to the map
			tasks.put(id, ttask);
			// Set to changed
			if (!taskChanged) taskChanged = true;
			// Get the next execution time
			long nextTime = ttask.getNextTime();
			// Notify loop if waiting time changes
			if (nextTime < nextTimeForAdding) {
				// Set to minimum time
				nextTimeForAdding = nextTime;
				// If the new time less then wait time of main loop
				if (nextTimeForAdding < scheduleTime) {
					// Need to notify main loop
					notify = true;
				}
			}
		}

		// Notify main loop
		if (notify) {
			synchronized (this) {
				this.notifyAll();
			}
		}

		// Return id
		return id;
	}

	/**
	 * Determine whether the timer task exists.
	 * @param taskID The timer task sequence number returned when adding.
	 * @return boolean whether the task exists.
	 */
	boolean contains(long taskID) {
		return tasks.containsKey(taskID);
	}

	/**
	 * Remove a timer task from task queue of handler.
	 * @param taskID The timer task sequence number returned when adding.
	 * @return The task that has been removed, returns null when it does not exist.
	 */
	Runnable remove(long taskID) {
		synchronized (tasks) {
			// Remove timer task
			TimerTask task = tasks.remove(taskID);
			// If no task be removed, return
			if (task == null) return null;
			// Set to changed
			if (!taskChanged) taskChanged = true;
			// Return runnable object
			return task.getRunner();
		}
	}

	/**
	 * Remove all timer tasks
	 */
	void clear() {
		synchronized (tasks) {
			// Check size
			if (tasks.size() == 0) return;
			// Clear the map
			tasks.clear();
			// Set to changed
			if (!taskChanged) taskChanged = true;
		}
	}

}
