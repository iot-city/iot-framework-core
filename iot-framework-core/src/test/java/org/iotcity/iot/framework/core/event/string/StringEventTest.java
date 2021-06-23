package org.iotcity.iot.framework.core.event.string;

import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.logging.Logger;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-05-09
 */
public class StringEventTest extends TestCase {

	private final Logger logger = FrameworkCore.getLogger();

	public void testEventPublisher() {

		logger.info("------------------------- STRING EVENT TEST -------------------------");

		StringEventPublisher pub = new StringEventPublisher();
		pub.addListener("event_type_0", new StringEventListener() {

			@Override
			public boolean onEvent(StringEvent event) throws Exception {
				logger.info("priority 0: " + event.getEventType());
				PropertiesConfigFile file = event.getEventData();
				logger.info(file.file);
				return true;
			}

		});
		pub.addListener("event_type_1", new StringEventListener() {

			@Override
			public boolean onEvent(StringEvent event) throws Exception {
				logger.info("priority 1: " + event.getEventType());
				PropertiesConfigFile file = event.getEventData();
				logger.info(file.file);
				return true;
			}

		}, 1);
		pub.addListener("event_type_1", new StringEventListener() {

			@Override
			public boolean onEvent(StringEvent event) throws Exception {
				logger.info("priority 2: " + event.getEventType());
				PropertiesConfigFile file = event.getEventData();
				logger.info(file.file);
				return true;
			}

		}, 2);

		StringEvent event = pub.publish(new StringEvent(this, "event_type_1", new PropertiesConfigFile("abc", null, true), false));
		logger.info("Event execution count: " + event.getExecutionCount());

		logger.info("------------------------- STRING EVENT TEST COMPLETED -------------------------");

		assertTrue(true);

	}

}
