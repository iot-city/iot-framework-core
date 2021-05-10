package org.iotcity.iot.framework.core.beans;

/**
 * Default class instance factory for using "<b>clazz.getDeclaredConstructor().newInstance()</b>" to create an instance.
 * @author ardon
 * @date 2021-05-10
 */
public final class DefaultClassInstanceFactory implements ClassInstanceFactory {

	@Override
	public <T> T getInstance(Class<?> clazz) {
		if (clazz == null) return null;
		Object obj = null;
		try {
			obj = clazz.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		T ret = (T) obj;
		return ret;
	}

}
