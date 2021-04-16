package org.iotcity.iot.framework.core.config;

/**
 * Configurable object for automatic configuration.
 * @param <T> The configure data type.
 * @author Ardon
 */
public interface Configurable<T> {

	/**
	 * Data configuration for configurable object.
	 * @param data Configuration data.
	 * @param reset Whether reset the data of the current configurable object.
	 * @return Whether configuration is successful
	 */
	boolean config(T data, boolean reset);

}
