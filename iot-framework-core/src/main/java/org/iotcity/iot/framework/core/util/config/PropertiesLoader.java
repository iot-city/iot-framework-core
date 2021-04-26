package org.iotcity.iot.framework.core.util.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.iotcity.iot.framework.core.config.PropertiesConfigFile;
import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.FileHelper;
import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Use configure loader to load properties file.
 * @author Ardon
 */
public final class PropertiesLoader {

	/**
	 * Properties files map (this key is file name, the value is properties object)
	 */
	private static final Map<String, Properties> props = new HashMap<>();

	// --------------------------------- Load configure file method ---------------------------------

	/**
	 * Gets a properties object from file (returns null on load failure).
	 * @param file The properties configure file information object.
	 * @return Properties object result.
	 */
	public static final Properties loadProperties(PropertiesConfigFile file) {
		if (file == null) return null;
		return loadProperties(file.file, file.encoding, file.fromPackage);
	}

	/**
	 * Gets a properties object from file (returns null on load failure).
	 * @param filePathName Properties file directory and the file name.<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties")
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param fromPackage Whether load file from package.
	 * @return Properties object result.
	 */
	public static final Properties loadProperties(String filePathName, String encoding, boolean fromPackage) {
		if (filePathName == null || filePathName.length() == 0) return null;
		String key = fromPackage + "|" + filePathName;
		boolean succeed = true;
		Properties prop = props.get(key);
		if (prop == null) {
			synchronized (props) {
				if (props.containsKey(key)) return props.get(key);
				prop = new Properties();
				succeed = FileHelper.loadProperties(prop, filePathName, encoding, fromPackage);
				props.put(key, succeed ? prop : null);
			}
		}
		return succeed ? prop : null;
	}

	/**
	 * Clear all properties file caches.
	 */
	public static final void clearCaches() {
		synchronized (props) {
			props.clear();
		}
	}

	// --------------------------------- Load configure bean methods ---------------------------------

	/**
	 * Load a configure file and set the properties to the bean (returns null when failed).
	 * @param <T> The bean type.
	 * @param beanClass The configure bean type.
	 * @param file The properties configure file information object.
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test").
	 * @return The configure bean.
	 */
	public static final <T> T loadConfigBean(Class<T> beanClass, PropertiesConfigFile file, String prefix) {
		if (file == null) return null;
		return loadConfigBean(beanClass, file.file, file.encoding, file.fromPackage, prefix);
	}

