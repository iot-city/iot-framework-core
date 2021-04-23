package org.iotcity.iot.framework.core.util.helper;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Data format conversion util
 * @author Ardon
 */
public final class ConvertHelper {

	// --------------------------- Private fields ----------------------------

	/**
	 * Hex characters
	 */
	private static final String HEX_CHARS = "0123456789ABCDEF";

	// --------------------------- Public methods for HEX ----------------------------

	/**
	 * Convert string from ASCII to HEX
	 * @param str ASCII string
	 * @return String HEX string
	 */
	public final static String string2Hex(String str) {
		char[] chars = str.toCharArray();
		StringBuilder hex = new StringBuilder();
		for (int i = 0; i < chars.length; i++) {
			String h = Integer.toHexString((int) chars[i]);
			hex.append(h.length() == 1 ? ("0" + h) : h);
		}
		return hex.toString().toUpperCase();
	}

	/**
	 * Convert string from HEX to ASCII
	 * @param hex HEX string
	 * @return String ASCII string
	 */
	public final static String hex2String(String hex) {
		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < hex.length() - 1; i += 2) {
			String output = hex.substring(i, (i + 2));
			int decimal = Integer.parseInt(output, 16);
			sb.append((char) decimal);
			temp.append(decimal);
		}
		return sb.toString();
	}

	/**
	 * Convert bytes to HEX string
	 * @param bs Data bytes
	 * @return String HEX String
	 */
	public final static String byte2Hex(byte[] bs) {
		if (bs == null || bs.length == 0) return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0, c = bs.length; i < c; i++) {
			String stmp = Integer.toHexString(bs[i] & 0xFF);
			if (stmp.length() < 2) {
				sb.append("0");
				sb.append(stmp);
			} else {
				sb.append(stmp);
			}
		}
		return sb.toString().toUpperCase().trim();
	}

	/**
	 * Convert bytes to HEX string by specified separator
	 * @param bs Data bytes
	 * @param sperator Used to separate each HEX data (e.g. 0A:01:FF)
	 * @return String HEX string
	 */
	public final static String byte2Hex(byte[] bs, String separator) {
		if (bs == null || bs.length == 0) return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0, c = bs.length; i < c; i++) {
			if (i > 0) sb.append(separator);
			String stmp = Integer.toHexString(bs[i] & 0xFF);
			if (stmp.length() < 2) {
				sb.append("0");
				sb.append(stmp);
			} else {
				sb.append(stmp);
			}
		}
		return sb.toString().toUpperCase().trim();
	}

	/**
	 * Convert HEX string to bytes
	 * @param hex HEX string
	 * @return byte[] Data bytes
	 */
	public final static byte[] hex2Bytes(String hex) {
		int hexLen = 0;
		if (hex == null || (hexLen = hex.length()) == 0) return null;
		if (hexLen == 1) hex = "0" + hex;
		int len = (hexLen / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toUpperCase().toCharArray();
		try {
			for (int i = 0; i < len; i++) {
				int pos = i * 2;
				result[i] = (byte) (HEX_CHARS.indexOf(achar[pos]) << 4 | HEX_CHARS.indexOf(achar[pos + 1]));
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	// --------------------------- Public methods for primitive ----------------------------

	/**
	 * Convert data from object to boolean (return false by default)
	 * @param value Object value
	 * @return boolean result
	 */
	public final static boolean toBoolean(Object value) {
		return toBoolean(value, false);
	}

	/**
	 * Convert data from object to boolean
	 * @param value Object value
	 * @param defaultValue Return value when incompatible
	 * @return boolean result
	 */
	public final static boolean toBoolean(Object value, boolean defaultValue) {
		if (value == null) return defaultValue;
		Class<?> clazz = value.getClass();
		if (clazz.equals(boolean.class) || value instanceof Boolean) {
			return (boolean) value;
		} else if (clazz.equals(int.class) || value instanceof Integer) {
			return ((int) value) == -1;
		} else {
			String ret = value.toString();
			if (ret.length() == 0) {
				return defaultValue;
			} else {
				ret = ret.toUpperCase();
				return (ret.equals("TRUE") || ret.equals("Y"));
			}
		}
	}

	/**
	 * Convert data from object to char
	 * @param value Object value
	 * @param defaultValue Return value when incompatible
	 * @return char result
	 */
	public final static char toChar(Object value, char defaultValue) {
		if (value == null) return defaultValue;
		Class<?> clazz = value.getClass();
		if (clazz.equals(char.class) || value instanceof Character) {
			return (char) value;
		} else {
			String ret = value.toString();
			if (ret.length() == 0) {
				return defaultValue;
			} else {
				return ret.charAt(0);
			}
		}
	}

	/**
	 * Convert data from object to int (return 0 by default)
	 * @param value Object value
	 * @return int result
	 */
	public final static int toInt(Object value) {
		return toInt(value, 0, 10);
	}

	/**
	 * Convert data from object to int
	 * @param value Object value
	 * @param defaultValue Return value when incompatible
	 * @return int result
	 */
	public final static int toInt(Object value, int defaultValue) {
		return toInt(value, defaultValue, 10);
	}

	/**
	 * Convert data from object to int
	 * @param value Object value
	 * @param defaultValue Return value when incompatible
	 * @param radix Specified radix
	 * @return int result
	 */
	public final static int toInt(Object value, int defaultValue, int radix) {
		if (value == null) return defaultValue;
		if (value.getClass().equals(int.class) || value instanceof Integer) {
			return (int) value;
		} else if (value.getClass().equals(double.class) || value instanceof Double) {
			return ((Double) value).intValue();
		} else {
			String s = value.toString();
			if (s.length() == 0) {
				return defaultValue;
			} else {
				try {
					return Integer.parseInt(s, radix);
				} catch (Exception e) {
				}
				return defaultValue;
			}
		}
	}

	/**
	 * Convert data from object to short (return 0 by default)
	 * @param value Object value
	 * @return short result
	 */
	public final static short toShort(Object value) {
		return toShort(value, (short) 0, 10);
	}

	/**
	 * Convert data from object to short
	 * @param value Object value
	 * @param defaultValue Return value when incompatible
	 * @return short result
	 */
	public final static short toShort(Object value, short defaultValue) {
		return toShort(value, defaultValue, 10);
	}

	/**
	 * Convert data from object to short
	 * @param value Object value
	 * @param defaultValue Return value when incompatible
	 * @param radix Specified radix
	 * @return short result
	 */
	public final static short toShort(Object value, short defaultValue, int radix) {
		if (value == null) return defaultValue;
		if (value.getClass().equals(short.class) || value instanceof Short) {
			return (short) value;
		} else if (value.getClass().equals(int.class) || value instanceof Integer) {
			return ((Integer) value).shortValue();
		} else if (value.getClass().equals(double.class) || value instanceof Double) {
			return ((Double) value).shortValue();
		} else {
			String s = value.toString();
			if (s.length() == 0) {
				return defaultValue;
			} else {
				try {
					return Short.valueOf(s, radix);
				} catch (Exception e) {
				}
				return defaultValue;
			}
		}
	}

	/**
	 * Convert data from object to long (return 0 by default)
	 * @param value Object value
	 * @return long result
	 */
	public final static long toLong(Object value) {
		return toLong(value, 0L);
	}

	/**
	 * Convert data from object to long
	 * @param value Object value
	 * @param defaultValue Return value when incompatible
	 * @return long result
	 */
	public final static long toLong(Object value, long defaultValue) {
		if (value == null) return defaultValue;
		if (value.getClass().equals(long.class) || value instanceof Long) {
			return (long) value;
		} else {
			String s = value.toString();
			if (s.length() == 0) {
				return defaultValue;
			} else {
				try {
					return Double.valueOf(s).longValue();
				} catch (Exception e) {
				}
				return defaultValue;
			}
		}
	}

	/**
	 * Convert data from object to float (return 0 by default)
	 * @param value Object value
	 * @return float result
	 */
	public final static float toFloat(Object value) {
		return toFloat(value, 0F);
	}

	/**
	 * Convert data from object to float
	 * @param value Object value
	 * @param defaultValue Return value when incompatible
	 * @return float result
	 */
	public final static float toFloat(Object value, float defaultValue) {
		if (value == null) return defaultValue;
		if (value.getClass().equals(float.class) || value instanceof Float) {
			return (float) value;
		} else if (value instanceof Double) {
			return ((Double) value).floatValue();
		} else {
			String s = value.toString();
			if (s.length() == 0) {
				return defaultValue;
			} else {
				try {
					return Double.valueOf(s).floatValue();
				} catch (Exception e) {
				}
				return defaultValue;
			}
		}
	}

	/**
	 * Convert data from object to double (return 0 by default)
	 * @param value Object value
	 * @return double result
	 */
	public final static double toDouble(Object value) {
		return toDouble(value, 0D);
	}

	/**
	 * Convert data from object to double
	 * @param value Object value
	 * @param defaultValue Return value when incompatible
	 * @return double result
	 */
	public final static double toDouble(Object value, double defaultValue) {
		if (value == null) return defaultValue;
		if (value.getClass().equals(double.class) || value instanceof Double) {
			return (double) value;
		} else {
			String s = value.toString();
			if (s.length() == 0) {
				return defaultValue;
			} else {
				try {
					return Double.valueOf(s).doubleValue();
				} catch (Exception e) {
				}
				return defaultValue;
			}
		}
	}

	/**
	 * Convert data from object to byte (return 0 by default)
	 * @param value Object value
	 * @return byte result
	 */
	public final static byte toByte(Object value) {
		return toByte(value, (byte) 0, 10);
	}

	/**
	 * Convert data from object to byte
	 * @param value Object value
	 * @param defaultValue Return value when incompatible
	 * @return byte result
	 */
	public final static byte toByte(Object value, byte defaultValue) {
		return toByte(value, defaultValue, 10);
	}

	/**
	 * Convert data from object to byte
	 * @param value Object value
	 * @param defaultValue Return value when incompatible
	 * @param radix Specified radix
	 * @return byte result
	 */
	public final static byte toByte(Object value, byte defaultValue, int radix) {
		if (value == null) return defaultValue;
		if (value instanceof Byte || value instanceof Integer) {
			return Byte.valueOf(value.toString());
		} else {
			String s = value.toString();
			if (s.length() == 0) {
				return defaultValue;
			} else {
				try {
					byte c = (byte) Integer.parseInt(s, radix);
					return c;
				} catch (Exception e) {
				}
				return defaultValue;
			}
		}
	}

	// --------------------------- Public methods for other type ----------------------------

	/**
	 * Convert data from object to BigDecimal (return null by default)
	 * @param value Object value
	 * @return BigDecimal result
	 */
	public final static BigDecimal toBigDecimal(Object value) {
		return toBigDecimal(value, null);
	}

	/**
	 * Convert data from object to BigDecimal
	 * @param value Object value
	 * @param defaultValue Return value when incompatible
	 * @return BigDecimal result
	 */
	public final static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
		if (value == null) return defaultValue;
		return new BigDecimal(value.toString());
	}

	/**
	 * Convert data from object to Date (return null by default).<br/>
	 * Acceptable date formats include: Date, 1602331954000 (ms), "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss" or "yyyy-MM-dd".
	 * @param value Object value
	 * @return Date result
	 */
	public final static Date toDate(Object value) {
		return toDate(value, null);
	}

	/**
	 * Convert data from object to Date.<br/>
	 * Acceptable date formats include: Date, 1602331954000 (ms), "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss" or "yyyy-MM-dd".
	 * @param value Object value.
	 * @param defaultValue Return value when incompatible.
	 * @return Date result.
	 */
	public final static Date toDate(Object value, Date defaultValue) {
		if (value == null) return defaultValue;
		if (value instanceof Date) return (Date) value;
		if (value instanceof Long || value instanceof Integer) {
			return new Date((Long) value);
		}
		String str = value.getClass() == String.class ? (String) value : String.valueOf(value);
		int len = str.length();
		String format;
		if (len == 23) {
			format = "yyyy-MM-dd HH:mm:ss.SSS";
		} else if (len == 10) {
			format = "yyyy-MM-dd";
		} else {
			if (Pattern.compile("^\\d+-\\d+-\\d+(\\s+)\\d+:\\d+:\\d+$").matcher(str).matches()) {
				format = "yyyy-MM-dd HH:mm:ss";
			} else if (Pattern.compile("^\\d+-\\d+-\\d+$").matcher(str).matches()) {
				format = "yyyy-MM-dd";
			} else {
				format = "";
			}
		}
		try {
			final DateFormat df = new SimpleDateFormat(format);
			ParsePosition pp = new ParsePosition(0);
			Date d = df.parse(str, pp);
			return d == null ? defaultValue : d;
		} catch (final Exception e) {
		}
		return defaultValue;
	}

	/**
	 * Convert data from object to Date (return null by default)
	 * @param value Object value
	 * @param format The date format to match the object value (e.g. "yyyy-MM-dd HH:mm:ss.SSS")
	 * @return Date result
	 */
	public final static Date toDate(String value, String format) {
		return toDate(value, format, null);
	}

	/**
	 * Convert data from object to Date
	 * @param value Object value
	 * @param format The date format to match the object value (e.g. "yyyy-MM-dd HH:mm:ss.SSS")
	 * @param defaultValue Return value when incompatible.
	 * @return Date result
	 */
	public final static Date toDate(String value, String format, Date defaultValue) {
		try {
			final DateFormat df = new SimpleDateFormat(format);
			ParsePosition pp = new ParsePosition(0);
			return df.parse(value, pp);
		} catch (final Exception e) {
		}
		return defaultValue;
	}

	/**
	 * Determine whether the data type supports data conversion.<br/>
	 * The supported types include: boolean, int, long, float, double, short, byte, char, String and Date.
	 * @param type The data class type.
	 * @return Whether supports data conversion.
	 */
	public final static boolean isConvertible(Class<?> type) {
		return type == String.class || JavaHelper.isPrimitive(type) || type == Date.class;
	}

	/**
	 * Converts data to a value of the specified data type<br/>
	 * The supported types include: boolean, int, long, float, double, short, byte, char, String and Date.
	 * @param type The data class type of result.
	 * @param value The data value before conversion.
	 * @return Data value after conversion.
	 */
	public final static Object convertTo(Class<?> type, Object value) {
		return convertTo(type, value, JavaHelper.getTypeDefaultValue(type));
	}

	/**
	 * Converts data to a value of the specified data type<br/>
	 * The supported types include: boolean, int, long, float, double, short, byte, char, String and Date.
	 * @param type The data class type of result.
	 * @param value The data value before conversion.
	 * @param defaultValue The default value when the value is null or incompatible.
	 * @return Data value after conversion.
	 */
	public final static Object convertTo(Class<?> type, Object value, Object defaultValue) {
		if (value == null) {
			return defaultValue;
		} else if (type == String.class) {
			return String.valueOf(value);
		} else if (value.getClass() == String.class) {
			value = ((String) value).trim();
		}
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
		} else if (type == Date.class) {
			return ConvertHelper.toDate(value, (Date) defaultValue);
		} else {
			return value;
		}
	}

	// --------------------------- Public methods for formating ----------------------------

	/**
	 * Convert data from date to formated string (return null by default).<br/>
	 * <b>Current datetime format: "yyyy-MM-dd HH:mm:ss".</b>
	 * @param date Data value
	 * @return String result
	 */
	public final static String formatDate(Date date) {
		try {
			if (date != null) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return df.format(date);
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Convert data from date to formated string (return null by default)
	 * @param date Data value
	 * @param format Output string format (e.g. "yyyy-MM-dd HH:mm:ss.SSS")
	 * @return String result
	 */
	public final static String formatDate(Date date, String format) {
		try {
			if (date != null) {
				DateFormat df = new SimpleDateFormat(format);
				return df.format(date);
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Convert data from double to formated string
	 * @param number Data value
	 * @param format The result formating string (e.g. "#,###.0#", "#.00", "###", ".###" )
	 * @return String result
	 */
	public final static String formatNumber(double number, String format) {
		String sReturn = "";
		DecimalFormat nf = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
		nf.applyPattern(format);
		try {
			sReturn = nf.format(number);
		} catch (Exception e) {
			sReturn = nf.format(0);
		}
		return sReturn;
	}

	/**
	 * Format minutes to "HH:mm" format (e.g. format 80 minutes to "01:20").
	 * @param minutes The minutes begins from midnight (00:00).
	 * @return Formated string (e.g. "01:20").
	 */
	public final static String formatMinutes(int minutes) {
		int h = Double.valueOf(Math.floor(minutes / 60.0)).intValue();
		int m = minutes % 60;
		return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m);
	}

	/**
	 * Format milliseconds to "D HH:mm:ss:SSS" format (e.g. format 4834853 milliseconds to "0D 01:20:34.853").
	 * @param ms The milliseconds begins from midnight (00:00:00.000).
	 * @param skipZeroFields Whether ignore the leading data segment with a value of 0 (e.g. Returns "10:24.532" when this data is set to true).
	 * @return Formated string (e.g. "0D 01:20:34.853").
	 */
	public final static String formatMilliseconds(long ms, boolean skipZeroFields) {
		boolean isNegative = (ms < 0);
		if (isNegative) ms = Math.abs(ms);
		long milliseconds = ms % 1000;
		ms = (ms - milliseconds) / 1000;
		long seconds = ms % 60;
		ms = (ms - seconds) / 60;
		long minites = ms % 60;
		ms = (ms - minites) / 60;
		long hours = ms % 24;
		ms = (ms - hours) / 24;
		long day = ms;
		if (skipZeroFields) {
			StringBuilder sb = new StringBuilder(isNegative ? "-" : "");
			if (day > 0) sb.append(day).append("D ");
			if (hours > 0 || day > 0) sb.append(hours < 10 ? "0" + hours : hours).append(":");
			if (minites > 0 || hours > 0 || day > 0) sb.append(minites < 10 ? "0" + minites : minites).append(":");
			if (seconds > 0 || minites > 0 || hours > 0 || day > 0) sb.append(seconds < 10 ? "0" + seconds : seconds).append(".");
			sb.append(String.format("%03d", milliseconds));
			return sb.toString();
		} else {
			String format = isNegative ? "-%dD %02d:%02d:%02d.%03d" : "%dD %02d:%02d:%02d.%03d";
			return String.format(format, day, hours, minites, seconds, milliseconds);
		}
	}

}
