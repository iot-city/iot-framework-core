package org.iotcity.iot.framework.core.bus;

/**
 * Example event data object
 * @author ardon
 * @date 2021-05-10
 */
public class ExampleEventData {

	public String name;
	public String desc;

	/**
	 * Constructor for example event data object
	 */
	public ExampleEventData(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}

}
