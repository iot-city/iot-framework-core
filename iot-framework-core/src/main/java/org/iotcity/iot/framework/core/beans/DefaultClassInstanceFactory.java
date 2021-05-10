package org.iotcity.iot.framework.core.beans;

/**
 * Default class instance factory for using "<b>clazz.getDeclaredConstructor().newInstance()</b>" to create an instance.
 * @author ardon
 * @date 2021-05-10
 */
public final class DefaultClassInstanceFactory implements ClassInstanceFactory {

	@Override
	public <T> T getInstance(Class<?> clazz) throws Exception {
		if (clazz == null) return null;
		@SuppressWarnings("unchecked")
		T ret = (T) clazz.getDeclaredConstructor().newInstance();
		return ret;
	}

}
