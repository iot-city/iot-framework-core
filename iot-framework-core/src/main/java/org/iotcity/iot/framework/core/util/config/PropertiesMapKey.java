package org.iotcity.iot.framework.core.util.config;

/**
 * The properties map key information object.
 * @author ardon
 * @date 2021-08-11
 */
class PropertiesMapKey {

	/**
	 * The configuration string for map key.
	 */
	final String key;
	/**
	 * The configuration prefix string of map key.
	 */
	final String prefix;

	/**
	 * Constructor for map key information object.
	 * @param key The configuration string for map key.
	 * @param prefix The configuration prefix string of map key.
	 */
	PropertiesMapKey(String key, String prefix) {
		this.key = key;
		this.prefix = prefix;
	}

}
