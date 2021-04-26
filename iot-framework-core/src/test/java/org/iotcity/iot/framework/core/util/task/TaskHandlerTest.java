package org.iotcity.iot.framework.core.util.task;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.logging.Logger;

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
	private final static int RUNS = 50;

	/**
	 * Timer test case
	 */
	public void testTimer() {

		IoTFramework.init();

		System.out.println("-------------------- TEST TASK HANDLER --------------------");

		TaskHandler handler = new TaskHandler();

		System.out.println("-------------------------- (RUN POOL TASK) ------------------------");

		logger.info(">> Run task test for thread pool.");
		handler.run(new Runnable() {

			@Override
			public void run() {
				logger.info("<< Run task test for thread pool ok.");

				System.out.println("-------------------------- (RUN TIMER TASK) ------------------------");

				long delay = handler.getDelayForEverySeconds(6);
				logger.info(">> 1. Schedule timer task with delay every: 6 seconds, delay time: " + delay);
				handler.add(new Runnable() {

					@Override
					public void run() {
						logger.info("<< 1. Run timer task with delay 6 seconds ok.");
						long delay = handler.getDelayForEverySeconds(5);
						logger.info(">> 2. Schedule timer task with delay every: 5 seconds, delay time: " + delay);
						handler.add(new Runnable() {

							@Override
							public void run() {
								logger.info("<< 2. Run timer task with delay 10 seconds ok.");
								logger.info(">> 3. Schedule timer task with delay time: 1000 ms, period: 2000 ms, executions: 2");
								handler.add(new Runnable() {

									private int count = 0;

									@Override
									public void run() {
										count++;
										logger.info("<< 3. Run timer task with delay, period, executions: " + count);
										if (count == 2) {
											logger.info("<< 3. Schedule timer task with delay time: 1000 ms, period: 2000 ms, executions: 2, ok.");

											handler.outputStatistic();

											logger.info(">> 4. Schedule timer 20 tasks with random delay time, period: 1000 ms.");
											for (int i = 0, c = TASKS; i < c; i++) {
												addMoreTasks(handler);
											}
										}
									}

								}, 1000, 2000, 2);
							}

						}, delay);
					}

				}, delay);

				try {
					// Sleep for notify
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				delay = handler.getDelayForEverySeconds(1);
				logger.info(">> 1.1. Schedule timer task for notify with delay every: 1 seconds, delay time: " + delay);
				handler.add(new Runnable() {

					@Override
					public void run() {
						logger.info("<< 1.1. Run timer task for notify with delay 1 seconds ok.");
					}

				}, delay);
			}

		});

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
		final long delay = new Random().nextInt(5000);
		handler.add("DELAY-" + delay, new Runnable() {

			private int count = 0;

			@Override
			public void run() {
				long tc = total.incrementAndGet();
				count++;
				try {
					Thread.sleep(new Random().nextInt(1000));
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				if (tc % 100 == 0) {
					handler.outputStatistic();
				}
				if (count % 10 == 0) {
					logger.info("<< 4. Run timer task with delay: " + delay + ", run times: " + count + ", total: " + tc);
				}
				if (count == RUNS) {
					logger.info("<<<<< 4. Run timer task with delay: " + delay + ", run times: " + count + ", FINISHED...................");
				}
				if (tc == TASKS * RUNS) {
					logger.info("<<<<<<<<<<<<<<<<<<<<< ALL TASKS FINISHED...................");

					logger.info(">> 5. Schedule timer task with delay every: 5 seconds, period: 5 seconds, executions: 10...");
					logger.info("FOR SYSTEM TIME CHANGING TEST ...................");

					handler.add(new Runnable() {

						int count = 0;

						@Override
						public void run() {
							count++;
							logger.info("5. Please change the system time ...");
							if (count == 10) {
								logger.info("<< 5. System time changing test finished ...................");
								logger.info("Wait for 3 seconds to destroy...");
								handler.add(new Runnable() {

									@Override
									public void run() {
										handler.destroy();
										logger.info("Wait for 2 seconds to finish...");
										try {
											Thread.sleep(2000);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										finish();
									}

								}, 3000);

							}
						}

					}, handler.getDelayForEverySeconds(5), 5000, 10);

				}
			}

		}, delay, 1000, RUNS);
	}

	private void finish() {
		synchronized (lock) {
			lock.notifyAll();
		}
	}

}
