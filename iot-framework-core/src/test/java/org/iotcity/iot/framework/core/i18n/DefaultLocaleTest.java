package org.iotcity.iot.framework.core.i18n;

import java.util.Date;

import org.iotcity.iot.framework.core.logging.DefaultLoggerFactory;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;

import junit.framework.TestCase;

/**
 * @author Ardon
 */
public class DefaultLocaleTest extends TestCase {

	private final Logger logger = new DefaultLoggerFactory().getLogger();

	public void testLocaleText() {

		logger.info("------------------------- DEFAULT LOCALE TEST -------------------------");

		DefaultLocaleFacotry factory = new DefaultLocaleFacotry();
		factory.load("org/iotcity/iot/framework/core/i18n/iot-i18n-template.properties", true);

		logger.info("Default language: " + factory.getDefaultLang());
		LocaleText locale = factory.getDefaultLocale("CORE");
		logger.info(locale.text("core.test.locale1", "core.test.locale1", ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")));
		logger.info(locale.text("core.test.locale2", "core.test.locale2", ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")));

		logger.info("------------------------- LOCALE TEST en_US -------------------------");

		locale = factory.getLocale("CORE", "en_US");
		logger.info("en_US:");
		logger.info(locale.text("core.test.locale1", "core.test.locale1", ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")));
		logger.info(locale.text("core.test.locale2", "core.test.locale2", ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")));

		logger.info("------------------------- LOCALE TEST zh_CN -------------------------");

		locale = factory.getLocale("CORE", "zh_CN");
		logger.info("zh_CN:");
		logger.info(locale.text("core.test.locale1", "core.test.locale1", ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")));
		logger.info(locale.text("core.test.locale2", "core.test.locale2", ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")));

		assertTrue(true);

	}

}
