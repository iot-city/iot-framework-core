package org.iotcity.iot.framework.core.config;

/**
 * Automatic data configuration object.
 * @author Ardon
 */
public interface DataConfigure<T> extends AutoConfigure {

	/**
	 * Execute data configuration for configurable object.
	 * @param configurable Configurable object that need to be configured (required, not null).
	 * @return Whether configuration is successful.
	 */
	boolean config(T configurable);

}
