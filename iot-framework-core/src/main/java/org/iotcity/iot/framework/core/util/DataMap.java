package org.iotcity.iot.framework.core.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.core.util.helper.ConvertHelper;

/**
 * Data map that supports data type conversion.
 * @author ardon
 * @date 2021-05-26
 */
public class DataMap extends HashMap<String, Object> {

	/**
	 * Version ID for serialized form.
	 */
	private static final long serialVersionUID = 1L;

	// --------------------------- Constructors ----------------------------

	/**
	 * Constructs an empty <tt>DataMap</tt> with the default initial capacity (16) and the default load factor (0.75).
	 */
	public DataMap() {
		super();
	}

	/**
	 * Constructs an empty <tt>DataMap</tt> with the specified initial capacity and the default load factor (0.75).
	 * @param initialCapacity the initial capacity.
	 * @throws IllegalArgumentException if the initial capacity is negative.
	 */
	public DataMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructs an empty <tt>DataMap</tt> with the specified initial capacity and load factor.
	 * @param initialCapacity the initial capacity.
	 * @param loadFactor the load factor.
	 * @throws IllegalArgumentException if the initial capacity is negative or the load factor is non positive.
	 */
	public DataMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructs a new <tt>DataMap</tt> with the same mappings as the specified <tt>Map</tt>. The <tt>DataMap</tt> is created with default load factor (0.75) and an initial capacity sufficient to hold the mappings in the specified <tt>Map</tt>.
	 * @param m the map whose mappings are to be placed in this map.
	 * @throws NullPointerException if the specified map is null.
	 */
	public DataMap(Map<? extends String, ? extends Object> m) {
		super(m);
	}

	// --------------------------- Public methods ----------------------------

	/**
	 * Gets data value by specified return type (returns null if this map contains no mapping for the key).
	 * @param <T> The return class type.
	 * @param key Value key.
	 * @return T Value result.
	 */
	public <T> T getValue(String key) {
		@SuppressWarnings("unchecked")
		T v = (T) this.get(key);
		return v;
	}

	/**
	 * Gets data value from object to boolean (returns false by default).
	 * @param key Object key.
	 * @return boolean result.
	 */
	public boolean getBoolean(String key) {
		return ConvertHelper.toBoolean(this.get(key));
	}

	/**
	 * Gets data value from object to boolean.
	 * @param key Object key.
	 * @param defaultValue Return value when incompatible.
	 * @return boolean result.
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		return ConvertHelper.toBoolean(this.get(key), defaultValue);
	}

	/**
	 * Gets data value from object to char.
	 * @param key Object key.
	 * @param defaultValue Return value when incompatible.
	 * @return char result.
	 */
	public char getChar(String key, char defaultValue) {
		return ConvertHelper.toChar(this.get(key), defaultValue);
	}

	/**
	 * Gets data value from object to int (returns 0 by default).
	 * @param key Object key.
	 * @return int result.
	 */
	public int getInt(String key) {
		return ConvertHelper.toInt(this.get(key));
	}

	/**
	 * Gets data value from object to int.
	 * @param key Object key.
	 * @param defaultValue Return value when incompatible.
	 * @return int result.
	 */
	public int getInt(String key, int defaultValue) {
		return ConvertHelper.toInt(this.get(key), defaultValue);
	}

	/**
	 * Gets data value from object to int.
	 * @param key Object key.
	 * @param defaultValue Return value when incompatible.
	 * @param radix Specified radix.
	 * @return int result.
	 */
	public int getInt(String key, int defaultValue, int radix) {
		return ConvertHelper.toInt(this.get(key), defaultValue, radix);
	}

	/**
	 * Gets data value from object to short (returns 0 by default).
	 * @param key Object key.
	 * @return short result.
	 */
	public short getShort(String key) {
		return ConvertHelper.toShort(this.get(key));
	}

	/**
	 * Gets data value from object to short.
	 * @param key Object key.
	 * @param defaultValue Return value when incompatible.
	 * @return short result.
	 */
	public short getShort(String key, short defaultValue) {
		return ConvertHelper.toShort(this.get(key), defaultValue);
	}

