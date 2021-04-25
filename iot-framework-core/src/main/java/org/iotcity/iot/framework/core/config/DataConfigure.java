package org.iotcity.iot.framework.core.config;

/**
 * Automatic data configuration object.
 * @author Ardon
 */
public abstract class DataConfigure<T> implements AutoConfigure<T> {

	/**
	 * Configuration data (not null).
	 */
	protected final T data;

	/**
	 * Constructor for automatic data configuration object.
	 * @param <T> The configure data type.
	 * @param data Configuration data (required, not null).
	 * @throws IllegalArgumentException An error will be thrown when the parameter "data" is null.
	 */
	public DataConfigure(T data) throws IllegalArgumentException {
		// Parameters verification
		if (data == null) throw new IllegalArgumentException("Parameter data can not be null!");
		this.data = data;
	}

	/**
	 * Gets the configure data (not null).
	 * @return Configuration data.
	 */
	public T getConfigureData() {
		return this.data;
	}

}
