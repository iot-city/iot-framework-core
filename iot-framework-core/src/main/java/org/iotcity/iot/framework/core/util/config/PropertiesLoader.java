package org.iotcity.iot.framework.core.util.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.iotcity.iot.framework.core.util.helper.ConvertHelper;
import org.iotcity.iot.framework.core.util.helper.FileHelper;

/**
 * Use configure loader to load properties file
 * @author Ardon
 */
public final class PropertiesLoader {

	/**
	 * Properties files map (this key is file name, the value is properties object)
	 */
	private static final Map<String, Properties> props = new HashMap<>();

	/**
	 * Gets a properties object
	 * @param filePathName Properties file directory and the file name (e.g. "org/iotcity/iot/framework/actor/iot-actor.properties")
	 * @param fromPackage Whether load file from package
	 * @return Properties Properties object result
	 */
	public static Properties loadProperties(String filePathName, String encoding, boolean fromPackage) {
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

	/**
	 * Load a configure file and set the properties to the bean (returns null when failed)
	 * @param beanClass The configure bean type
	 * @param filePathName Configure file directory and the file name (e.g. "org/iotcity/iot/framework/actor/iot-actor.properties")
	 * @param encoding File encoding (e.g. "UTF-8")
	 * @param prefix The property prefix of the configuration file (e.g. "iot.framework.actor.apps")
	 * @param fromPackage Whether load the file from package
	 * @return T The configure bean
	 */
	public static <T> T loadConfig(Class<T> beanClass, String filePathName, String encoding, String prefix, boolean fromPackage) {
		try {
			T bean = beanClass.newInstance();
			return loadConfigBean(bean, filePathName, encoding, prefix, fromPackage);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Load a configure file and set the properties to the bean (returns null when failed)
	 * @param bean The configure bean
	 * @param filePathName Configure file directory and the file name (e.g. "org/iotcity/iot/framework/actor/iot-actor.properties")
	 * @param encoding File encoding (e.g. "UTF-8")
	 * @param prefix The property prefix of the configuration file (e.g. "iot.framework.actor.apps")
	 * @param fromPackage Whether load the file from package
	 * @return T The configure bean
	 */
	public static <T> T loadConfigBean(T bean, String filePathName, String encoding, String prefix, boolean fromPackage) {
		if (bean == null) return null;
		if (prefix == null) prefix = "";
		if (prefix.length() > 0 && !prefix.endsWith(".")) prefix += ".";
		Properties props = loadProperties(filePathName, encoding, fromPackage);
		if (props == null) return null;
		try {
			fillConfigBean(bean.getClass(), bean, props, prefix);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return bean;
	}

	private static void fillConfigBean(Class<?> type, Object bean, Properties props, String prefix) throws Exception {
		Field[] fields = type.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (Modifier.isStatic(field.getModifiers())) continue;
				Class<?> fieldType = field.getType();
				String fieldName = field.getName();
				String key = prefix + fieldName;
				fillConfigField(field, fieldType, bean, props, key);
			}
		}
	}

	private static void fillConfigField(Field field, Class<?> type, Object bean, Properties props, String key) throws Exception {
		field.setAccessible(true);
		if (type.isPrimitive() || type == String.class || type == Date.class) {
			if (!props.containsKey(key)) return;
			field.set(bean, getFieldValue(type, props.getProperty(key), field.get(bean)));
		} else if (type.isArray()) {
			if (!props.containsKey(key)) return;
			Class<?> subType = type.getComponentType();
			if (subType.isPrimitive() || subType == String.class || subType == Date.class) {
				String value = props.getProperty(key);
				if (value != null && value.length() > 0) {
					String[] values = value.split("[,;]");
					if (values != null && values.length > 0) {
						Object array = Array.newInstance(subType, values.length);
						Object defaultValue = getDefaultValueByType(subType);
						for (int i = 0, c = values.length; i < c; i++) {
							Array.set(array, i, getFieldValue(subType, values[i], defaultValue));
						}
						field.set(bean, array);
					}
				}
			}
		} else {
			Object fobj = field.get(bean);
			if (fobj == null) {
				fobj = type.newInstance();
				field.set(bean, fobj);
			}
			fillConfigBean(type, fobj, props, key + ".");
		}
	}

	private static Object getFieldValue(Class<?> type, String value, Object defaultValue) {
		if (value != null) value = value.trim();
		if (type.isPrimitive()) {
			if (type == boolean.class || type == Boolean.class) {
				return ConvertHelper.toBoolean(value, (boolean) defaultValue);
			} else if (type == int.class || type == Integer.class) {
				return ConvertHelper.toInt(value, (int) defaultValue);
			} else if (type == long.class || type == Long.class) {
				return ConvertHelper.toLong(value, (long) defaultValue);
			} else if (type == float.class || type == Float.class) {
				return ConvertHelper.toFloat(value, (float) defaultValue);
			} else if (type == double.class || type == Double.class) {
				return ConvertHelper.toDouble(value, (double) defaultValue);
			} else if (type == short.class || type == Short.class) {
				return ConvertHelper.toShort(value, (short) defaultValue);
			} else if (type == byte.class || type == Byte.class) {
				return ConvertHelper.toByte(value, (byte) defaultValue);
			} else if (type == char.class || type == Character.class) {
				return ConvertHelper.toChar(value, (char) defaultValue);
			} else {
				return value == null ? defaultValue : value;
			}
		} else if (type == String.class) {
			return value == null ? defaultValue : value;
		} else if (type == Date.class) {
			return ConvertHelper.toDate(value, (Date) defaultValue);
		} else {
			return value == null ? defaultValue : value;
		}
	}

	private static Object getDefaultValueByType(Class<?> type) {
		if (type.isPrimitive()) {
			if (type == boolean.class || type == Boolean.class) {
				return false;
			} else if (type == int.class || type == Integer.class) {
				return 0;
			} else if (type == long.class || type == Long.class) {
				return 0L;
			} else if (type == float.class || type == Float.class) {
				return 0;
			} else if (type == double.class || type == Double.class) {
				return 0.0;
			} else if (type == short.class || type == Short.class) {
				return 0;
			} else if (type == byte.class || type == Byte.class) {
				return 0;
			} else if (type == char.class || type == Character.class) {
				return '\0';
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
