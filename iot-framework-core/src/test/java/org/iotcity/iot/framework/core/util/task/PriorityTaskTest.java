package org.iotcity.iot.framework.core.util.task;

import java.util.concurrent.atomic.AtomicInteger;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.logging.Logger;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-06-30
 */
public class PriorityTaskTest extends TestCase {

	private final Logger logger = FrameworkCore.getLogger();
	private final Object lock = new Object();

	public void testPriority1() {

		IoTFramework.init();

		System.out.println("-------------------- TEST TASK PRIORITY 1 --------------------");

		TaskHandler handler = new TaskHandler("PRIORITY-TEST", 1, 2, 60, 10);

		AtomicInteger total = new AtomicInteger();
		AtomicInteger fails = new AtomicInteger();
		AtomicInteger counter = new AtomicInteger();
		for (int i = 0, c = 100; i < c; i++) {
			final int num = i + 1;
			if (!handler.run(new Runnable() {

				@Override
				public void run() {
					int count = counter.incrementAndGet();
					logger.info("Running task-" + num + "...");
					if (count >= total.get()) {
						synchronized (lock) {
							lock.notify();
						}
						logger.info("Total runs: " + count);
					}
				}

			}, i)) {
				fails.incrementAndGet();
				logger.warn("Submit task-" + num + " failed.");
			} else {
				total.incrementAndGet();
			}
		}
		logger.info("Total submitted tasks: " + total.get() + ", total fails: " + fails.get());

		// --------------------------- LOCK FOR TEST FINISH ------------------------

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info("All tests runs in task handler finished.");
		assertTrue(true);

	}

	public void testPriority2() {

		IoTFramework.init();

		System.out.println("-------------------- TEST TASK PRIORITY 2 --------------------");

		TaskHandler handler = new TaskHandler("PRIORITY-TEST", 0, 2, 60, 0);

		AtomicInteger total = new AtomicInteger();
		AtomicInteger fails = new AtomicInteger();
		AtomicInteger counter = new AtomicInteger();
		for (int i = 0, c = 100; i < c; i++) {
			final int num = i + 1;
			if (!handler.run(new Runnable() {

				@Override
				public void run() {
					int count = counter.incrementAndGet();
					logger.info("Running task-" + num + "...");
					if (count >= total.get()) {
						synchronized (lock) {
							lock.notify();
						}
						logger.info("Total runs: " + count);
					}
				}

			}, i)) {
				fails.incrementAndGet();
				logger.warn("Submit task-" + num + " failed.");
			} else {
				total.incrementAndGet();
			}
		}
		logger.info("Total submitted tasks: " + total.get() + ", total fails: " + fails.get());

		// --------------------------- LOCK FOR TEST FINISH ------------------------

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info("All tests runs in task handler finished.");
		assertTrue(true);

	}

	public void testNoPriority() {

		IoTFramework.init();

		System.out.println("-------------------- TEST TASK NO PRIORITY --------------------");

		TaskHandler handler = new TaskHandler("PRIORITY-TEST", 0, 2, 60, 0);

		AtomicInteger total = new AtomicInteger();
		AtomicInteger fails = new AtomicInteger();
		AtomicInteger counter = new AtomicInteger();
		for (int i = 0, c = 100; i < c; i++) {
			final int num = i + 1;
			if (!handler.run(new Runnable() {

				@Override
				public void run() {
					int count = counter.incrementAndGet();
					logger.info("Running task-" + num + "...");
					if (count >= total.get()) {
						synchronized (lock) {
							lock.notify();
						}
						logger.info("Total runs: " + count);
					}
				}

			})) {
				fails.incrementAndGet();
				logger.warn("Submit task-" + num + " failed.");
			} else {
				total.incrementAndGet();
			}
		}
		logger.info("Total submitted tasks: " + total.get() + ", total fails: " + fails.get());

		// --------------------------- LOCK FOR TEST FINISH ------------------------

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info("All tests runs in task handler finished.");
		assertTrue(true);

	}

}
