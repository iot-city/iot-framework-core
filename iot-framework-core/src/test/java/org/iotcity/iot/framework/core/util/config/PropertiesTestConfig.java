package org.iotcity.iot.framework.core.util.config;

import java.util.Date;
import java.util.List;

/**
 * @author Ardon
 * @date 2021-04-22
 */
public class PropertiesTestConfig {

	// ----------------------- Field configure ------------------------

	/**
	 * Test name.
	 */
	public String name;
	/**
	 * Test time.
	 */
	public Date time;

	// ----------------------- Bean configure ------------------------

	/**
	 * Test bean object.
	 */
	public PropertiesTestConfigSub bean;

	// ----------------------- Array configure ------------------------

	/**
	 * Test primitive array for mode1.
	 */
	public int[] array1;
	/**
	 * Test string array for mode2.
	 */
	public String[] array2;
	/**
	 * Test bean array for mode3.
	 */
	public PropertiesTestConfigArray[] array3;
	/**
	 * Test bean array for mode4.
	 */
	public PropertiesTestConfigArray[] array4;

	// ----------------------- List configure ------------------------

	/**
	 * Test string list for mode1.
	 */
	public List<String> list1;
	/**
	 * Test for object list for mode2.
	 */
	public List<PropertiesTestConfigSub> list2;
	/**
	 * Test for object list for mode3.
	 */
	public List<PropertiesTestConfigSub> list3;

	// ----------------------- Map configure ------------------------

	/**
	 * Test for object map for mode1.
	 */
	public PropertiesMap<String> map1;

	/**
	 * Test for object map for mode2.
	 */
	public PropertiesMap<PropertiesTestConfigSub> map2;

	/**
	 * Test for object map for mode3.
	 */
	public PropertiesMap<PropertiesTestConfigSub> map3;

	// ----------------------- No configure ------------------------

	/**
	 * Test sub object when no configure.
	 */
	public PropertiesTestConfigSub noConfig = new PropertiesTestConfigSub();

}
