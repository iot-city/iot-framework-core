package org.iotcity.iot.framework;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.iotcity.iot.framework.core.CoreConfigureManager;
import org.iotcity.iot.framework.core.annotation.AnnotationAnalyzer;
import org.iotcity.iot.framework.core.bus.BusEvent;
import org.iotcity.iot.framework.core.bus.BusEventPublisher;
import org.iotcity.iot.framework.core.config.ConfigureManager;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.config.PropertiesConfigureManager;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.FileHelper;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;
import org.iotcity.iot.framework.event.FrameworkEventData;
import org.iotcity.iot.framework.event.FrameworkState;

/**
 * Configure manager handler of framework.
 * @author Ardon
 * @date 2021-04-26
 */
public final class FrameworkConfiguration {

	// --------------------------- Static field ----------------------------

	/**
	 * The configuration properties.
	 */
	private static Properties frameworkProps = loadFrameworkConfiguration();

	// --------------------------- Static method ----------------------------

	/**
	 * Load the framework configuration properties (returns not null).
	 */
	private static final Properties loadFrameworkConfiguration() {

		// Get the framework configuration file name.
		String configFile = System.getProperty("framework.file", "framework.properties");
		if (StringHelper.isEmpty(configFile)) configFile = "framework.properties";
		// Get the framework file encoding.
		String encoding = System.getProperty("framework.file.encoding", "UTF-8");
		// Get the framework file package option.
		boolean fromPackage = ConvertHelper.toBoolean(System.getProperty("framework.file.fromPackage"), false);

		// The properties object.
		Properties props;
		// Gets the configure file
		PropertiesConfigFile file = new PropertiesConfigFile(configFile, encoding, fromPackage);
		if (file.fromPackage) {
			// Load properties from package
			props = PropertiesLoader.loadProperties(file);
		} else {
			// Ignore configuration files that do not exist
			String filePathName = FileHelper.toLocalDirectory(file.file, false);
			if (FileHelper.exists(filePathName)) {
				// Load properties from directory
				props = PropertiesLoader.loadProperties(file);
			} else {
				props = null;
			}
		}

		// Return object.
		return props == null ? new Properties() : props;
	}

	/**
	 * Gets the framework configuration properties (returns not null).
	 */
	public static final Properties getFrameworkConfiguration() {
		return frameworkProps;
	}

	/**
	 * Set the framework configuration properties before calling {@link IoTFramework#init()}.<br/>
	 * See: "framework.properties" file to get option fields.
	 * @param props The framework configuration properties (required, can not be null).
	 */
	public static final void setFrameworkConfiguration(Properties props) {
		if (props != null) frameworkProps = props;
	}

	// --------------------------- Private fields ----------------------------

	/**
	 * The configure manager lock.
	 */
	private final Object lock = new Object();
	/**
	 * Completed configuration classes.
	 */
	private final Set<Class<? extends ConfigureManager>> executed = new HashSet<>();
	/**
	 * Configuration classes waiting for execution.
	 */
	private final Set<Class<? extends ConfigureManager>> managers = new HashSet<>();

	// --------------------------- Initialize method ----------------------------

