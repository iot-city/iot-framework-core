package org.iotcity.iot.framework.core.util.task;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-07-01
 */
public class TaskGroupTest extends TestCase {

	private final Logger logger = FrameworkCore.getLogger();

	public void testTaskGroup() {

		IoTFramework.init();

		System.out.println("-------------------- TEST TASK GROUP WITHOUT TIMEOUT --------------------");

		TaskHandler handler = new TaskHandler("NO-TIMEOUT-TEST", 0, 10, 60, 1000);

		Integer[] array = new Integer[1000];
		for (int i = 0, c = array.length; i < c; i++) {
			array[i] = i + 1;
		}

		TaskGroupDataContext<Integer> context = new TaskGroupDataContext<Integer>(array) {

			@Override
			public int getPriority(int index, Integer data) {
				return 0;
			}

			@Override
			public boolean run(int index, Integer data) {
				if (data > 100) {
					return false;
				} else {
					System.out.println("Running: " + data);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return true;
				}
			}

		};

		TaskGroupExecutor executor = new TaskGroupExecutor(handler, context, 5, true, 0);
		int successes = executor.execute();
		System.out.println("Run tasks complete with success: " + successes);
		System.out.println(StringHelper.format("Submitted: {0}, Executed: {1}, Successes: {2}, Remains: {3}", executor.getSubmitted(), executor.getExecuted(), executor.getSuccesses(), executor.getRemains()));

		System.out.println("-------------------- TEST TASK GROUP WTIH TIMEOUT --------------------");

		context.setNextIndex(0);
		executor = new TaskGroupExecutor(handler, context, 4, true, 3000);
		successes = executor.execute();
		System.out.println("Run tasks complete with success: " + successes);
		System.out.println(StringHelper.format("Submitted: {0}, Executed: {1}, Successes: {2}, Remains: {3}", executor.getSubmitted(), executor.getExecuted(), executor.getSuccesses(), executor.getRemains()));

		logger.info("All tests runs in task handler finished.");
		assertTrue(true);

	}

}
