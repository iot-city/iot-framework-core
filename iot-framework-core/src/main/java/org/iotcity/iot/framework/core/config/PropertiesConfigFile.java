package org.iotcity.iot.framework.core.config;

/**
 * The properties configure file object used to specify the source of the properties file.
 * @author Ardon
 * @date 2021-04-25
 */
public final class PropertiesConfigFile {

	// --------------------------- Public fields ----------------------------

	/**
	 * The configure properties file to load (required, not null or empty).<br/>
	 * (e.g. "org/iotcity/iot/framework/core/i18n/i18n-xxxx-xxxx.properties")
	 */
	public String file;
	/**
	 * Text encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 */
	public String encoding = "UTF-8";
	/**
	 * Whether to load the file from package (false by default).
	 */
	public boolean fromPackage = false;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for the properties configure file object.
	 */
	public PropertiesConfigFile() {
	}

	/**
	 * Constructor for the properties configure file object.
	 * @param file The configure properties file to load (required, not null or empty).<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/i18n/i18n-xxxx-xxxx.properties")
	 * @param encoding Text encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param fromPackage Whether to load the file from package.
	 */
	public PropertiesConfigFile(String file, String encoding, boolean fromPackage) {
		this.file = file;
		this.encoding = encoding;
		this.fromPackage = fromPackage;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{file=\"");
		sb.append(file);
		sb.append("\", encoding=\"");
		sb.append(encoding.toString());
		sb.append("\", fromPackage=");
		sb.append(fromPackage);
		sb.append("}");
		return sb.toString();
	}

}
