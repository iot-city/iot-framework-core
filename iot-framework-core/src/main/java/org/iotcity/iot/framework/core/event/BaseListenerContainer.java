package org.iotcity.iot.framework.core.event;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic event listener container.
 * @param <T> The class type of event type.
 * @param <E> The event class type.
 * @author ardon
 * @date 2021-05-07
 */
final class BaseListenerContainer<T, E extends Event<T>> {

	// --------------------------- Private object fields ----------------------------

	/**
	 * Lock for modified state.
	 */
	private final Object lock = new Object();
	/**
	 * The listener map.
	 */
	private final Map<EventListener<T, E>, ListenerObject> map = new HashMap<>();
	/**
	 * The listener array.
	 */
	private ListenerObject[] listeners = null;
	/**
	 * The listener data status for modifying.
	 */
	private boolean modified = true;

	// --------------------------- EventListener comparator ----------------------------

	/**
	 * Event listener comparator for priority (the priority with the highest value is called first).
	 */
	private final Comparator<ListenerObject> COMPARATOR = new Comparator<ListenerObject>() {

		@Override
		public int compare(BaseListenerContainer<T, E>.ListenerObject o1, BaseListenerContainer<T, E>.ListenerObject o2) {
			if (o1.priority == o2.priority) return 0;
			return o1.priority < o2.priority ? 1 : -1;
		}

	};

	// --------------------------- Friendly methods ----------------------------

	/**
	 * Gets the listener size.
	 */
	int size() {
		return map.size();
	}

	/**
	 * Add a listener to the container.
	 * @param listener Event listener instance (not null).
	 * @param priority The execution order priority for the listener (the priority with the highest value is called first, 0 by default).
	 */
	void add(EventListener<T, E> listener, int priority) {
		synchronized (lock) {
			map.put(listener, new ListenerObject(listener, priority));
			if (!modified) modified = true;
		}
	}

	/**
	 * Determines whether the container contains the specified listener object.
	 * @param listener Event listener instance to be tested (not null).
	 * @return Returns true if the data has been found; otherwise, returns false.
	 */
	boolean contains(EventListener<T, E> listener) {
		return map.containsKey(listener);
	}

	/**
	 * Remove the listener from container.
	 * @param listener Event listener instance to be removed (not null).
	 * @return Returns true if the data has been found and removed; otherwise, returns false.
	 */
	boolean remove(EventListener<T, E> listener) {
		synchronized (lock) {
			if (map.remove(listener) != null) {
				if (!modified) modified = true;
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Gets listeners from container.
	 */
	ListenerObject[] listeners() {
		if (modified) {
			synchronized (lock) {
				if (modified) {
					@SuppressWarnings("unchecked")
					ListenerObject[] values = (ListenerObject[]) Array.newInstance(ListenerObject.class, map.size());
					values = map.values().toArray(values);
					Arrays.sort(values, COMPARATOR);
					listeners = values;
					modified = false;
				}
			}
		}
		return listeners;
	}

	// --------------------------- Inner object class ----------------------------

	/**
	 * Event listener object class for container.
	 * @author ardon
	 * @date 2021-05-08
	 */
	final class ListenerObject {

		/**
		 * Event listener instance (not null).
		 */
		final EventListener<T, E> listener;
		/**
		 * The execution order priority for the listener (the priority with the highest value is called first, 0 by default).
		 */
		final int priority;

		/**
		 * Constructor for listener object class.
		 * @param listener Event listener instance.
		 * @param priority The execution order priority for the listener (the priority with the highest value is called first, 0 by default).
		 */
		ListenerObject(EventListener<T, E> listener, int priority) {
			this.listener = listener;
			this.priority = priority;
		}

	}

}
