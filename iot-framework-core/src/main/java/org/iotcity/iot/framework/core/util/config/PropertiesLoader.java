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
	 * Gets a properties object from file.
	 * @param filePathName Properties file directory and the file name.<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties")
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param fromPackage Whether load file from package.
	 * @return Properties object result (not null).
	 */
	public final static Properties loadProperties(String filePathName, String encoding, boolean fromPackage) {
		if (filePathName == null || filePathName.length() == 0) return null;
		String key = fromPackage + "|" + filePathName;
		Properties prop = props.get(key);
		if (prop == null) {
			synchronized (props) {
				prop = props.get(key);
				if (prop == null) {
					prop = new Properties();
					FileHelper.loadProperties(prop, filePathName, encoding, fromPackage);
					props.put(key, prop);
				}
			}
		}
		return prop;
	}

	// --------------------------------- Load configure data methods ---------------------------------

	/**
	 * Load a configure file and set the properties to the bean (returns null when failed).
	 * @param <T> The bean type.
	 * @param beanClass The configure bean type.
	 * @param filePathName Configure file directory and the file name.<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties")
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test").
	 * @param fromPackage Whether load the file from package.
	 * @return The configure bean.
	 */
	public final static <T> T loadConfigBean(Class<T> beanClass, String filePathName, String encoding, String prefix, boolean fromPackage) {
		try {
			T bean = beanClass.newInstance();
			return loadConfigBean(bean, filePathName, encoding, prefix, fromPackage);
		} catch (Exception e) {
			System.err.println("Load configure to a bean error: " + filePathName);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Load a configure file and set the properties to the bean (returns null when failed).
	 * @param <T> The bean type.
	 * @param bean The configure bean.
	 * @param filePathName Configure file directory and the file name.<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties")
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test").
	 * @param fromPackage Whether load the file from package.
	 * @return The configure bean.
	 */
	public final static <T> T loadConfigBean(T bean, String filePathName, String encoding, String prefix, boolean fromPackage) {
		if (bean == null) return null;
		if (prefix == null) prefix = "";
		Properties props = loadProperties(filePathName, encoding, fromPackage);
		if (props == null) return null;
		if (prefix.length() > 0 && !prefix.endsWith(".")) prefix += ".";
		try {
			fillConfigBean(bean.getClass(), bean, props, prefix);
		} catch (Exception e) {
			System.err.println("Load configure to a bean error: " + filePathName);
			e.printStackTrace();
			return null;
		}
		return bean;
	}

	/**
	 * Load a configure file and set the properties to an array (returns null when failed).
	 * @param <T> The array type.
	 * @param beanClass The array component type.
	 * @param filePathName Configure file directory and the file name.<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties")
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.values").
	 * @param fromPackage Whether load the file from package.
	 * @return The configure array data.
	 */
	public final static <T> T loadConfigArray(Class<?> beanClass, String filePathName, String encoding, String prefix, boolean fromPackage) {
		if (beanClass == null || filePathName == null) return null;
		if (prefix == null) prefix = "";
		Properties props = loadProperties(filePathName, encoding, fromPackage);
		if (props == null) return null;
		return getConfigArray(beanClass, props, prefix);
	}

	/**
	 * Load a configure file and set the properties to a list (returns null when failed).
	 * @param <T> The parameterized type of list.
	 * @param beanClass The parameterized type class of list.
	 * @param filePathName Configure file directory and the file name.<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties")
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.list").
	 * @param fromPackage Whether load the file from package.
	 * @return The configure list data.
	 */
	public final static <T> List<T> loadConfigList(Class<T> beanClass, String filePathName, String encoding, String prefix, boolean fromPackage) {
		if (beanClass == null || filePathName == null) return null;
		if (prefix == null) prefix = "";
		Properties props = loadProperties(filePathName, encoding, fromPackage);
		if (props == null) return null;
		return getConfigList(beanClass, props, prefix);
	}

	/**
	 * Load a configure file and set the properties to a map (returns null when failed).
	 * @param <T> The parameterized type of map.
	 * @param beanClass The parameterized type class of map.
	 * @param filePathName Configure file directory and the file name.<br/>
	 *            (e.g. "org/iotcity/iot/framework/core/util/config/iot-properties-template.properties")
	 * @param encoding File encoding (optional, e.g. "UTF-8", if it is set to null, it will be judged automatically).
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.map").
	 * @param fromPackage Whether load the file from package.
	 * @return The configure map data.
	 */
	public final static <T> PropertiesMap<T> loadConfigMap(Class<T> beanClass, String filePathName, String encoding, String prefix, boolean fromPackage) {
		if (beanClass == null || filePathName == null) return null;
		if (prefix == null) prefix = "";
		Properties props = loadProperties(filePathName, encoding, fromPackage);
		if (props == null) return null;
		return getConfigMap(beanClass, props, prefix);
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
	public final static <T> T getConfigBean(Class<T> beanClass, Properties props, String prefix) {
		try {
			T bean = beanClass.newInstance();
			return getConfigBean(bean, props, prefix);
		} catch (Exception e) {
			System.err.println("Gets a bean configured from properties error: " + e.getMessage());
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
	public final static <T> T getConfigBean(T bean, Properties props, String prefix) {
		if (bean == null || props == null) return null;
		if (prefix == null) prefix = "";
		if (prefix.length() > 0 && !prefix.endsWith(".")) prefix += ".";
		try {
			fillConfigBean(bean.getClass(), bean, props, prefix);
		} catch (Exception e) {
			System.err.println("Gets a bean configured from properties error: " + e.getMessage());
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
	public final static <T> T getConfigArray(Class<?> beanClass, Properties props, String prefix) {
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
				try {
					Object v = beanClass.newInstance();
					Array.set(array, i, v);
					fillConfigBean(beanClass, v, props, pkey.prefix + ".");
				} catch (Exception e) {
					System.err.println("Gets an array configured from properties error: " + e.getMessage());
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
	public final static <T> List<T> getConfigList(Class<T> beanClass, Properties props, String prefix) {
		if (beanClass == null || props == null || StringHelper.isEmpty(prefix)) return null;
		String value = props.getProperty(prefix);
		if (StringHelper.isEmptyWithTrim(value)) return null;
		String[] values = value.trim().split("[,;]");
		if (values == null || values.length == 0) return null;
		List<T> list = new ArrayList<T>();
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
				try {
					T v = beanClass.newInstance();
					list.add(v);
					fillConfigBean(beanClass, v, props, pkey.prefix + ".");
				} catch (Exception e) {
					System.err.println("Gets a list configured from properties error: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	/**
	 * Gets a properties map configured from the properties configure (returns null when failed).
	 * @param <T> The parameterized type of map.
	 * @param beanClass The parameterized type class of map.
	 * @param props Properties object has loaded (not null).
	 * @param prefix The property prefix of the configuration file (e.g. "iot.properties.test.map").
	 * @return The configure bean map.
	 */
	public final static <T> PropertiesMap<T> getConfigMap(Class<T> beanClass, Properties props, String prefix) {
		if (beanClass == null || props == null || StringHelper.isEmpty(prefix)) return null;
		String value = props.getProperty(prefix);
		if (StringHelper.isEmptyWithTrim(value)) return null;
		String[] values = value.trim().split("[,;]");
		if (values == null || values.length == 0) return null;
		PropertiesMap<T> map = new PropertiesMap<>();
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
				try {
					T v = beanClass.newInstance();
					map.put(pkey.key, v);
					fillConfigBean(beanClass, v, props, pkey.prefix + ".");
				} catch (Exception e) {
					System.err.println("Gets a properties map configured from properties error: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return map;
	}

	// --------------------------------- Fill configure bean methods ---------------------------------

	/**
	 * Set values to property fields of the bean.
	 */
	private final static void fillConfigBean(Class<?> type, Object bean, Properties props, String prefix) throws Exception {
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
	private final static void fillConfigField(Field field, Class<?> type, Object bean, Properties props, String key) throws Exception {
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
