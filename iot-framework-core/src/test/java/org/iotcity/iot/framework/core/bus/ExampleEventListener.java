package org.iotcity.iot.framework.core.bus;

import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.logging.Logger;

/**
 * @author ardon
 * @date 2021-05-10
 */
@BusDataListener(ExampleEventData.class)
public class ExampleEventListener implements BusEventListener {

	private final Logger logger = FrameworkCore.getLogger();

	@Override
	public boolean onEvent(BusEvent event) throws Exception {
		ExampleEventData data = event.getData();
		logger.info("On bus event data: " + data.name + "; desc: " + data.desc);
		return true;
	}

}
