package org.iotcity.iot.framework.core.bus;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-05-10
 */
public class BusEventTest extends TestCase {

	public void testBusEvent() {

		JavaHelper.log("------------------------- BUS EVENT TEST -------------------------");

		IoTFramework.init();

		IoTFramework.getBusEventPublisher().publish(new BusEvent(this, new ExampleEventData("test name", "test desc")));

		JavaHelper.log("------------------------- BUS EVENT TEST COMPLETED -------------------------");

	}

}
