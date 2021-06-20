package org.iotcity.iot.framework.core.bus;

import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.logging.Logger;

/**
 * @author ardon
 * @date 2021-06-21
 */
@BusDataListener(ExampleEventData2.class)
public class ExampleEventListener2 implements BusEventListener {

	private final Logger logger = FrameworkCore.getLogger();

	@Override
	public boolean onEvent(BusEvent event) {
		ExampleEventData2 data = event.getData();
		logger.info("On bus event data2: " + data.name + "; desc: " + data.desc);
		return true;
	}

}
