package org.iotcity.iot.framework;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.iotcity.iot.framework.core.CoreConfigureManager;
import org.iotcity.iot.framework.core.annotation.AnnotationAnalyzer;
import org.iotcity.iot.framework.core.config.ConfigureManager;
import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.config.PropertiesConfigureManager;
import org.iotcity.iot.framework.core.logging.Logger;
import org.iotcity.iot.framework.core.util.config.PropertiesLoader;
import org.iotcity.iot.framework.core.util.helper.FileHelper;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Configure manager handler of framework.
 * @author Ardon
 * @date 2021-04-26
 */
public final class ConfigureHandler {

	// --------------------------- Private static fields ----------------------------

	/**
	 * The framework configure root file.
	 */
	private static final String CONFIG_FILE = "framework.properties";

	// --------------------------- Private fields ----------------------------

	/**
	 * The configure manager lock.
	 */
	private final Object lock = new Object();
	/**
	 * The configuration properties.
	 */
	private Properties props = null;
	/**
	 * Completed configuration classes.
	 */
	private Set<Class<? extends ConfigureManager>> executed = new HashSet<>();
	/**
	 * Configuration classes waiting for execution.
	 */
	private Set<Class<? extends ConfigureManager>> managers = new HashSet<>();

	// --------------------------- Initialize method ----------------------------

	/**
	 * Initialize configure managers.
	 * @param options Framework startup options data object.
	 */
	void init(FrameworkOptions options) {

		// Framework messages
		String[] messages = new String[] {
			"============================================= LOADING FRAMEWORK RESOURCES =============================================",
			"☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆",
			"============================================ WELCOME TO IOT FRAMEWORK ===========================================",
			"✧",
			"✧ FRAMEWORK GROUPID: " + IoTFramework.NAME + ", VERSION: " + IoTFramework.VERSION + ", WEB SITE: " + IoTFramework.SITE,
			"✧",
			"✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧✧",
			"✧ ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   (*^▽^*)    GOOD DAY, COMMANDER!    (*^▽^*) ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟   ⛟",
			"=================================================================================================================",
		};
		// Output messages
		JavaHelper.log(messages[0]);

		// -------------------- Load configure file --------------------

		// Gets the configure file
		PropertiesConfigFile file;
		if (options == null || options.frameworkFile == null || StringHelper.isEmpty(options.frameworkFile.file)) {
			file = new PropertiesConfigFile(CONFIG_FILE, "UTF-8", false);
		} else {
			file = options.frameworkFile;
		}
		if (file.fromPackage) {
			// Load properties from package
			props = PropertiesLoader.loadProperties(file);
		} else {
			// Ignore configuration files that do not exist
			String filePathName = FileHelper.toLocalDirectory(file.file, false);
			if (FileHelper.exists(filePathName)) {
				// Load properties from directory
				props = PropertiesLoader.loadProperties(file);
			}
		}

		// Initialize core configure manager
		this.perform(CoreConfigureManager.class);

		// -------------------- Search configure manager --------------------

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
		JavaHelper.log(messages[1]);
		// Get a logger
		Logger logger = IoTFramework.getLoggerFactory().getLogger();
		// Output framework messages
		logger.info(messages[2]);
		logger.info(messages[3]);
		logger.info(messages[4]);
		logger.info(messages[5]);
		logger.info(messages[6]);
		logger.info(messages[7]);
		logger.info(messages[8]);

	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Add a configure manager class to handler.
	 * @param clazz Configure manager class.
	 */
	public <T extends ConfigureManager> void addManager(Class<T> clazz) {
		if (clazz == null || executed.contains(clazz) || managers.contains(clazz)) return;
		synchronized (lock) {
			if (executed.contains(clazz) || managers.contains(clazz)) return;
			managers.add(clazz);
		}
	}

	/**
	 * Perform all configuration managers immediately.
	 * @return Whether configurations are successful.
	 */
	public boolean performAll() {
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
	 * @return Whether configuration is successful.
	 */
	public boolean perform(Class<? extends ConfigureManager> managerClass) {
		if (managerClass == null) return false;
		synchronized (lock) {
			if (executed.contains(managerClass)) return true;
			boolean succeed = true;
			try {
				if (PropertiesConfigureManager.class.isAssignableFrom(managerClass)) {
					// Set configure file to configure manager
					PropertiesConfigureManager pm = (PropertiesConfigureManager) managerClass.newInstance();
					if (props != null) {
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
					ConfigureManager cm = managerClass.newInstance();
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
