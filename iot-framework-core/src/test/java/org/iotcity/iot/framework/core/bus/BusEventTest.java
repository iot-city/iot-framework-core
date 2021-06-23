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

		try {
			// normal event data1
			IoTFramework.getBusEventPublisher().publish(new BusEvent(this, new ExampleEventData("Name 1", "Desc 1"), false));

			// normal event data2
			IoTFramework.getBusEventPublisher().publish(new BusEvent(this, new ExampleEventData2("Name 2", "Desc 2"), false));

			// specified filter event
			IoTFramework.getBusEventPublisher().publish(new ExampleBusEvent(this, new ExampleEventData("Name for filter", "Desc for filter"), "example"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		JavaHelper.log("------------------------- BUS EVENT TEST COMPLETED -------------------------");

	}

}
