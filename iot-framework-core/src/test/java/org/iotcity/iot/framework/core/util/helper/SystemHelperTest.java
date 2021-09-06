package org.iotcity.iot.framework.core.util.helper;

import junit.framework.TestCase;

/**
 * @author ardon
 * @date 2021-08-12
 */
public class SystemHelperTest extends TestCase {

	public void testLocalAddress() {

		JavaHelper.log("SYSTEM LANG: " + SystemHelper.getSystemLang());
		JavaHelper.log("LOCAL IP: " + SystemHelper.getLocalIP(false));
		JavaHelper.log("LOCAL MAC: " + SystemHelper.getLocalMac(false));

	}

}
