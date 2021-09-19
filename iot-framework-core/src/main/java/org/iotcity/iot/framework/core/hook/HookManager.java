package org.iotcity.iot.framework.core.hook;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.core.bus.BusEventPublisher;
import org.iotcity.iot.framework.core.i18n.LocaleText;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.event.FrameworkEventData;
import org.iotcity.iot.framework.event.FrameworkState;

/**
 * The system shutdown hook manager.
 * @author ardon
 * @date 2021-09-18
 */
public final class HookManager {

	/**
	 * The look listeners.
	 */
	private final Map<HookListener, HookListenerObject> listeners = new ConcurrentHashMap<>();

	/**
	 * Constructor for system shutdown hook manager.
	 */
	public HookManager() {
		// Gets the hook manager.
		final HookManager hook = this;
		// Add a shutdown hook.
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				// Gets logger and locale objects.
				Logger logger = FrameworkCore.getLogger();
				LocaleText locale = FrameworkCore.getLocale();
				// Logs a message.
				logger.info(locale.text("core.hook.shuttingdown"));

				// Publish a shutting down event.
				BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
				publisher.publish(new BusEvent(hook, new FrameworkEventData(FrameworkState.SHUTTINGDOWN), false));

				// Gets the listener objects.
				HookListenerObject[] objs = listeners.values().toArray(new HookListenerObject[listeners.size()]);
				// Array sort.
				Arrays.sort(objs, new Comparator<HookListenerObject>() {

					@Override
					public int compare(HookListenerObject o1, HookListenerObject o2) {
						if (o1.priority == o2.priority) return 0;
						return o1.priority < o2.priority ? 1 : -1;
					}

				});

				// Execute listeners.
				for (HookListenerObject obj : objs) {
					// Execute object.
					try {
						obj.listener.onShuttingDown();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				// Publish an exit event.
				publisher.publish(new BusEvent(hook, new FrameworkEventData(FrameworkState.EXIT), false));

				// Sleep for 1s.
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}

				// Logs a message.
				logger.info(locale.text("core.hook.shutteddown"));
				// Flush logger buffer.
				logger.flush();

			}

		});
	}

	/**
	 * Add a hook listener to the manager.
	 * @param lisenter The system shutting down listener.
	 * @param priority The execution order priority for the listener (optional, the priority with the highest value is called first, 0 by default).
	 */
	public void addHook(HookListener lisenter, int priority) {
		if (lisenter == null) return;
		listeners.put(lisenter, new HookListenerObject(lisenter, priority));
	}

	/**
	 * Remove a hook listener from the manager.
	 * @param lisenter The system shutting down listener.
	 */
	public void removeHook(HookListener lisenter) {
		if (lisenter == null) return;
		listeners.remove(lisenter);
	}

	/**
	 * Clear all hook listeners.
	 */
	public void clear() {
		listeners.clear();
	}

	/**
	 * Gets the hook listeners size.
	 */
	public int size() {
		return listeners.size();
	}

	/**
	 * The listener object for shutting down.
	 * @author ardon
	 * @date 2021-09-18
	 */
	final static class HookListenerObject {

		/**
		 * The system shutting down listener.
		 */
		final HookListener listener;
		/**
		 * The execution order priority for the listener (optional, the priority with the highest value is called first, 0 by default).
		 */
		final int priority;

		/**
		 * Constructor for listener object.
		 * @param listener The system shutting down listener.
		 * @param priority The execution order priority for the listener (optional, the priority with the highest value is called first, 0 by default).
		 */
		HookListenerObject(HookListener listener, int priority) {
			this.listener = listener;
			this.priority = priority;
		}

	}

}
