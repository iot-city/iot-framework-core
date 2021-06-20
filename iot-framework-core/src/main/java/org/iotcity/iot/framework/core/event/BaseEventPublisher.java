package org.iotcity.iot.framework.core.event;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.config.Configurable;

/**
 * Basic event publisher is used to publish base event data processing.
 * @param <T> The class type of event type.
 * @param <E> The event class type.
 * @param <L> The listener class type.
 * @param <F> The listener factory class type.
 * @author Ardon
 * @date 2021-04-24
 */
public abstract class BaseEventPublisher<T, E extends Event<T>, L extends EventListener<T, E>, F extends EventListenerFactory<T, E, L>> implements EventPublisher<T, E>, Configurable<BaseEventConfig<T, E, L>[]> {

	// --------------------------- Private object fields ----------------------------

	/**
	 * The actual class of type.
	 */
	protected final Type typeClass;
	/**
	 * The actual class of event.
	 */
	protected final Class<E> eventClass;
	/**
	 * The actual class of listener.
	 */
	protected final Class<L> listenerClass;
	/**
	 * Lock for listener map.
	 */
	private final Object lock = new Object();
	/**
	 * The listener container map (the key is the event type, the value is the listener container).
	 */
	private final Map<T, BaseListenerContainer<T, E, L>> map = new HashMap<>();
	/**
	 * The event listener to create an event listener (optional, it can be set to null when using {@link IoTFramework }.getGlobalInstanceFactory() to create an instance).
	 */
	private F factory = null;

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

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for event publisher.
	 */
	@SuppressWarnings("unchecked")
	public BaseEventPublisher() {
		Type[] types = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
		typeClass = types[0];
		eventClass = (Class<E>) types[1];
		listenerClass = (Class<L>) types[2];
	}

	// --------------------------- Listener factory methods ----------------------------

	/**
	 * Set the event listener factory to create an event listener.
	 * @param factory The event listener factory (it can be set to null when using {@link IoTFramework }.getGlobalInstanceFactory() to create an instance).
	 */
	public void setListenerFactory(F factory) {
		this.factory = factory;
	}

	/**
	 * Gets the event listener factory in publisher.<br/>
	 * It can be null value when using {@link IoTFramework }.getGlobalInstanceFactory() to create an instance.
	 * @return The event listener factory.
	 */
	public F getListenerFactory() {
		return factory;
	}

	/**
	 * Gets a listener instance from factory for specified event type (returns null when no matching listener is found).
	 * @param type The type of data event to listen on.
	 * @return Event listener instance to process the event data after receiving the event.
	 */
	public abstract L getListenerInstanceFromFactory(T type);

	// --------------------------- Public object methods ----------------------------

	@Override
	public boolean config(BaseEventConfig<T, E, L>[] data, boolean reset) {
		if (data == null) return false;
		if (reset) this.clearListeners();
		for (BaseEventConfig<T, E, L> config : data) {
			if (config == null) continue;
			addListener(config.type, config.listener, config.priority, config.filterEventClass);
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
		BaseListenerContainer<T, E, L> context = map.get(type);
		if (context == null) return 0;
		return context.size();
	}

	/**
	 * Add a listener to publisher, the priority value is set to 0 by default.
	 * @param type The event type.
	 * @param listener The event listener object.
	 */
	public void addListener(T type, L listener) {
		this.addListener(type, listener, 0, null);
	}

	/**
	 * Add a listener to publisher, the priority value is set to 0 by default.
	 * @param type The event type.
	 * @param listener The event listener object.
	 * @param priority The execution order priority for the listener (optional, the priority with the highest value is called first, 0 by default).
	 */
	public void addListener(T type, L listener, int priority) {
		this.addListener(type, listener, priority, null);
	}

	/**
	 * Add a listener to publisher.
	 * @param type The event type (required, not null).
	 * @param listener The event listener object (required, not null).
	 * @param priority The execution order priority for the listener (optional, the priority with the highest value is called first, 0 by default).
	 * @param filterEventClass Specifies the class type of event to listen on (optional, the listener will respond to events of the currently specified event type and inherited subclass types).
	 */
	public void addListener(T type, L listener, int priority, Class<? extends E> filterEventClass) {
		if (type == null || listener == null) return;
		BaseListenerContainer<T, E, L> context = map.get(type);
		if (context == null) {
			synchronized (lock) {
				context = map.get(type);
				if (context == null) {
					context = new BaseListenerContainer<T, E, L>();
					map.put(type, context);
				}
			}
		}
		context.add(listener, priority, filterEventClass);
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
	public boolean containsListener(T type, L listener) {
		if (type == null || listener == null) return false;
		BaseListenerContainer<T, E, L> context = map.get(type);
		return context == null ? false : context.contains(listener);
	}

	/**
	 * Get listeners by specified type (returns null when no matching listener is found).
	 * @param type The event type (required, not null).
	 * @return Event listeners to process the event data after receiving the event.
	 */
	public L[] getListeners(T type) {
		BaseListenerObject<T, E, L>[] objs = getTypeListeners(type);
		if (objs == null || objs.length == 0) return null;
		@SuppressWarnings("unchecked")
		L[] listeners = (L[]) Array.newInstance(listenerClass, objs.length);
		for (int i = 0, c = objs.length; i < c; i++) {
			BaseListenerObject<T, E, L> obj = objs[i];
			listeners[i] = obj.listener;
		}
		return listeners;
	}

	/**
	 * Remove a listener from publisher by specified event type.
	 * @param type The event type (required, not null).
	 * @param listener The event listener object to be removed (required, not null).
	 * @return Returns true if the listener has been found and removed; otherwise, returns false.
	 */
	public boolean removeListener(T type, L listener) {
		if (type == null || listener == null) return false;
		BaseListenerContainer<T, E, L> context = map.get(type);
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
		BaseListenerObject<T, E, L>[] listeners = getTypeListeners(event.getType());
		if (listeners == null) return 0;
		int count = 0;
		Class<?> eventClass = event.getClass();
		for (BaseListenerObject<T, E, L> object : listeners) {
			if (event.isStopped()) break;
			if (object.filterEventClass != null && !object.filterEventClass.isAssignableFrom(eventClass)) {
				continue;
			}
			if (object.listener.onEvent(event)) {
				count++;
			}
		}
		return count;
	}

	// --------------------------- Protected object methods ----------------------------

	/**
	 * Get listeners by specified type (returns null if invalid).
	 * @param type The event type.
	 * @return Listeners array.
	 */
	protected BaseListenerObject<T, E, L>[] getTypeListeners(T type) {
		if (type == null) return null;
		BaseListenerContainer<T, E, L> context = map.get(type);
		return context == null ? null : context.listeners();
	}

	/**
	 * Get class event listeners by type (returns including super class listeners, not null).
	 * @param type The class event type.
	 * @return Listeners list.
	 */
	protected List<BaseListenerObject<T, E, L>> getClassListeners(Class<?> type) {
		List<BaseListenerObject<T, E, L>> list = new ArrayList<>();
		int typesCount = 0;
		while (type != null) {
			BaseListenerContainer<T, E, L> context = map.get(type);
			if (context != null) {
				BaseListenerObject<T, E, L>[] listeners = context.listeners();
				if (listeners.length > 0) {
					typesCount++;
					list.addAll(Arrays.asList(listeners));
				}
			}
			type = type.getSuperclass();
		}
		if (typesCount > 1) list.sort(COMPARATOR);
		return list;
	}

}
