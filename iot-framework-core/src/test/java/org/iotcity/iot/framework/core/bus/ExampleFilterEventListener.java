package org.iotcity.iot.framework.core.bus;

import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.logging.Logger;

/**
 * @author ardon
 * @date 2021-06-21
 */
@BusDataListener(value = ExampleEventData.class, priority = 1, filterEvent = ExampleBusEvent.class)
public class ExampleFilterEventListener implements BusEventListener {

	private final Logger logger = FrameworkCore.getLogger();

	@Override
	public boolean onEvent(BusEvent event) {
		ExampleBusEvent exampleEvent = (ExampleBusEvent) event;
		logger.info("On filter event name: " + exampleEvent.getName());
		ExampleEventData data = exampleEvent.getData();
		logger.info("On filter event data: " + data.name + "; desc: " + data.desc);
		return true;
	}

}
