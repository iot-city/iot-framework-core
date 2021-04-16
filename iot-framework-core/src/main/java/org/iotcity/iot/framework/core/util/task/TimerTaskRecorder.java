package org.iotcity.iot.framework.core.util.task;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * The timer recorder is used to record the execution time of tasks.
 * @author Ardon
 */
final class TimerTaskRecorder {

	// --------------------------- Static fields ----------------------------

	/**
	 * Maximum range of system time changes (millisecond).
	 */
	private final static long TIME_CHANGED_LIMITED = 60 * 1000;
	/**
	 * Maximum number of records.
	 */
	private final static long MAX_RECORDS = 60;

	// --------------------------- Private fields ----------------------------

	/**
	 * Execution time of tasks.
	 */
	private LinkedList<TimeRecord> records = new LinkedList<>();
	/**
	 * System time has changed status.
	 */
	private boolean timeChanged = false;

	// --------------------------- Friendly fields ----------------------------

	/**
	 * The times of task detection in the main loop.
	 */
	long loopCount = 0;
	/**
	 * Maximum execution time in all records.
	 */
	long maxExecTime = 0;
	/**
	 * Minimum execution time in all records.
	 */
	long minExecTime = 0;
	/**
	 * Average execution time of 60% data in the middle of records.
	 */
	long avgExecTime = 0;
	/**
	 * Average waiting time of 60% data in the middle of records.
	 */
	long avgWaitTime = 0;

	// --------------------------- Comparator method ----------------------------

	/**
	 * The record comparator for array sort.
	 */
	private final static Comparator<TimeRecord> RECORD_COMPARATOR = new Comparator<TimeRecord>() {

		@Override
		public int compare(TimeRecord o1, TimeRecord o2) {
			return o1.execTime > o2.execTime ? 1 : (o1.execTime < o2.execTime ? -1 : 0);
		}

	};

	// --------------------------- Friendly methods ----------------------------

	/**
	 * Get records size.
	 * @return Records size.
	 */
	int size() {
		return records.size();
	}

	/**
	 * Record the task execution time.
	 * @param startTime Start time of task execution.
	 * @param endTime End time of task execution.
	 * @param waitTime Time to wait for next execution.
	 * @param currentTime Current system time.
	 */
	void record(long startTime, long endTime, long waitTime, long currentTime) {
		// Increase the number of times
		loopCount++;
		// Check time, if end time less then start time, system time has changed
		if (endTime < startTime || currentTime < endTime || currentTime < startTime) {
			// Set changed
			timeChanged = true;
		} else {
			// Limit the max records
			if (records.size() >= MAX_RECORDS) {
				// Remove first one
				records.poll();
			}
			// Add to records
			records.offer(new TimeRecord(startTime, endTime, waitTime, currentTime));
		}
	}

	/**
	 * Determine whether the system time has changed.
	 * @param startTime Start time of task execution.
	 * @param waitTime Time to wait for next execution.
	 * @param currentTime Current system time.
	 * @return Whether the system time has changed.
	 */
	boolean systemTimeChanged(long startTime, long waitTime, long currentTime) {
		// Check changed state
		if (timeChanged) {
			// Reset state
			timeChanged = false;
			// Return changed
			return true;
		}

		// Get average time of records
		int length = records.size();
		if (length == 0) return false;

		// Get record array
		TimeRecord[] array = records.toArray(new TimeRecord[length]);
		// Sort array
		Arrays.sort(array, RECORD_COMPARATOR);
		// Get data range
		int min = ((Double) Math.floor(length * 0.2)).intValue();
		int max = ((Double) Math.ceil(length * 0.8)).intValue();

		// Initialize sum time
		long sumExecTime = 0;
		long sumWaitTime = 0;
		// Reset data
		maxExecTime = 0;
		minExecTime = Long.MAX_VALUE;
		// Traversal records
		for (int i = 0; i < length; i++) {
			// Get record data
			TimeRecord record = array[i];
			// Calculate sum time
			if (i >= min && i < max) {
				sumExecTime += record.execTime;
				sumWaitTime += record.waitTime;
			}
			// Get max execution time
			if (record.execTime > maxExecTime) maxExecTime = record.execTime;
			// Get min execution time
			if (record.execTime < minExecTime) minExecTime = record.execTime;
		}
		// Fix min execution time
		if (minExecTime == Long.MAX_VALUE) minExecTime = 0;
		// Get average time
		avgExecTime = sumExecTime / (max - min);
		avgWaitTime = sumWaitTime / (max - min);

		// Get the actual usage time
		long actual = currentTime - startTime;
		// Get normal usage time
		long normal = avgExecTime + waitTime;
		// Return true if it's mutation time
		return Math.abs(actual - normal) > TIME_CHANGED_LIMITED;

	}

	// --------------------------- Inner data class ----------------------------

	/**
	 * Execution time data.
	 * @author Ardon
	 */
	final class TimeRecord {

		/**
		 * Start time of task execution.
		 */
		final long startTime;
		/**
		 * End time of task execution.
		 */
		final long endTime;
		/**
		 * Tasks execution time.
		 */
		final long execTime;
		/**
		 * Time to wait for next execution.
		 */
		final long waitTime;
		/**
		 * The system time of current record.
		 */
		final long recordTime;

		/**
		 * Constructor for execution time data.
		 * @param startTime Start time of task execution.
		 * @param endTime End time of task execution.
		 * @param waitTime Time to wait for next execution.
		 * @param recordTime Time of current record.
		 */
		TimeRecord(long startTime, long endTime, long waitTime, long recordTime) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.execTime = endTime - startTime;
			this.waitTime = waitTime;
			this.recordTime = recordTime;
		}

	}

}
