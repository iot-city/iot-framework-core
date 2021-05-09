package org.iotcity.iot.framework.core.event.clazz;

import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.logging.Logger;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-05-09
 */
public class ClassEventTest extends TestCase {

	private final Logger logger = FrameworkCore.getLogger();

	public void testEventPublisher() {

		logger.info("------------------------- CLASS EVENT TEST -------------------------");

		ClassEventPublisher pub = new ClassEventPublisher();
		pub.addListener(PropertiesConfigFile.class, new ClassEventListener() {

			@Override
			public boolean onEvent(ClassEvent event) {
				logger.info("priority 0: " + event.getType());
				PropertiesConfigFile file = event.getData();
				logger.info(file.file);
				return true;
			}

		});
		pub.addListener(PropertiesConfigFile.class, new ClassEventListener() {

			@Override
			public boolean onEvent(ClassEvent event) {
				logger.info("priority 1: " + event.getType());
				PropertiesConfigFile file = event.getData();
				logger.info(file.file);
				// event.stopPropagation();
				return true;
			}

		}, 1);
		int count = pub.publish(new ClassEvent(this, new PropertiesConfigFile("abc", null, true)));
		logger.info("Event execution count: " + count);

		logger.info("------------------------- CLASS EVENT TEST COMPLETED -------------------------");

		assertTrue(true);

	}

}
