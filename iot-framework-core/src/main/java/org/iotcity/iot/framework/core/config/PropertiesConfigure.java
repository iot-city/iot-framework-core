package org.iotcity.iot.framework.core.config;

import java.util.Properties;

import org.iotcity.iot.framework.core.util.config.PropertiesLoader;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Automatic properties configuration object.
 * @param <T> The configure data type.
 * @author Ardon
 */
public abstract class PropertiesConfigure<T> implements AutoConfigure<T> {

	/**
	 * Properties configure data (not null).
	 */
	protected final Properties props;

	/**
	 * Constructor for automatic properties configuration object.
	 * @param configFile The configure properties file to load (required, not null or empty).
	 * @param fromPackage Whether load the file from package.
	 * @throws IllegalArgumentException An error is thrown when the parameter "configFile" is null or empty.
	 */
	public PropertiesConfigure(String configFile, boolean fromPackage) throws IllegalArgumentException {
		// Parameters verification
		if (StringHelper.isEmpty(configFile)) {
			throw new IllegalArgumentException("Parameter configFile can not be null or empty!");
		}
		// Load properties file
		props = PropertiesLoader.loadProperties(configFile, "UTF-8", fromPackage);
	}

	/**
	 * Constructor for automatic properties configuration object.
	 * @param configFile The configure properties file to load (required, not null or empty).
	 * @param encoding Text encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param fromPackage Whether load the file from package.
	 * @throws IllegalArgumentException An error is thrown when the parameter "configFile" is null or empty.
	 */
	public PropertiesConfigure(String configFile, String encoding, boolean fromPackage) throws IllegalArgumentException {
		// Parameters verification
		if (StringHelper.isEmpty(configFile)) {
			throw new IllegalArgumentException("Parameter configFile can not be null or empty!");
		}
		// Load properties file
		props = PropertiesLoader.loadProperties(configFile, encoding, fromPackage);
	}

}
