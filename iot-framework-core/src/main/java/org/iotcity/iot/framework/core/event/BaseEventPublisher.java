package org.iotcity.iot.framework.core.event;

import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.core.config.Configurable;

/**
 * Basic event publisher is used to publish base event data processing.
 * @param <T> The class type of event type.
 * @param <E> The event class type.
 * @author Ardon
 * @date 2021-04-24
 */
public class BaseEventPublisher<T, E extends Event<T>> implements EventPublisher<T, E>, Configurable<BaseEventConfig<T, E>[]> {

	// --------------------------- Private object fields ----------------------------

	/**
	 * Lock for listener map.
	 */
	private final Object lock = new Object();
	/**
	 * The listener container map (the key is the event type, the value is the listener container).
	 */
	private final Map<T, BaseListenerContainer<T, E>> map = new HashMap<>();

	// --------------------------- Public object methods ----------------------------

	@Override
	public boolean config(BaseEventConfig<T, E>[] data, boolean reset) {
		if (data == null) return false;
		if (reset) this.clearListeners();
		for (BaseEventConfig<T, E> config : data) {
			if (config == null) continue;
			addListener(config.type, config.listener, config.priority);
		}
		return true;
	}

	/**
	 * Gets the size for event types.
	 */
	public int getTypeSize() {
		return map.size();
	}

	/**
	 * Gets the listener size for specified event type.
	 * @param type The event type.
	 * @return The listener size.
	 */
	public int getListenerSize(T type) {
		BaseListenerContainer<T, E> context = map.get(type);
		if (context == null) return 0;
		return context.size();
	}

	/**
	 * Add a listener to publisher, the priority value is set to 0 by default.
	 * @param type The event type.
	 * @param listener The event listener object.
	 */
	public void addListener(T type, EventListener<T, E> listener) {
		this.addListener(type, listener, 0);
	}

	/**
	 * Add a listener to publisher.
	 * @param type The event type (required, not null).
	 * @param listener The event listener object (required, not null).
	 * @param priority The execution order priority for the listener (the priority with the highest value is called first, 0 by default).
	 */
	public void addListener(T type, EventListener<T, E> listener, int priority) {
		if (type == null || listener == null) return;
		BaseListenerContainer<T, E> context = map.get(type);
		if (context == null) {
			synchronized (lock) {
				context = map.get(type);
				if (context == null) {
					context = new BaseListenerContainer<T, E>();
					map.put(type, context);
				}
			}
		}
		context.add(listener, priority);
	}

	/**
	 * Determines whether the publisher contains the specified event type.
	 * @param type The event type (required, not null).
	 * @return Returns true if the event type has been found; otherwise, returns false.
	 */
	public boolean containsType(T type) {
		return map.containsKey(type);
	}

	/**
	 * Determines whether the publisher contains the specified event type and listener.
	 * @param type The event type (required, not null).
	 * @param listener The event listener object (required, not null).
	 * @return Returns true if the event type and the listener has been found; otherwise, returns false.
	 */
	public boolean containsListener(T type, EventListener<T, E> listener) {
		if (type == null || listener == null) return false;
		BaseListenerContainer<T, E> context = map.get(type);
		return context == null ? false : context.contains(listener);
	}

	/**
	 * Remove a listener from publisher by specified event type.
	 * @param type The event type (required, not null).
	 * @param listener The event listener object to be removed (required, not null).
	 * @return Returns true if the listener has been found and removed; otherwise, returns false.
	 */
	public boolean removeListener(T type, EventListener<T, E> listener) {
		if (type == null || listener == null) return false;
		BaseListenerContainer<T, E> context = map.get(type);
		return context == null ? false : context.remove(listener);
	}

	/**
	 * Remove listeners by specified event type.
	 * @param type The event type (required, not null).
	 */
	public void removeListeners(T type) {
		if (type == null) return;
		synchronized (lock) {
			map.remove(type);
		}
	}

	/**
	 * Clear all listeners in publisher.
	 */
	public void clearListeners() {
		if (map.size() == 0) return;
		synchronized (lock) {
			map.clear();
		}
	}

	@Override
	public int publish(E event) throws IllegalArgumentException {
		if (event == null) throw new IllegalArgumentException("Parameter event can not be null!");
		T type = event.getType();
		if (type == null) return 0;
		BaseListenerContainer<T, E> context = map.get(type);
		if (context == null) return 0;
		BaseListenerContainer<T, E>.ListenerObject[] listeners = context.listeners();
		if (listeners.length == 0) return 0;
		int count = 0;
		for (BaseListenerContainer<T, E>.ListenerObject object : listeners) {
			if (event.isStopped()) break;
			if (object.listener.onEvent(event)) {
				count++;
			}
		}
		return count;
	}

}
