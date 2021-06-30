package org.iotcity.iot.framework.core.util.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Timer task queue object.
 * @author Ardon
 */
final class TimerTaskQueue {

	// --------------------------- Private fields ----------------------------

	/**
	 * The maximum waiting time for main loop
	 */
	private final static long MAX_WAIT_TIME = 1 * 60 * 1000;

	/**
	 * The task handler name
	 */
	private final String name;
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

	// --------------------------- Statistic fields ----------------------------

	/**
	 * Number of running tasks at current time.
	 */
	final AtomicLong runningTasks = new AtomicLong(0);
	/**
	 * Total execution times of all tasks.
	 */
	final AtomicLong totalExecuteCount = new AtomicLong(0);
	/**
	 * Total number of tasks finished.
	 */
	final AtomicLong totalFinished = new AtomicLong(0);
	/**
	 * Total number of times all tasks were run.
	 */
	final AtomicLong totalRunTimes = new AtomicLong(0);
	/**
	 * Total elapsed time of all tasks in milliseconds.
	 */
	final AtomicLong totalElapsedTime = new AtomicLong(0);

	// --------------------------- Comparator method ----------------------------

	/**
	 * The statistic comparator for sort.
	 */
	private final static Comparator<TimerTaskStatus> STATISTIC_COMPARATOR = new Comparator<TimerTaskStatus>() {

		@Override
		public int compare(TimerTaskStatus o1, TimerTaskStatus o2) {
			return o1.avgElapsedTImePerRun > o2.avgElapsedTImePerRun ? -1 : (o1.avgElapsedTImePerRun < o2.avgElapsedTImePerRun ? 1 : 0);
		}

	};

	// ------------------------------ Constructor -------------------------------

	/**
	 * Constructor for timer task queue object.
	 * @param name The task handler name.
	 */
	TimerTaskQueue(String name) {
		this.name = name;
	}

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
	 * The next execution time of all tasks (greater then 0).
	 * @param currentTime Current system time.
	 * @return Wait time for the lock.
	 */
	long getWaitTime(long currentTime) {
		// Lock for add task method
		synchronized (tasks) {
			// Set schedule time to new task time
			if (nextTimeForAdding < scheduleTime) {
				// Set to adding time
				scheduleTime = nextTimeForAdding;
			}
		}
		// Wait for notify if no task
		if (scheduleTime > currentTime) {
			// Get waiting time
			long schedule = scheduleTime - currentTime;
			// Return the fix time using maximum time
			return schedule > MAX_WAIT_TIME ? MAX_WAIT_TIME : schedule;
		} else {
			// Lock for 10 milliseconds
			return 10;
		}
	}

	/**
	 * Update task execution time after system time update
	 * @param changeMilliseconds Milliseconds that have changed.
	 * @param currentTime Current system time.
	 */
	void systemTimeUpdate(long changeMilliseconds, long currentTime) {
		// Lock for update
		synchronized (tasks) {
			// Traversal all tasks
			for (TimerTask task : this.tasks.values()) {
				// Update task execution time
				task.systemTimeUpdate(changeMilliseconds, currentTime);
			}
		}
	}

	// --------------------------- Friendly methods ----------------------------

	/**
	 * Gets the task handler name.
	 * @return Task handler name.
	 */
	String getName() {
		return name;
	}

	/**
	 * Get total tasks size.
	 * @return Task size.
	 */
	int size() {
		return tasks.size();
	}

	/**
	 * Gets timer task statistic data of all tasks (the returned data is not null).
	 * @return Timer task statistic data.
	 */
	TimerTaskStatistic getStatistic() {
		// Return statistic data
		return new TimerTaskStatistic(runningTasks.get(), totalExecuteCount.get(), totalFinished.get(), totalRunTimes.get(), totalElapsedTime.get());
	}

	/**
	 * Get the status data of all tasks (the returned data is not null).
	 * @return Tasks status data array.
	 */
	TimerTaskStatus[] getTaskStatus() {
		// Create list
		List<TimerTaskStatus> list = new ArrayList<>();
		// Lock for traversal
		synchronized (tasks) {
			// Traversal all tasks
			for (TimerTask task : this.tasks.values()) {
				// Get task status
				list.add(task.getStatus());
			}
		}
		// Return array data
		return list.toArray(new TimerTaskStatus[list.size()]);
	}

	/**
	 * Busiest tasks status data (the returned data is not null).
	 * @param amount Maximum number of data returned.
	 * @return Tasks status data array.
	 */
	TimerTaskStatus[] getBusyTaskStatus(int amount) {
		if (amount <= 0) return new TimerTaskStatus[0];
		// Create list
		List<TimerTaskStatus> list = new ArrayList<>();
		// Lock for traversal
		synchronized (tasks) {
			// Traversal all tasks
			for (TimerTask task : this.tasks.values()) {
				// Get task status
				list.add(task.getStatus());
			}
		}
		// Get list size and return data array
		int length = list.size();
		if (length > 0) {
			// Sort by average elapsed time
			list.sort(STATISTIC_COMPARATOR);
			// Get size
			int size = length < amount ? length : amount;
			// Return array
			return list.subList(0, size).toArray(new TimerTaskStatus[size]);
		} else {
			return new TimerTaskStatus[0];
		}
	}

	// --------------------------- Task management methods ----------------------------

	/**
	 * Add a task to be executed after the specified delay time, and then execute according to each specified period.<br/>
	 * The maximum number of times the task runs does not exceed the number of executions.
	 * @param name Task name, will be used for logging.
	 * @param task Task to be execute, this task will be executed in single thread mode within the thread pool.
	 * @param delay Delay in milliseconds before task is to be executed (greater than 0).
	 * @param period Time in milliseconds between successive task executions (greater than 0).
	 * @param executions Maximum number of tasks executed (greater than 0).
	 * @param priority The runnable execution priority (0 by default, the higher the value, the higher the priority, the higher value will be executed first).
	 * @return long Returns a task ID (sequence number).
	 */
	long add(String name, Runnable task, long delay, long period, long executions, int priority) {

		// Get next task id
		long id = atoID.incrementAndGet();
		if (StringHelper.isEmpty(name)) name = "TASK-" + id;
		long currentTime = System.currentTimeMillis();
		// Create the timer task object
		TimerTask ttask = new TimerTask(this, id, name, task, delay, period, executions, currentTime, priority);

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
	 * Gets the task ID of the last added task.
	 * @return The task ID.
	 */
	long getLastTaskID() {
		return atoID.get();
	}

	/**
	 * Gets a task by ID.
	 * @param taskID The timer task sequence number returned when adding.
	 * @return Task in queue.
	 */
	TimerTask get(long taskID) {
		return tasks.get(taskID);
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
