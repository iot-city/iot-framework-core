package org.iotcity.iot.framework.core.util.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * Providing auxiliary functions for Java language development.
 * @author Ardon
 */
public final class JavaHelper {

	// ------------------------------------ Console methods -------------------------------

	/**
	 * Output an information message to console.
	 * @param message An information message.
	 */
	public static final void log(String... message) {
		if (message == null || message.length == 0) return;
		String date = "[" + ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS") + "] ";
		for (String msg : message) {
			System.out.println(date + msg);
		}
	}

	/**
	 * Output an error message to console.
	 * @param message An error message.
	 */
	public static final void err(String... message) {
		if (message == null || message.length == 0) return;
		String date = "[" + ConvertHelper.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS") + "] ";
		for (String msg : message) {
			System.err.println(date + msg);
		}
	}

	// ------------------------------------ Basic methods -------------------------------

	/**
	 * The the initial number for map
	 * @param count Estimated capacity
	 * @return int Initial number result
	 */
	public static final int getMapInitialCapacity(int count) {
		return (int) (count / 0.75 + 1);
	}

	/**
	 * Gets throwable object stack trace information
	 * @param e Throwable object (e.g. Exception object)
	 * @return String Stack trace information
	 */
	public static final String getThrowableTrace(Throwable e) {
		if (e != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		}
		return "none";
	}

	/**
	 * Determine whether the data type is a primitive type.<br/>
	 * <b>Primitive types: boolean, int, long, float, double, short, byte or char.</b>
	 * @param type The class type.
	 * @return Returns true if the data type is a primitive type; otherwise, returns false.
	 */
	public static final boolean isPrimitive(Class<?> type) {
		if (type == null) return false;
		// boolean, int, long, float, double, short, byte or char
		if (type.isPrimitive()) {
			return true;
		} else if (type == Boolean.class || type == Integer.class || type == Long.class || type == Float.class || type == Double.class || type == Short.class || type == Byte.class || type == Character.class) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the default data value according to the data type.
	 * @param type The class type.
	 * @return Object The default data value of current type.
	 */
	public static final Object getTypeDefaultValue(Class<?> type) {
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
	}

	// ------------------------------------ Preview methods -------------------------------

	/**
	 * Gets a data preview string.
	 * @param data Data object.
	 * @return Preview string of data value.
	 */
	public static final String getDataPreview(Object data) {
		StringBuilder sb = new StringBuilder();
		getDataPreview(data, sb);
		return sb.toString();
	}

	/**
	 * Gets a data preview string.
	 * @param data Data object.
	 * @param sb The StringBuilder object that appends the string.
	 */
	public static final void getDataPreview(Object data, StringBuilder sb) {
		if (data == null) {
			sb.append("null");
		} else {
			getDataPreviewValue(data, sb, false);
		}
	}

	/**
	 * Gets a class types array preview string (use simple name of class).
	 * @param types The class types.
	 * @return Preview string of type simple names.
	 */
	public static final String getTypesPreview(Class<?>[] types) {
		StringBuilder sb = new StringBuilder();
		getTypesPreview(types, sb);
		return sb.toString();
	}

	/**
	 * Gets a class types array preview string (use simple name of class).
	 * @param types The class types.
	 * @param sb The StringBuilder object that appends the string.
	 */
	public static final void getTypesPreview(Class<?>[] types, StringBuilder sb) {
		if (types == null) {
			sb.append("null");
		} else {
			sb.append("[");
			for (int i = 0, c = types.length; i < c; i++) {
				Class<?> type = types[i];
				if (i > 0) sb.append(", ");
				sb.append(type.getSimpleName());
			}
			sb.append("]");
		}
	}

	/**
	 * Gets a class types of data array preview string (use simple name of class).
	 * @param data The data array to get class types.
	 * @return Preview string of type simple names.
	 */
	public static final <T> String getDataTypesPreview(T[] data) {
		StringBuilder sb = new StringBuilder();
		getDataTypesPreview(data, sb);
		return sb.toString();
	}

	/**
	 * Gets a class types of data array preview string (use simple name of class).
	 * @param data The data array to get class types.
	 * @param sb The StringBuilder object that appends the string.
	 */
	public static final <T> void getDataTypesPreview(T[] data, StringBuilder sb) {
		if (data == null) {
			sb.append("null");
		} else {
			sb.append("[");
			for (int i = 0, c = data.length; i < c; i++) {
				if (i > 0) sb.append(", ");
				Object value = data[i];
				if (value == null) {
					sb.append("null");
				} else {
					sb.append(value.getClass().getSimpleName());
				}
			}
			sb.append("]");
		}
	}

	/**
	 * Get an array preview string.
	 * @param <T> Data class type.
	 * @param data Data array.
	 * @param withPreSimpleName Whether to append a prefix simple name of class type before the data value (if set it to true, the long result would be "Long: 1000").
	 * @return Preview string of data values.
	 */
	public static final <T> String getArrayPreview(T[] data, boolean withPreSimpleName) {
		StringBuilder sb = new StringBuilder();
		getArrayPreview(data, sb, withPreSimpleName);
		return sb.toString();
	}

	/**
	 * Get an array preview string.
	 * @param <T> Data class type.
	 * @param data Data array.
	 * @param sb The StringBuilder object that appends the string.
	 * @param withPreSimpleName Whether to append a prefix simple name of class type before the data value (if set it to true, the long result would be "Long: 1000").
	 */
	public static final <T> void getArrayPreview(T[] data, StringBuilder sb, boolean withPreSimpleName) {
		if (data == null) {
			sb.append("null");
		} else {
			sb.append("[");
			for (int i = 0, c = data.length; i < c; i++) {
				if (i > 0) sb.append(", ");
				getDataPreviewValue(data[i], sb, withPreSimpleName);
			}
			sb.append("]");
		}
	}

	/**
	 * Get a data preview string by class type.
	 * @param data Data object.
	 * @param sb The StringBuilder object that appends the string.
	 * @param withPreSimpleName Whether to append a prefix simple name of class type before the data value (if set it to true, the long result would be "Long: 1000").
	 */
	private static final void getDataPreviewValue(Object data, StringBuilder sb, boolean withPreSimpleName) {
		if (data == null) {
			sb.append("null");
		} else {
			Class<?> type = data.getClass();
			if (withPreSimpleName) sb.append(type.getSimpleName()).append(": ");
			if (isPrimitive(type)) {
				sb.append(data);
			} else if (type == String.class) {
				sb.append("\"").append(data).append("\"");
			} else if (type == Date.class) {
				sb.append("\"").append(ConvertHelper.formatDate((Date) data)).append("\"");
			} else {
				sb.append(data);
			}
		}
	}

}
