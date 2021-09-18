package org.iotcity.iot.framework.core.config;

import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.core.util.helper.FileHelper;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Properties configure manager.
 * @author Ardon
 * @date 2021-04-25
 */
public abstract class PropertiesConfigureManager implements ConfigureManager {

	// --------------------------- Protected fields ----------------------------

	/**
	 * The internal configure prefix and configure data map.
	 */
	protected final Map<String, ConfigureData<?>> internal = new HashMap<>();
	/**
	 * The external configure prefix and configure data map.
	 */
	protected final Map<String, ConfigureData<?>> external = new HashMap<>();

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for properties configure manager.
	 */
	public PropertiesConfigureManager() {
	}

	// --------------------------- Protected method ----------------------------

	/**
	 * Add internal properties configure and configurable information to the manager.
	 * @param <T> The configure data type.
	 * @param configure Automatic properties configuration object.
	 * @param configurable Configurable object for automatic configuration.
	 * @param file The properties configure file object used to specify the source of the properties file.
	 * @param reset Whether reset the data of the current configurable object before configuration.
	 */
	protected final <T> void addInternal(PropertiesConfigure<T> configure, Configurable<T> configurable, PropertiesConfigFile file, boolean reset) {
		String prefix = configure.getPrefixKey();
		if (StringHelper.isEmpty(prefix)) return;
		internal.put(prefix, new ConfigureData<T>(configure, configurable, file, reset));
	}

	/**
	 * Add external properties configure and configurable information to the manager.
	 * @param <T> The configure data type.
	 * @param configure Automatic properties configuration object.
	 * @param configurable Configurable object for automatic configuration.
	 * @param reset Whether reset the data of the current configurable object before configuration.
	 */
	protected final <T> void addExternal(PropertiesConfigure<T> configure, Configurable<T> configurable, boolean reset) {
		String prefix = configure.getPrefixKey();
		if (StringHelper.isEmpty(prefix)) return;
		external.put(prefix, new ConfigureData<T>(configure, configurable, configure.getDefaultExternalFile(), reset));
	}

	// --------------------------- Public method ----------------------------

	/**
	 * Gets the external prefix keys to generate properties configure files information.
	 * @return The file prefix keys.
	 */
	public final String[] getExternalKeys() {
		return external.keySet().toArray(new String[external.size()]);
	}

	/**
	 * Set a external configure file information for a specified prefix key.
	 * @param prefixKey The prefix key of configuration object.
	 * @param file The properties configure file object.
	 */
	public final void setExternalFile(String prefixKey, PropertiesConfigFile file) {
		if (file == null || StringHelper.isEmpty(file.file) || StringHelper.isEmpty(prefixKey)) return;
		ConfigureData<?> data = external.get(prefixKey);
		if (data == null) return;
		data.file = file;
	}

	@Override
	public final boolean perform() {
		boolean succeed = true;
		// Configure all internal configurations first
		for (ConfigureData<?> data : internal.values()) {
			if (!data.config()) {
				if (succeed) succeed = false;
			}
		}
		// Configure all external configurations
		for (ConfigureData<?> data : external.values()) {
			if (!data.config()) {
				if (succeed) succeed = false;
			}
		}
		this.onPerformed();
		return succeed;
	}

	/**
	 * This method is called after execution of configuration.
	 */
	protected abstract void onPerformed();

	// --------------------------- Inner class ----------------------------

	/**
	 * Configure data.
	 * @author Ardon
	 * @date 2021-04-25
	 */
	final class ConfigureData<T> {

		/**
		 * Automatic properties configuration object.
		 */
		private final PropertiesConfigure<T> configure;
		/**
		 * Configurable object for automatic configuration.
		 */
		private final Configurable<T> configurable;
		/**
		 * The configure file.
		 */
		private PropertiesConfigFile file;
		/**
		 * Whether clear data before do configuration.
		 */
		private final boolean reset;

		/**
		 * Constructor for configure data.
		 * @param configure Automatic properties configuration object.
		 * @param configurable Configurable object for automatic configuration.
		 * @param file The configure file.
		 * @param reset Whether clear data before do configuration.
		 */
		ConfigureData(PropertiesConfigure<T> configure, Configurable<T> configurable, PropertiesConfigFile file, boolean reset) {
			this.configure = configure;
			this.configurable = configurable;
			this.file = file;
			this.reset = reset;
		}

		/**
		 * Do configuration.
		 * @return Returns true if configuration is successful; otherwise, returns false.
		 */
		final boolean config() {
			if (file == null) return true;
			if (!file.fromPackage) {
				// Output message
				JavaHelper.log("External file: " + configure.getPrefixKey() + " >> " + file.file);
				// Ignore configuration files that do not exist
				if (StringHelper.isEmpty(file.file)) return true;
				String filePathName = FileHelper.toLocalDirectory(file.file, false);
				if (!FileHelper.exists(filePathName)) return true;
			}
			return configure.config(file, configurable, reset);
		}

	}

}
