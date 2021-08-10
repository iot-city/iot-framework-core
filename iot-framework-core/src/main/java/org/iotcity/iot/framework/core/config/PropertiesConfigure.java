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

	// --------------------------- Protected fields ----------------------------

	/**
	 * Properties configure data (null when file is not loaded).
	 */
	protected Properties props;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for automatic properties configuration object.
	 */
	public PropertiesConfigure() {
	}

	/**
	 * Load a properties object from file.
	 * @param configFile The configure properties file information to load (required, not null).
	 * @return Returns true if the configuration file is loaded successfully; otherwise, returns false.
	 */
	public final boolean load(PropertiesConfigFile configFile) throws IllegalArgumentException {
		if (configFile == null || StringHelper.isEmpty(configFile.file)) return false;
		this.props = PropertiesLoader.loadProperties(configFile.file, configFile.encoding, configFile.fromPackage);
		return this.props != null;
	}

	// --------------------------- Public method ----------------------------

	/**
	 * Gets the properties object that has been loaded (null when file is not loaded).
	 * @return The properties object.
	 */
	public final Properties getProperties() {
		return props;
	}

	/**
	 * Automatically configure file data to the configurable object.
	 * @param <T> The configure data type.
	 * @param configFile The configure properties file information to load (required, not null).
	 * @param configurable Configurable object that need to be configured (required, not null).
	 * @param reset Whether reset the data of the current configurable object.
	 * @return Returns true if configuration is successful; otherwise, returns false.
	 */
	public final boolean config(PropertiesConfigFile configFile, Configurable<T> configurable, boolean reset) {
		return this.load(configFile) && this.config(configurable, reset);
	}

	// --------------------------- Abstract method ----------------------------

	/**
	 * Gets the configuration prefix key of the current configuration.
	 * @return The configuration prefix key.
	 */
	public abstract String getPrefixKey();

	/**
	 * Gets the default configuration file information object for external directory.
	 * @return The default configuration file for external directory.
	 */
	public abstract PropertiesConfigFile getDefaultExternalFile();

}
