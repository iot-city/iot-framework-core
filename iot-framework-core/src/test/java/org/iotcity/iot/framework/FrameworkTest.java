package org.iotcity.iot.framework;

import junit.framework.TestCase;

/**
 * @author Ardon
 * @date 2021-04-26
 */
public class FrameworkTest extends TestCase {

	/**
	 * Test application configuration.
	 */
	public void testConfigure() {
		IoTFramework.init();
	}

}
