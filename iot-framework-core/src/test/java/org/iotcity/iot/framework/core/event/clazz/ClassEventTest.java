package org.iotcity.iot.framework.core.event.clazz;

import org.iotcity.iot.framework.IoTFramework;
import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.logging.Logger;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-05-09
 */
public class ClassEventTest extends TestCase {

	private final Logger logger;

	public ClassEventTest() {
		IoTFramework.init();
		logger = FrameworkCore.getLogger();
	}

	public void testEventPublisher() {

		logger.info("------------------------- CLASS EVENT TEST -------------------------");

		ClassEventPublisher pub = new ClassEventPublisher(true);

		pub.addListener(Object.class, new ClassEventListener() {

			@Override
			public boolean onEvent(ClassEvent event) throws Exception {
				logger.info("Object event 2: " + event.getEventType());
				PropertiesConfigFile file = event.getEventData();
				logger.info(file.file);
				return true;
			}

		}, 2);

		pub.addListener(PropertiesConfigFile.class, new ClassEventListener() {

			@Override
			public boolean onEvent(ClassEvent event) throws Exception {
				logger.info("priority 0: " + event.getEventType());
				PropertiesConfigFile file = event.getEventData();
				logger.info(file.file);
				return true;
			}

		});
		pub.addListener(PropertiesConfigFile.class, new ClassEventListener() {

			@Override
			public boolean onEvent(ClassEvent event) throws Exception {
				logger.info("priority 1: " + event.getEventType());
				PropertiesConfigFile file = event.getEventData();
				logger.info(file.file);
				// event.stopPropagation();
				return true;
			}

		}, 1);
		ClassEventListener[] listeners = pub.getListeners(PropertiesConfigFile.class);
		logger.info("Listeners of PropertiesConfigFile.class: " + listeners.length);

		ClassEvent event = pub.publish(new ClassEvent(this, new PropertiesConfigFile("abc", null, true), false));
		logger.info("Event execution count: " + event.getExecutionCount());

		logger.info("------------------------- CLASS EVENT TEST COMPLETED -------------------------");

		assertTrue(true);

	}

}
