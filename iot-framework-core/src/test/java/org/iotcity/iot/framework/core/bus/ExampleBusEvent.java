package org.iotcity.iot.framework.core.bus;

/**
 * @author ardon
 * @date 2021-06-21
 */
public class ExampleBusEvent extends BusEvent {

	private static final long serialVersionUID = 1L;
	private final String name;

	public ExampleBusEvent(Object source, Object data, String name) throws IllegalArgumentException {
		super(source, data);
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
