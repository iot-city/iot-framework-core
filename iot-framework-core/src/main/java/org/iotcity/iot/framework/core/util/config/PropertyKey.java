package org.iotcity.iot.framework.core.util.config;

/**
 * Property key analyzer.
 * @author Ardon
 * @date 2021-04-23
 */
final class PropertyKey {

	/**
	 * The key for map.
	 */
	final String key;
	/**
	 * The prefix for properties configure.
	 */
	final String prefix;

	/**
	 * Constructor for property key analyzer.
	 * @param key The key of comma (,) separated strings.
	 * @param prefix The prefix string of current configure key.
	 */
	PropertyKey(String key, String prefix) {
		int pos = key.indexOf('.');
		if (pos == -1) {
			this.key = key;
			this.prefix = prefix + "." + key;
		} else {
			this.key = key.substring(pos + 1);
			this.prefix = key;
		}
	}

}
