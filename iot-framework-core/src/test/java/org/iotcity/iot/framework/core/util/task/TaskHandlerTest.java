package org.iotcity.iot.framework.core.util.task;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;

import junit.framework.TestCase;

/**
 * Test case for TaskHandler
 * @author Ardon
 */
public class TaskHandlerTest extends TestCase {

	private final Logger logger = FrameworkCore.getLogger();
	private final Object lock = new Object();
	private final AtomicLong total = new AtomicLong();
	private final static int TASKS = 20;
	private final static int RUNS = 20;

	/**
	 * Timer test case
	 */
	public void testTimer() {

		System.out.println("-------------------- TEST TASK HANDLER --------------------");

		TaskHandler handler = new TaskHandler();

		System.out.println("-------------------------- (RUN POOL TASK) ------------------------");

		logger.info(">> Run task test for thread pool.");
		handler.run(new Runnable() {

			@Override
			public void run() {
				logger.info("<< Run task test for thread pool ok.");
			}

		});

		System.out.println("-------------------------- (RUN TIMER TASK) ------------------------");

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 3);
		Date time = cal.getTime();
		logger.info(">> 1. Schedule timer task at time: " + ConvertHelper.formatDate(time, "yyyy-MM-dd HH:mm:ss.SSS"));
		handler.add(new Runnable() {

			@Override
			public void run() {
				logger.info("<< 1. Run timer task at time ok.");
				logger.info(">> 2. Schedule timer task with delay time: 1000 ms");
				handler.add(new Runnable() {

					@Override
					public void run() {
						logger.info("<< 2. Run timer task with delay ok.");
						logger.info(">> 3. Schedule timer task with delay time: 1000 ms, period: 2000 ms, executions: 2");
						handler.add(new Runnable() {

							private int count = 0;

							@Override
							public void run() {
								count++;
								logger.info("<< 3. Run timer task with delay, period, executions: " + count);
								if (count == 2) {
									logger.info("<< 3. Schedule timer task with delay time: 1000 ms, period: 2000 ms, executions: 2, ok.");
									logger.info(">> 4. Schedule timer 10 tasks with random delay time, period: 5000 ms.");
									for (int i = 0, c = TASKS; i < c; i++) {
										addMoreTasks(handler);
									}
								}
							}

						}, 1000, 2000, 2);
					}

				}, 1000);
			}

		}, time);

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

	private void addMoreTasks(TaskHandler handler) {
		final long delay = new Random().nextInt(10000);
		handler.add(new Runnable() {

			private int count = 0;

			@Override
			public void run() {
				long tc = total.incrementAndGet();
				count++;
				logger.info("<< 4. Run timer task with delay: " + delay + ", run times: " + count + ", total: " + tc);
				if (count == 20) {
					logger.info("<<<<< 4. Run timer task with delay: " + delay + ", run times: " + count + ", FINISHED...................");
				}
				if (tc == TASKS * RUNS) {
					logger.info("<<<<<<<<<<<<<<<<<<<<< ALL TASKS FINISHED...................");
					handler.add(new Runnable() {

						@Override
						public void run() {
							handler.destroy();
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							finish();
						}

					}, 3000);
				}
			}

		}, delay, 500, RUNS);
	}

	private void finish() {
		synchronized (lock) {
			lock.notifyAll();
		}
	}

}
