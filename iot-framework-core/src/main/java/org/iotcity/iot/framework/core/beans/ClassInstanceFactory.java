package org.iotcity.iot.framework.core.beans;

/**
 * Class instance factory for object instance creation.
 * @author ardon
 * @date 2021-05-10
 */
public interface ClassInstanceFactory {

	/**
	 * Create or get an instance of the declaring class (returns the class instance, it will returns null when no instance for specified class).
	 * @param <T> The instance class type.
	 * @param clazz The class of object instance.
	 * @return An object created by factory.
	 * @throws Exception Throw an exception when an error is encountered.
	 */
	<T> T getInstance(Class<?> clazz) throws Exception;

	/**
	 * Create or get an instance of the declaring class from global instance factory (returns the class instance, it will returns null when no instance for specified class).
	 * @param <T> The instance class type.
	 * @param clazz The class of object instance.
	 * @param parameterTypes The parameter class array of constructor.
	 * @param initargs The array of objects to be passed as arguments to the constructor call.
	 * @return An object created by factory.
	 * @throws Exception Throw an exception when an error is encountered.
	 */
	<T> T getInstance(Class<?> clazz, Class<?>[] parameterTypes, Object[] initargs) throws Exception;

}
