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
 * @param <L> The listener class type.
 * @author ardon
 * @date 2021-05-07
 */
final class BaseListenerContainer<T, E extends Event<T>, L extends EventListener<T, E>> {

	// --------------------------- Private object fields ----------------------------

	/**
	 * Lock for modified state.
	 */
	private final Object lock = new Object();
	/**
	 * The listener map.
	 */
	private final Map<L, BaseListenerObject<T, E, L>> map = new HashMap<>();
	/**
	 * The listener array.
	 */
	private BaseListenerObject<T, E, L>[] listeners = null;
	/**
	 * The listener data status for modifying.
	 */
	private boolean modified = false;

	// --------------------------- EventListener comparator ----------------------------

	/**
	 * Event listener comparator for priority (the priority with the highest value is called first).
	 */
	private final Comparator<BaseListenerObject<T, E, L>> COMPARATOR = new Comparator<BaseListenerObject<T, E, L>>() {

		@Override
		public int compare(BaseListenerObject<T, E, L> o1, BaseListenerObject<T, E, L> o2) {
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
	 * @param priority The execution order priority for the listener (optional, the priority with the highest value is called first, 0 by default).
	 * @param filterEventClass Specifies the class type of event to listen on (optional, the listener will respond to events of the currently specified event type and inherited subclass types).
	 */
	void add(L listener, int priority, Class<? extends E> filterEventClass) {
		synchronized (lock) {
			map.put(listener, new BaseListenerObject<T, E, L>(listener, priority, filterEventClass));
			if (!modified) modified = true;
		}
	}

	/**
	 * Determines whether the container contains the specified listener object.
	 * @param listener Event listener instance to be tested (not null).
	 * @return Returns true if the data has been found; otherwise, returns false.
	 */
	boolean contains(L listener) {
		return map.containsKey(listener);
	}

	/**
	 * Remove the listener from container.
	 * @param listener Event listener instance to be removed (not null).
	 * @return Returns true if the data has been found and removed; otherwise, returns false.
	 */
	boolean remove(L listener) {
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
	 * Gets listeners from container (returns null if there is no listener).
	 */
	BaseListenerObject<T, E, L>[] getListeners() {
		if (modified) {
			synchronized (lock) {
				if (modified) {
					if (map.size() == 0) {
						listeners = null;
					} else {
						@SuppressWarnings("unchecked")
						BaseListenerObject<T, E, L>[] values = (BaseListenerObject<T, E, L>[]) Array.newInstance(BaseListenerObject.class, map.size());
						values = map.values().toArray(values);
						Arrays.sort(values, COMPARATOR);
						listeners = values;
					}
					modified = false;
				}
			}
		}
		return listeners;
	}

}
