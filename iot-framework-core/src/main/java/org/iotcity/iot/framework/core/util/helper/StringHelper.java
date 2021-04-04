package org.iotcity.iot.framework.core.util.helper;

import java.util.Random;
import java.util.UUID;

/**
 * String util
 * @author Ardon
 */
public final class StringHelper {

	// --------------------------- Private fields ----------------------------

	/**
	 * Random object
	 */
	private static final Random _Random = new Random();
	/**
	 * Keywords for random string
	 */
	private static final char[] _KeyWords = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

	// --------------------------- Public methods ----------------------------

	/**
	 * Determine whether the string object is null or empty (the length is 0)
	 * @param str String object
	 * @return boolean Whether null or empty
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * Determine whether the trimming string object is null or empty (the length is 0)
	 * @param str String object
	 * @return boolean Whether null or empty
	 */
	public static boolean isEmptyWithTrim(String str) {
		return str == null || str.length() == 0 || str.trim().length() == 0;
	}

	/**
	 * Trim string object (the return string is not null)
	 * @param str String object
	 * @return String A non null string that has removed the leading and trailing spaces
	 */
	public static String trim(String str) {
		return str == null ? "" : str.trim();
	}

	/**
	 * Gets the global UUID string (32-bit length string)
	 * @return String UUID string
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * Merge multiple strings (return result is not null)
	 * @param params String data to be merged
	 * @return String Merged string
	 */
	public static String concat(String... params) {
		if (params == null || params.length == 0) return "";
		StringBuilder sb = new StringBuilder();
		for (String str : params) {
			sb.append(str);
		}
		return sb.toString();
	}

	/**
	 * Merge multiple strings with separator (return result is not null)
	 * @param separator The separator in result (e.g. "," or ";")
	 * @param params String data to be merged
	 * @return String Merged string
	 */
	public static String concatAs(String separator, Object... params) {
		if (params == null || params.length == 0) return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0, c = params.length; i < c; i++) {
			if (i > 0) sb.append(separator);
			sb.append(params[i]);
		}
		return sb.toString();
	}

	/**
	 * Replace the placeholder in the string with the parameter array (such as: {0}, {1}, etc.)
	 * @param str Format template string (e.g. "The example for {0}, the keyword is {1}.")
	 * @param values Array of parameters to replace {0}.. {n}
	 * @return String Formated result string
	 */
	public static String format(String str, Object... values) {
		if (str == null || str.length() == 0 || values == null || values.length == 0) return str;
		String result = str;
		for (int i = 0, c = values.length; i < c; i++) {
			Object obj = values[i];
			String param = "";
			if (obj != null) {
				param = obj.toString();
			}
			param = param.replaceAll("\\\\", "\\\\\\\\");
			result = result.replaceAll("\\{\\s*" + i + "\\s*\\}", param);
		}
		return result;
	}

	/**
	 * Gets the byte length of the string (2 bytes for each Chinese character)
	 * @param str A string whose length needs to be calculated
	 * @return int Byte length
	 */
	public static int getBytesLength(String str) {
		if (str == null || str.length() == 0) return 0;
		int len = 0;
		for (int i = 0, c = str.length(); i < c; i++) {
			int ascii = Character.codePointAt(str, i);
			if (ascii >= 0 && ascii <= 255) {
				len++;
			} else {
				len += 2;
			}
		}
		return len;
	}

	/**
	 * Find the position of the string in the string array (return - 1 if not found)
	 * @param str String to search for
	 * @param inArray Array to search
	 * @return int The string index in array
	 */
	public static int findPos(String str, String[] inArray) {
		if (inArray == null || inArray.length == 0) return -1;
		int size = inArray.length;
		if (str == null) {
			for (int i = 0; i < size; i++) {
				if (inArray[i] == null) return i;
			}
		} else {
			for (int i = 0; i < size; i++) {
				if (str.equals(inArray[i])) return i;
			}
		}
		return -1;
	}

	/**
	 * Convert a string from horizontal line to hump mode (e.g. from "aaa-bbb-ccc" to "AaaBbbCcc")
	 * @param source The string to be converted
	 * @param separator Separator key (e.g. '-', '_')
	 * @return String Conversion result string
	 */
	public static String lineToHumpMode(String source, char separator) {
		if (source == null || source.length() == 0) return source;
		StringBuilder sb = new StringBuilder(source.length());
		char[] cs = source.toCharArray();
		for (int i = 0, c = cs.length; i < c; i++) {
			char ch = cs[i];
			if (ch == separator) {
				if (i == c - 1) break;
				i++;
				sb.append(Character.toUpperCase(cs[i]));
			} else {
				if (i == 0) {
					sb.append(Character.toUpperCase(ch));
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Convert a string from hump mode to horizontal line (e.g. from "AaaBbbCcc" to "aaa-bbb-ccc")
	 * @param source The string to be converted
	 * @param separator Separator key (e.g. '-', '_')
	 * @param keepCharCase Whether keep the source upper case characters (e.g. true > "AaaBbbCcc" to "Aaa-Bbb-Ccc")
	 * @return String Conversion result string
	 */
	public static String humpToLineMode(String source, char separator, boolean keepCharCase) {
		if (source == null || source.length() == 0) return source;
		StringBuilder sb = new StringBuilder(source.length());
		char[] cs = source.toCharArray();
		for (int i = 0, c = cs.length; i < c; i++) {
			char ch = cs[i];
			if (i == 0) {
				sb.append(keepCharCase ? ch : Character.toLowerCase(ch));
			} else {
				if (Character.isUpperCase(ch)) {
					sb.append(separator).append(keepCharCase ? ch : Character.toLowerCase(ch));
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Converts the first character of a string to upper case (e.g. from "abc" to "Abc")
	 * @param value The string to be converted
	 * @return Conversion result string
	 */
	public static String upperCaseFirstChar(String value) {
		if (value == null || value.length() == 0 || Character.isUpperCase(value.charAt(0))) return value;
		byte[] items = value.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}

	/**
	 * Converts the first character of a string to lower case (e.g. from "Abc" to "abc")
	 * @param value The string to be converted
	 * @return Conversion result string
	 */
	public static String lowerCaseFirstChar(String value) {
		if (value == null || value.length() == 0 || Character.isLowerCase(value.charAt(0))) return value;
		byte[] items = value.getBytes();
		items[0] = (byte) ((char) items[0] - 'A' + 'a');
		return new String(items);
	}

	// --------------------------- Public random string methods ----------------------------

	/**
	 * Gets a random double value string (e.g. "23.455645633")
	 * @return String Double value string
	 */
	public static String getRandomDoubleString() {
		return String.valueOf(_Random.nextDouble());
	}

	/**
	 * Gets a random number value string (e.g. "20398495")
	 * @param length The string length
	 * @return String Number value string
	 */
	public static String getRandomNumericString(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(_Random.nextInt(10));
		}
		return sb.toString();
	}

	/**
	 * Gets a random string (including English words and numbers)
	 * @param length The string length
	 * @return String A random string
	 */
	public static String getRandomString(int length) {
		int wlen = _KeyWords.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(_KeyWords[_Random.nextInt(wlen)]);
		}
		return sb.toString();
	}

}
