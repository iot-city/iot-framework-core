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
	 */
	<T> T getInstance(Class<?> clazz);

}
