package org.iotcity.iot.framework.core.beans;

/**
 * Default class instance factory for using "<b>clazz.getConstructor(Class<?>... parameterTypes).newInstance(Object ... initargs)</b>" to create an instance.
 * @author ardon
 * @date 2021-05-10
 */
public final class DefaultClassInstanceFactory implements ClassInstanceFactory {

	@Override
	public final <T> T getInstance(Class<?> clazz) throws Exception {
		if (clazz == null) return null;
		@SuppressWarnings("unchecked")
		T ret = (T) clazz.getConstructor().newInstance();
		return ret;
	}

	@Override
	public final <T> T getInstance(Class<?> clazz, Class<?>[] parameterTypes, Object[] initargs) throws Exception {
		if (clazz == null) return null;
		@SuppressWarnings("unchecked")
		T ret = (T) clazz.getConstructor(parameterTypes).newInstance(initargs);
		return ret;
	}

}
