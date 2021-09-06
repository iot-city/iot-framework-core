package org.iotcity.iot.framework.core.util.task;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The named thread factory.
 * @author ardon
 * @date 2021-09-06
 */
public final class NamedThreadFactory implements ThreadFactory {

	/**
	 * The pool number.
	 */
	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	/**
	 * The thread group.
	 */
	private final ThreadGroup group;
	/**
	 * The thread number of factory.
	 */
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	/**
	 * The prefix of thread name.
	 */
	private final String namePrefix;

	/**
	 * Constructor for named thread factory.
	 * @param name The factory name.
	 */
	public NamedThreadFactory(String name) {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = name + "-" + poolNumber.getAndIncrement() + "-";
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
		if (t.isDaemon()) t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}

}