	/**
	 * Initialize configure managers.
	 */
	final void init() {

		// Output messages
		JavaHelper.log("========================================== LOADING FRAMEWORK RESOURCES ==========================================");

		// Initialize core configure manager
		this.perform(CoreConfigureManager.class);

		// Search for configure manager class and events
		AnnotationAnalyzer analyzer = new AnnotationAnalyzer();
		analyzer.addParsePackage(this.getClass().getPackage().getName());
		try {
			analyzer.start();
		} catch (Exception e) {
			JavaHelper.err("Failed to perform resource configuration: " + e.getMessage());
			e.printStackTrace();
		}

		// Output messages
		JavaHelper.log("*****************************************************************************************************************");
		// Get a logger
		Logger logger = IoTFramework.getLoggerFactory().getLogger();
		if (logger.colorful()) {
			// Output framework messages
			logger.log("\033[94;1m============================================ WELCOME TO IOT FRAMEWORK ===========================================\033[0m");
			logger.log("\033[94;1mFRAMEWORK GROUPID: \"" + IoTFramework.NAME + "\", VERSION: \"" + IoTFramework.VERSION + "\", SITE: \"" + IoTFramework.SITE + "\"\033[0m");
			logger.log("\033[94;1m*****************************************************************************************************************\033[0m");
			logger.log("\033[94;1m*****************************\033[95;m  -  (*^-^*)  -  GOOD DAY, COMMANDER!  -  (*^-^*)  -  \033[94;1m******************************\033[0m");
			logger.log("\033[94;1m*****************************************************************************************************************\033[0m");
			logger.log("\033[94;1m=================================================================================================================\033[0m");
		} else {
			// Output framework messages
			logger.log("============================================ WELCOME TO IOT FRAMEWORK ===========================================");
			logger.log("FRAMEWORK GROUPID: \"" + IoTFramework.NAME + "\", VERSION: \"" + IoTFramework.VERSION + "\", SITE: \"" + IoTFramework.SITE + "\"");
			logger.log("*****************************************************************************************************************");
			logger.log("*****************************  -  (*^-^*)  -  GOOD DAY, COMMANDER!  -  (*^-^*)  -  ******************************");
			logger.log("*****************************************************************************************************************");
			logger.log("=================================================================================================================");
		}

		// Clear caches
		PropertiesLoader.clearCaches();

		// Publish an initializing event.
		BusEventPublisher publisher = IoTFramework.getBusEventPublisher();
		publisher.publish(new BusEvent(this, new FrameworkEventData(FrameworkState.INITIALIZED), false));

	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Add a configure manager class to handler.
	 * @param managerClass Configure manager class.
	 */
	public final <T extends ConfigureManager> void addManager(Class<T> managerClass) {
		if (managerClass == null || executed.contains(managerClass) || managers.contains(managerClass)) return;
		synchronized (lock) {
			if (executed.contains(managerClass) || managers.contains(managerClass)) return;
			managers.add(managerClass);
		}
	}

	/**
	 * Perform all configuration managers immediately.
	 * @return Returns true if configurations are successful; otherwise, returns false.
	 */
	public final boolean performAll() {
		boolean succeed = true;
		synchronized (lock) {
			// Check size
			if (managers.size() == 0) return true;
			// Perform all managers
			for (Class<? extends ConfigureManager> managerClass : managers) {
				if (!perform(managerClass) && succeed) succeed = false;
			}
			// Clear waiting for execution
			managers.clear();
		}
		return succeed;
	}

	/**
	 * Perform a manager configuration.
	 * @param managerClass Configure manager class.
	 * @return Returns true if configuration is successful; otherwise, returns false.
	 */
	public final boolean perform(Class<? extends ConfigureManager> managerClass) {
		if (managerClass == null) return false;
		synchronized (lock) {
			if (executed.contains(managerClass)) return true;
			boolean succeed = true;
			try {
				if (PropertiesConfigureManager.class.isAssignableFrom(managerClass)) {
					// Set configure file to configure manager
					PropertiesConfigureManager pm = IoTFramework.getInstance(managerClass);
					// Get the framework files configuration.
					Properties props = getFrameworkConfiguration();
					// Check the size.
					if (props.size() > 0) {
						PropertiesConfigFile file;
						String[] keys = pm.getExternalKeys();
						if (keys != null && keys.length > 0) {
							for (String key : keys) {
								file = PropertiesLoader.getConfigBean(PropertiesConfigFile.class, props, key);
								pm.setExternalFile(key, file);
							}
						}
					}
					succeed = pm.perform();
				} else {
					// Perform configuration
					ConfigureManager cm = IoTFramework.getInstance(managerClass);
					succeed = cm.perform();
				}
			} catch (Exception e) {
				e.printStackTrace();
				succeed = false;
			}
			// Add to executed
			if (succeed) executed.add(managerClass);
			return succeed;
		}
	}

}
