package org.iotcity.iot.framework.core.config;

/**
 * Automatic properties configuration object.
 * @author Ardon
 */
public interface PropertiesConfigure<T> extends AutoConfigure {

	/**
	 * Load the properties file and configure the configuration data to the configurable object.
	 * @param configurable Configurable object that need to be configured (required, not null).
	 * @param configFile The configure properties file to load (required, not null or empty).
	 * @param fromPackage Whether load the file from package.
	 * @return Whether configuration is successful
	 */
	boolean config(T configurable, String configFile, boolean fromPackage);

}
