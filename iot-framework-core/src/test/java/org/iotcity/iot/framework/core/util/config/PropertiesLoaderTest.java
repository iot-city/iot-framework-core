package org.iotcity.iot.framework.core.util.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * @author Ardon
 * @date 2021-04-22
 */
public final class PropertiesLoaderTest extends TestCase {

	/**
	 * Timer test case
	 */
	public void testTimer() {

		System.out.println("-------------------- TEST PROPERTIES LOADER --------------------");

		String file = "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties";

		Properties props = PropertiesLoader.loadProperties(file, null, true);
		System.out.println("Properties loaded: " + props.getProperty("iot.properties.test.name"));

		PropertiesTestConfig config = PropertiesLoader.loadConfigBean(PropertiesTestConfig.class, file, "UTF-8", true, "iot.properties.test");
		System.out.println("Properties test bean loaded: " + config.name);

		config = PropertiesLoader.getConfigBean(PropertiesTestConfig.class, props, "iot.properties.test");
		System.out.println("Properties test bean geted: " + config.name);

		PropertiesTestConfigArray[] array = PropertiesLoader.getConfigArray(PropertiesTestConfigArray.class, props, "iot.properties.test.array3");
		System.out.println("Properties array3 geted: " + array.length);

		List<PropertiesTestConfigSub> list = PropertiesLoader.getConfigList(PropertiesTestConfigSub.class, props, "iot.properties.test.list2");
		System.out.println("Properties list2 geted: " + list.size());

		list = new ArrayList<PropertiesTestConfigSub>();
		PropertiesLoader.getConfigList(list, PropertiesTestConfigSub.class, props, "iot.properties.test.list2");
		System.out.println("Properties list2 geted: " + list.size());

		PropertiesMap<PropertiesTestConfigSub> map2 = PropertiesLoader.getConfigMap(PropertiesTestConfigSub.class, props, "iot.properties.test.map2");
		System.out.println("Properties map2 geted: " + map2.size());

		PropertiesMap<Object> map4 = PropertiesLoader.getConfigMap(Object.class, props, "iot.properties.test.map4");
		System.out.println("Properties map4 geted: " + map4.size());

		System.out.println("-------------------- TEST PROPERTIES LOADER COMPLETE --------------------");

		assertTrue(true);

	}

}