	/**
	 * Gets data value from object to short.
	 * @param key Object key.
	 * @param defaultValue Return value when incompatible.
	 * @param radix Specified radix.
	 * @return short result.
	 */
	public short getShort(String key, short defaultValue, int radix) {
		return ConvertHelper.toShort(this.get(key), defaultValue, radix);
	}

	/**
	 * Gets data value from object to long (returns 0 by default).
	 * @param key Object key.
	 * @return long result.
	 */
	public long getLong(String key) {
		return ConvertHelper.toLong(this.get(key));
	}

	/**
	 * Gets data value from object to long.
	 * @param key Object key.
	 * @param defaultValue Return value when incompatible.
	 * @return long result.
	 */
	public long getLong(String key, long defaultValue) {
		return ConvertHelper.toLong(this.get(key), defaultValue);
	}

	/**
	 * Gets data value from object to float (returns 0 by default).
	 * @param key Object key.
	 * @return float result.
	 */
	public float getFloat(String key) {
		return ConvertHelper.toFloat(this.get(key));
	}

	/**
	 * Gets data value from object to float.
	 * @param key Object key.
	 * @param defaultValue Return value when incompatible.
	 * @return float result.
	 */
	public float getFloat(String key, float defaultValue) {
		return ConvertHelper.toFloat(this.get(key), defaultValue);
	}

	/**
	 * Gets data value from object to double (returns 0 by default).
	 * @param key Object key.
	 * @return double result.
	 */
	public double getDouble(String key) {
		return ConvertHelper.toDouble(this.get(key));
	}

	/**
	 * Gets data value from object to double.
	 * @param key Object key.
	 * @param defaultValue Return value when incompatible.
	 * @return double result.
	 */
	public double getDouble(String key, double defaultValue) {
		return ConvertHelper.toDouble(this.get(key), defaultValue);
	}

	/**
	 * Gets data value from object to byte (returns 0 by default).
	 * @param key Object key.
	 * @return byte result.
	 */
	public byte getByte(String key) {
		return ConvertHelper.toByte(this.get(key));
	}

	/**
	 * Gets data value from object to byte.
	 * @param key Object key.
	 * @param defaultValue Return value when incompatible.
	 * @return byte result.
	 */
	public byte getByte(String key, byte defaultValue) {
		return ConvertHelper.toByte(this.get(key), defaultValue);
	}

	/**
	 * Gets data value from object to byte.
	 * @param key Object key.
	 * @param defaultValue Return value when incompatible.
	 * @param radix Specified radix.
	 * @return byte result.
	 */
	public byte getByte(String key, byte defaultValue, int radix) {
		return ConvertHelper.toByte(this.get(key), defaultValue, radix);
	}

	/**
	 * Gets data value from object to BigDecimal (returns null by default).
	 * @param key Object key.
	 * @return BigDecimal result.
	 */
	public BigDecimal getBigDecimal(String key) {
		return ConvertHelper.toBigDecimal(this.get(key));
	}

	/**
	 * Gets data value from object to BigDecimal.
	 * @param key Object key.
	 * @param defaultValue Return value when incompatible.
	 * @return BigDecimal result.
	 */
	public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
		return ConvertHelper.toBigDecimal(this.get(key), defaultValue);
	}

	/**
	 * Gets data value from object to Date (returns null by default).<br/>
	 * Acceptable date formats include: Date, 1602331954000 (ms), "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss" or "yyyy-MM-dd".
	 * @param key Object key.
	 * @return Date result.
	 */
	public Date getDate(String key) {
		return ConvertHelper.toDate(this.get(key));
	}

	/**
	 * Gets data value from object to Date.<br/>
	 * Acceptable date formats include: Date, 1602331954000 (ms), "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss" or "yyyy-MM-dd".
	 * @param key Object key.
	 * @param defaultValue Return value when incompatible.
	 * @return Date result.
	 */
	public Date getDate(String key, Date defaultValue) {
		return ConvertHelper.toDate(this.get(key), defaultValue);
	}

}
