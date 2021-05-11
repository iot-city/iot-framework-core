package org.iotcity.iot.framework.core.i18n;

import java.util.Date;

import org.iotcity.iot.framework.core.FrameworkCore;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;

import junit.framework.TestCase;

/**
 * @author Ardon
 */
public class DefaultLocaleTest extends TestCase {

	private final Logger logger = FrameworkCore.getLogger();

	public void testLocaleText() {

		logger.info("------------------------- DEFAULT LOCALE TEST -------------------------");

		DefaultLocaleFacotry factory = new DefaultLocaleFacotry();
		logger.info("Global language: " + factory.getGlobalLangKey());

		logger.info("------------------------- TEST TEMPLATE CONFIGURE -------------------------");

		LocaleConfigure configure = new LocaleConfigure();
		PropertiesConfigFile file = new PropertiesConfigFile();
		file.file = "org/iotcity/iot/framework/core/i18n/iot-i18n-template.properties";
		file.fromPackage = true;
		configure.config(file, factory, true);

		logger.info("------------------------- LOCALE TEST: CORE -------------------------");

		LocaleText locale = factory.getLocale("CORE");
		logger.info("Default language: " + factory.getDefaultLangKey("CORE"));
		logger.info(locale.text("core.test.locale1", "core.test.locale1", ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")));
		logger.info(locale.text("core.test.locale2", "core.test.locale2", ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")));

		logger.info("------------------------- LOCALE TEST: en_US -------------------------");

		locale = factory.getLocale("CORE", "en_US");
		logger.info("en_US:");
		logger.info(locale.text("core.test.locale1", "core.test.locale1", ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")));
		logger.info(locale.text("core.test.locale2", "core.test.locale2", ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")));

		logger.info("------------------------- LOCALE TEST: zh_CN -------------------------");

		locale = factory.getLocale("CORE", "zh_CN");
		logger.info("zh_CN:");
		logger.info(locale.text("core.test.locale1", "core.test.locale1", ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")));
		logger.info(locale.text("core.test.locale2", "core.test.locale2", ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")));

		logger.info("------------------------- LOCALE TEST: ACTORS -------------------------");

		locale = factory.getLocale("ACTORS");
		logger.info("Default language: " + factory.getDefaultLangKey("ACTORS"));
		logger.info(locale.text("core.test.locale1", "core.test.locale1", ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")));
		logger.info(locale.text("core.test.locale2", "core.test.locale2", ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")));

		assertTrue(true);

	}

}
