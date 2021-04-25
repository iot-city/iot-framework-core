package org.iotcity.iot.framework.core.config;

/**
 * Automatic data configure object.
 * @param <T> The configure data type.
 * @author Ardon
 */
public interface AutoConfigure<T> {

	/**
	 * Automatically configure data to the configurable object.
	 * @param <T> The configure data type.
	 * @param configurable Configurable object that need to be configured (required, not null).
	 * @param reset Whether reset the data of the current configurable object.
	 * @return Whether configuration is successful.
	 */
	boolean config(Configurable<T> configurable, boolean reset);

}