	/**
	 * Load a configure file and set the properties to the bean (returns null when failed).
	 * @param <T> The bean type.
	 * @param beanClass The configure bean type.
	 * @param filePathName Configure file directory and the file name.<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties")
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param fromPackage Whether load the file from package.
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test").
	 * @return The configure bean.
	 */
	public static final <T> T loadConfigBean(Class<T> beanClass, String filePathName, String encoding, boolean fromPackage, String prefix) {
		try {
			T bean = beanClass.newInstance();
			return loadConfigBean(bean, filePathName, encoding, fromPackage, prefix) ? bean : null;
		} catch (Exception e) {
			JavaHelper.err("Load configure to a bean error: " + filePathName);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Load a configure file and set the properties to the bean (returns false when failed).
	 * @param <T> The bean type.
	 * @param bean The configure bean.
	 * @param file The properties configure file information object.
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test").
	 * @return Whether load configure successful.
	 */
	public static final <T> boolean loadConfigBean(T bean, PropertiesConfigFile file, String prefix) {
		if (file == null) return false;
		return loadConfigBean(bean, file.file, file.encoding, file.fromPackage, prefix);
	}

	/**
	 * Load a configure file and set the properties to the bean (returns false when failed).
	 * @param <T> The bean type.
	 * @param bean The configure bean.
	 * @param filePathName Configure file directory and the file name.<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties")
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param fromPackage Whether load the file from package.
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test").
	 * @return Whether load configure successful.
	 */
	public static final <T> boolean loadConfigBean(T bean, String filePathName, String encoding, boolean fromPackage, String prefix) {
		if (bean == null) return false;
		if (prefix == null) prefix = "";
		Properties props = loadProperties(filePathName, encoding, fromPackage);
		if (props == null) return false;
		String enabled = props.getProperty(prefix);
		if (!StringHelper.isEmpty(enabled) && !ConvertHelper.toBoolean(enabled, false)) return false;
		if (prefix.length() > 0 && !prefix.endsWith(".")) prefix += ".";
		try {
			fillConfigBean(bean.getClass(), bean, props, prefix);
		} catch (Exception e) {
			JavaHelper.err("Load configure to a bean error: " + filePathName);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// --------------------------------- Load configure array methods ---------------------------------

	/**
	 * Load a configure file and set the properties to an array (returns null when failed).
	 * @param <T> The array type.
	 * @param beanClass The array component type.
	 * @param file The properties configure file information object.
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.values").
	 * @return The configure array data.
	 */
	public static final <T> T loadConfigArray(Class<?> beanClass, PropertiesConfigFile file, String prefix) {
		if (file == null) return null;
		return loadConfigArray(beanClass, file.file, file.encoding, file.fromPackage, prefix);
	}

	/**
	 * Load a configure file and set the properties to an array (returns null when failed).
	 * @param <T> The array type.
	 * @param beanClass The array component type.
	 * @param filePathName Configure file directory and the file name.<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties")
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param fromPackage Whether load the file from package.
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.values").
	 * @return The configure array data.
	 */
	public static final <T> T loadConfigArray(Class<?> beanClass, String filePathName, String encoding, boolean fromPackage, String prefix) {
		if (beanClass == null || filePathName == null) return null;
		if (prefix == null) prefix = "";
		Properties props = loadProperties(filePathName, encoding, fromPackage);
		if (props == null) return null;
		return getConfigArray(beanClass, props, prefix);
	}

	// --------------------------------- Load configure list methods ---------------------------------

	/**
	 * Load a configure file and set the properties to a list (returns null when failed).
	 * @param <T> The parameterized type of list.
	 * @param beanClass The parameterized type class of list.
	 * @param file The properties configure file information object.
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.list").
	 * @return The configure list data.
	 */
	public static final <T> List<T> loadConfigList(Class<T> beanClass, PropertiesConfigFile file, String prefix) {
		if (file == null) return null;
		List<T> list = new ArrayList<T>();
		return loadConfigList(list, beanClass, file.file, file.encoding, file.fromPackage, prefix) ? list : null;
	}

	/**
	 * Load a configure file and set the properties to a list (returns null when failed).
	 * @param <T> The parameterized type of list.
	 * @param beanClass The parameterized type class of list.
	 * @param filePathName Configure file directory and the file name.<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties")
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param fromPackage Whether load the file from package.
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.list").
	 * @return The configure list data.
	 */
	public static final <T> List<T> loadConfigList(Class<T> beanClass, String filePathName, String encoding, boolean fromPackage, String prefix) {
		List<T> list = new ArrayList<T>();
		return loadConfigList(list, beanClass, filePathName, encoding, fromPackage, prefix) ? list : null;
	}

	/**
	 * Load a configure file and set the properties to a list (returns false when failed).
	 * @param <T> The parameterized type of list.
	 * @param list The configure list to be setup.
	 * @param beanClass The parameterized type class of list.
	 * @param file The properties configure file information object.
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.list").
	 * @return Whether load configure successful.
	 */
	public static final <T> boolean loadConfigList(List<T> list, Class<T> beanClass, PropertiesConfigFile file, String prefix) {
		if (file == null) return false;
		return loadConfigList(list, beanClass, file.file, file.encoding, file.fromPackage, prefix);
	}

	/**
	 * Load a configure file and set the properties to a list (returns false when failed).
	 * @param <T> The parameterized type of list.
	 * @param list The configure list to be setup.
	 * @param beanClass The parameterized type class of list.
	 * @param filePathName Configure file directory and the file name.<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties")
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param fromPackage Whether load the file from package.
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.list").
	 * @return Whether load configure successful.
	 */
	public static final <T> boolean loadConfigList(List<T> list, Class<T> beanClass, String filePathName, String encoding, boolean fromPackage, String prefix) {
		if (list == null || beanClass == null || filePathName == null) return false;
		if (prefix == null) prefix = "";
		Properties props = loadProperties(filePathName, encoding, fromPackage);
		if (props == null) return false;
		return getConfigList(list, beanClass, props, prefix);
	}

	// --------------------------------- Load configure map methods ---------------------------------

	/**
	 * Load a configure file and set the properties to a map (returns null when failed).
	 * @param <T> The parameterized type of map.
	 * @param beanClass The parameterized type class of map.
	 * @param file The properties configure file information object.
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.map").
	 * @return The configure map data.
	 */
	public static final <T> PropertiesMap<T> loadConfigMap(Class<T> beanClass, PropertiesConfigFile file, String prefix) {
		if (file == null) return null;
		PropertiesMap<T> map = new PropertiesMap<T>();
		return loadConfigMap(map, beanClass, file.file, file.encoding, file.fromPackage, prefix) ? map : null;
	}

	/**
	 * Load a configure file and set the properties to a map (returns null when failed).
	 * @param <T> The parameterized type of map.
	 * @param beanClass The parameterized type class of map.
	 * @param filePathName Configure file directory and the file name.<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties")
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param fromPackage Whether load the file from package.
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.map").
	 * @return The configure map data.
	 */
	public static final <T> PropertiesMap<T> loadConfigMap(Class<T> beanClass, String filePathName, String encoding, boolean fromPackage, String prefix) {
		PropertiesMap<T> map = new PropertiesMap<T>();
		return loadConfigMap(map, beanClass, filePathName, encoding, fromPackage, prefix) ? map : null;
	}

	/**
	 * Load a configure file and set the properties to a map (returns false when failed).
	 * @param <T> The parameterized type of map.
	 * @param map The configure map to be setup.
	 * @param beanClass The parameterized type class of map.
	 * @param file The properties configure file information object.
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.map").
	 * @return Whether load configure successful.
	 */
	public static final <T> boolean loadConfigMap(PropertiesMap<T> map, Class<T> beanClass, PropertiesConfigFile file, String prefix) {
		if (file == null) return false;
		return loadConfigMap(map, beanClass, file.file, file.encoding, file.fromPackage, prefix);
	}

	/**
	 * Load a configure file and set the properties to a map (returns false when failed).
	 * @param <T> The parameterized type of map.
	 * @param map The configure map to be setup.
	 * @param beanClass The parameterized type class of map.
	 * @param filePathName Configure file directory and the file name.<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties")
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param fromPackage Whether load the file from package.
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.map").
	 * @return Whether load configure successful.
	 */
	public static final <T> boolean loadConfigMap(PropertiesMap<T> map, Class<T> beanClass, String filePathName, String encoding, boolean fromPackage, String prefix) {
		if (map == null || beanClass == null || filePathName == null) return false;
		if (prefix == null) prefix = "";
		Properties props = loadProperties(filePathName, encoding, fromPackage);
		if (props == null) return false;
		return getConfigMap(map, beanClass, props, prefix);
	}

	// --------------------------------- Get configure data methods ---------------------------------

	/**
	 * Gets a bean configured from the properties configure (returns null when failed).
	 * @param <T> The bean type.
	 * @param beanClass The configure bean type.
	 * @param props Properties object has loaded (not null).
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test").
	 * @return The configure bean.
	 */
	public static final <T> T getConfigBean(Class<T> beanClass, Properties props, String prefix) {
		try {
			T bean = beanClass.newInstance();
			return getConfigBean(bean, props, prefix);
		} catch (Exception e) {
			JavaHelper.err("Gets a bean configured from properties error: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets a bean configured from the properties configure (returns null when failed).
	 * @param <T> The bean type.
	 * @param bean The configure bean to be setup.
	 * @param props Properties object has loaded (not null).
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test").
	 * @return The configure bean.
	 */
	public static final <T> T getConfigBean(T bean, Properties props, String prefix) {
		if (bean == null || props == null) return null;
		if (prefix == null) prefix = "";
		String enabled = props.getProperty(prefix);
		if (!StringHelper.isEmpty(enabled) && !ConvertHelper.toBoolean(enabled, false)) return null;
		if (prefix.length() > 0 && !prefix.endsWith(".")) prefix += ".";
		try {
			fillConfigBean(bean.getClass(), bean, props, prefix);
		} catch (Exception e) {
			JavaHelper.err("Gets a bean configured from properties error: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		return bean;
	}

	/**
	 * Gets an array configured from the properties configure (returns null when failed).
	 * @param <T> The array component type.
	 * @param beanClass The array component type.
	 * @param props Properties object has loaded (not null).
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.values").
	 * @return The configure bean array.
	 */
	public static final <T> T getConfigArray(Class<?> beanClass, Properties props, String prefix) {
		if (beanClass == null || props == null || StringHelper.isEmpty(prefix)) return null;
		String value = props.getProperty(prefix);
		if (StringHelper.isEmptyWithTrim(value)) return null;
		String[] values = value.trim().split("[,;]");
		if (values == null || values.length == 0) return null;
		Object array = Array.newInstance(beanClass, values.length);
		if (ConvertHelper.isConvertible(beanClass)) {
			// Convert convertible data to array
			// e.g. iot.framework.actor.apps.app1.packages=actors.app1.async, actors.app1.sync
			Object defaultValue = JavaHelper.getTypeDefaultValue(beanClass);
			for (int i = 0, c = values.length; i < c; i++) {
				String v = values[i].trim();
				if (beanClass == String.class) {
					Array.set(array, i, v);
				} else {
					Array.set(array, i, ConvertHelper.convertTo(beanClass, v, defaultValue));
				}
			}
		} else {
			// Convert to a custom data object by section defined
			// e.g. iot.framework.actor.apps=app1, app2
			for (int i = 0, c = values.length; i < c; i++) {
				String k = values[i].trim();
				if (StringHelper.isEmpty(k)) continue;
				PropertyKey pkey = new PropertyKey(k, prefix);
				String enabled = props.getProperty(pkey.prefix);
				if (!StringHelper.isEmpty(enabled) && !ConvertHelper.toBoolean(enabled, false)) continue;
				try {
					Object v = beanClass.newInstance();
					Array.set(array, i, v);
					fillConfigBean(beanClass, v, props, pkey.prefix + ".");
				} catch (Exception e) {
					JavaHelper.err("Gets an array configured from properties error: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		@SuppressWarnings("unchecked")
		T ret = (T) array;
		return ret;
	}

	/**
	 * Gets a list configured from the properties configure (returns null when failed).
	 * @param <T> The parameterized type of list.
	 * @param beanClass The parameterized type class of list.
	 * @param props Properties object has loaded (not null).
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.values").
	 * @return The configure bean array.
	 */
	public static final <T> List<T> getConfigList(Class<T> beanClass, Properties props, String prefix) {
		List<T> list = new ArrayList<T>();
		return getConfigList(list, beanClass, props, prefix) ? list : null;
	}

	/**
	 * Gets a list configured from the properties configure (returns false when failed).
	 * @param <T> The parameterized type of list.
	 * @param list The configure list to be setup.
	 * @param beanClass The parameterized type class of list.
	 * @param props Properties object has loaded (not null).
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.values").
	 * @return Whether load configure successful.
	 */
	public static final <T> boolean getConfigList(List<T> list, Class<T> beanClass, Properties props, String prefix) {
		if (list == null || beanClass == null || props == null || StringHelper.isEmpty(prefix)) return false;
		String value = props.getProperty(prefix);
		if (StringHelper.isEmptyWithTrim(value)) return false;
		String[] values = value.trim().split("[,;]");
		if (values == null || values.length == 0) return false;
		if (ConvertHelper.isConvertible(beanClass)) {
			// Convert convertible data to array
			// e.g. iot.framework.actor.apps.app1.packages=actors.app1.async, actors.app1.sync
			Object defaultValue = JavaHelper.getTypeDefaultValue(beanClass);
			for (int i = 0, c = values.length; i < c; i++) {
				String v = values[i].trim();
				if (StringHelper.isEmpty(v)) continue;
				@SuppressWarnings("unchecked")
				T tv = (T) ConvertHelper.convertTo(beanClass, v, defaultValue);
				list.add(tv);
			}
		} else {
			// Convert to a custom data object by section defined
			// e.g. iot.framework.actor.apps=app1, app2
			for (int i = 0, c = values.length; i < c; i++) {
				String k = values[i].trim();
				if (StringHelper.isEmpty(k)) continue;
				PropertyKey pkey = new PropertyKey(k, prefix);
				String enabled = props.getProperty(pkey.prefix);
				if (!StringHelper.isEmpty(enabled) && !ConvertHelper.toBoolean(enabled, false)) continue;
				try {
					T v = beanClass.newInstance();
					list.add(v);
					fillConfigBean(beanClass, v, props, pkey.prefix + ".");
				} catch (Exception e) {
					JavaHelper.err("Gets a list configured from properties error: " + e.getMessage());
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Gets a properties map configured from the properties configure (returns null when failed).
	 * @param <T> The parameterized type of map.
	 * @param beanClass The parameterized type class of map.
	 * @param props Properties object has loaded (not null).
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.map").
	 * @return The configure bean map.
	 */
	public static final <T> PropertiesMap<T> getConfigMap(Class<T> beanClass, Properties props, String prefix) {
		PropertiesMap<T> map = new PropertiesMap<>();
		return getConfigMap(map, beanClass, props, prefix) ? map : null;
	}

	/**
	 * Gets a properties map configured from the properties configure (returns false when failed).
	 * @param <T> The parameterized type of map.
	 * @param map The configure map to be setup.
	 * @param beanClass The parameterized type class of map.
	 * @param props Properties object has loaded (not null).
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.map").
	 * @return Whether load configure successful.
	 */
	public static final <T> boolean getConfigMap(PropertiesMap<T> map, Class<T> beanClass, Properties props, String prefix) {
		if (map == null || beanClass == null || props == null || StringHelper.isEmpty(prefix)) return false;
		String value = props.getProperty(prefix);
		if (StringHelper.isEmptyWithTrim(value)) return false;
		String[] values = value.trim().split("[,;]");
		if (values == null || values.length == 0) return false;
		if (ConvertHelper.isConvertible(beanClass)) {
			// Convert convertible data to map
			// e.g. iot.framework.actor.apps.map=sub1, sub2
			Object defaultValue = JavaHelper.getTypeDefaultValue(beanClass);
			for (int i = 0, c = values.length; i < c; i++) {
				String k = values[i].trim();
				if (StringHelper.isEmpty(k)) continue;
				PropertyKey pkey = new PropertyKey(k, prefix);
				@SuppressWarnings("unchecked")
				T v = (T) ConvertHelper.convertTo(beanClass, props.get(pkey.prefix), defaultValue);
				map.put(pkey.key, v);
			}
		} else {
			// Convert to a custom data object by section defined
			// e.g. iot.framework.actor.apps=app1, app2
			for (int i = 0, c = values.length; i < c; i++) {
				String k = values[i].trim();
				if (StringHelper.isEmpty(k)) continue;
				PropertyKey pkey = new PropertyKey(k, prefix);
				String enabled = props.getProperty(pkey.prefix);
				if (!StringHelper.isEmpty(enabled) && !ConvertHelper.toBoolean(enabled, false)) continue;
				try {
					T v = beanClass.newInstance();
					map.put(pkey.key, v);
					fillConfigBean(beanClass, v, props, pkey.prefix + ".");
				} catch (Exception e) {
					JavaHelper.err("Gets a properties map configured from properties error: " + e.getMessage());
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	// --------------------------------- Fill configure bean methods ---------------------------------

	/**
	 * Set values to property fields of the bean.
	 */
	private static final void fillConfigBean(Class<?> type, Object bean, Properties props, String prefix) throws Exception {
		Field[] fields = type.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (Modifier.isStatic(field.getModifiers())) continue;
				fillConfigField(field, field.getType(), bean, props, prefix + field.getName());
			}
		}
	}

	/**
	 * Get a data value from props and set it to the field.
	 */
	private static final void fillConfigField(Field field, Class<?> type, Object bean, Properties props, String key) throws Exception {
		if (!props.containsKey(key)) return;
		field.setAccessible(true);
		if (ConvertHelper.isConvertible(type)) {
			// Get a convertible data
			String data = props.getProperty(key);
			// Skip no configure
			if (StringHelper.isEmpty(data)) return;
			data = data.trim();
			if (data.length() == 0) return;
			if (type == String.class) {
				field.set(bean, data);
			} else {
				field.set(bean, ConvertHelper.convertTo(type, data, field.get(bean)));
			}
		} else if (type.isArray()) {
			// Get an array data
			Class<?> subType = type.getComponentType();
			Object array = getConfigArray(subType, props, key);
			if (array != null) field.set(bean, array);
		} else if (type == PropertiesMap.class) {
			ParameterizedType ptype = (ParameterizedType) field.getGenericType();
			if (ptype == null) return;
			Type[] types = ptype.getActualTypeArguments();
			if (types == null || types.length == 0) return;
			Class<?> valueType = (Class<?>) types[0];
			PropertiesMap<?> map = getConfigMap(valueType, props, key);
			if (map != null) field.set(bean, map);
		} else if (type == List.class) {
			ParameterizedType ptype = (ParameterizedType) field.getGenericType();
			if (ptype == null) return;
			Type[] types = ptype.getActualTypeArguments();
			if (types == null || types.length == 0) return;
			Class<?> valueType = (Class<?>) types[0];
			List<?> list = getConfigList(valueType, props, key);
			if (list != null) field.set(bean, list);
		} else {
			// Determine whether the field enables parsing configuration
			// e.g. iot.framework.actor.apps.app1.pool=true
			String enabled = props.getProperty(key);
			if (!StringHelper.isEmpty(enabled) && !ConvertHelper.toBoolean(enabled, false)) return;
			// Parse the sub-bean
			Object subBean = field.get(bean);
			if (subBean == null) {
				subBean = type.newInstance();
				field.set(bean, subBean);
			}
			fillConfigBean(type, subBean, props, key + ".");
		}
	}

}
