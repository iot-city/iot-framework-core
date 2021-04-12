package org.iotcity.iot.framework.core.logging;

import junit.framework.TestCase;

/**
 * @author Ardon
 */
public class DefaultLoggerTest extends TestCase {

	/**
	 * Test color effect
	 */
	public void testColor() {
		//
		// System.out.println("CONSOLE COLORS -----------------------------------------");
		// System.out.println("\033[30;4m" + "It's my color with number: 30" + "\033[0m");
		// System.out.println("\033[31;4m" + "It's my color with number: 31" + "\033[0m");
		// System.out.println("\033[32;4m" + "It's my color with number: 32" + "\033[0m");
		// System.out.println("\033[33;4m" + "It's my color with number: 33" + "\033[0m");
		// System.out.println("\033[34;4m" + "It's my color with number: 34" + "\033[0m");
		// System.out.println("\033[35;4m" + "It's my color with number: 35" + "\033[0m");
		// System.out.println("\033[36;4m" + "It's my color with number: 36" + "\033[0m");
		// System.out.println("\033[37;4m" + "It's my color with number: 37" + "\033[0m");
		//
		// System.out.println("\033[40;4m" + "It's my color with number: 40" + "\033[0m");
		// System.out.println("\033[41;4m" + "It's my color with number: 41" + "\033[0m");
		// System.out.println("\033[42;4m" + "It's my color with number: 41" + "\033[0m");
		// System.out.println("\033[43;4m" + "It's my color with number: 43" + "\033[0m");
		// System.out.println("\033[44;4m" + "It's my color with number: 44" + "\033[0m");
		// System.out.println("\033[45;4m" + "It's my color with number: 45" + "\033[0m");
		// System.out.println("\033[46;4m" + "It's my color with number: 46" + "\033[0m");
		// System.out.println("\033[47;4m" + "It's my color with number: 47" + "\033[0m");
		//
		// System.out.println("---------------------------------------------------------");
		//
		// System.out.println("\033[90;4m" + "It's my color with number: 90" + "\033[0m");
		// System.out.println("\033[91;4m" + "It's my color with number: 91" + "\033[0m");
		// System.out.println("\033[92;4m" + "It's my color with number: 92" + "\033[0m");
		// System.out.println("\033[93;4m" + "It's my color with number: 93" + "\033[0m");
		// System.out.println("\033[94;4m" + "It's my color with number: 94" + "\033[0m");
		// System.out.println("\033[95;4m" + "It's my color with number: 95" + "\033[0m");
		// System.out.println("\033[96;4m" + "It's my color with number: 96" + "\033[0m");
		// System.out.println("\033[97;4m" + "It's my color with number: 97" + "\033[0m");
		//
		// System.out.println("\033[100;4m" + "It's my color with number: 100" + "\033[0m");
		// System.out.println("\033[101;4m" + "It's my color with number: 101" + "\033[0m");
		// System.out.println("\033[102;4m" + "It's my color with number: 101" + "\033[0m");
		// System.out.println("\033[103;4m" + "It's my color with number: 103" + "\033[0m");
		// System.out.println("\033[104;4m" + "It's my color with number: 104" + "\033[0m");
		// System.out.println("\033[105;4m" + "It's my color with number: 105" + "\033[0m");
		// System.out.println("\033[106;4m" + "It's my color with number: 106" + "\033[0m");
		// System.out.println("\033[107;4m" + "It's my color with number: 107" + "\033[0m");

		assertTrue(true);

	}

	/**
	 * Run logger test
	 */
	public void testLogger() {

		System.out.println("-------------------- TEST GLOBAL CONFIG ------------------");

		System.out.println("-------------------------- (DEFAULT) ------------------------");

		DefaultLoggerFactory factor = new DefaultLoggerFactory();
		Logger logger = factor.getLogger();
		logger.trace("Test debugger for default constructor");
		logger.debug("Test debugger for default constructor");
		logger.info("Test debugger for default constructor");
		logger.warn("Test debugger for default constructor");
		logger.error("Test debugger for default constructor");
		logger.fatal("Test debugger for default constructor");

		System.out.println("-------------------- TEST TEMPLATE CONFIG ------------------");

		System.out.println("-------------------------- (GLOBAL) ------------------------");

		factor = new DefaultLoggerFactory("org/iotcity/iot/framework/core/logging/iot-logger-template.properties", true);
		logger = factor.getLogger("GLOBAL");
		logger.trace("Test debugger for template constructor");
		logger.debug("Test debugger for template constructor");
		logger.info("Test debugger for template constructor");
		logger.warn("Test debugger for template constructor");
		logger.error("Test debugger for template constructor");
		logger.fatal("Test debugger for template constructor");

		System.out.println("-------------------------- (CORE) ------------------------");

		logger = factor.getLogger("CORE");
		logger.trace("Test debugger for template constructor");
		logger.debug("Test debugger for template constructor");
		logger.info("Test debugger for template constructor");
		logger.warn("Test debugger for template constructor");
		logger.error("Test debugger for template constructor");
		logger.fatal("Test debugger for template constructor");

		System.out.println("-------------------------- (ACTOR) ------------------------");

		logger = factor.getLogger("ACTOR");
		logger.trace("Test debugger for template constructor");
		logger.debug("Test debugger for template constructor");
		logger.info("Test debugger for template constructor");
		logger.warn("Test debugger for template constructor");
		logger.error("Test debugger for template constructor");
		logger.fatal("Test debugger for template constructor", new Throwable("FATAL TEST"));

		System.out.println("-------------------------- (CLASS) ------------------------");

		logger = factor.getLogger("CORE", this.getClass());
		logger.trace("Test debugger for class");
		logger.debug("Test debugger for class");
		logger.info("Test debugger for class");
		logger = factor.getLogger("CORE", this.getClass(), 1);
		logger.warn("Test debugger for depth");
		logger.error("Test debugger for depth");
		logger.fatal("Test debugger for depth");

		assertTrue(true);
	}

}
