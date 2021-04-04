package org.iotcity.iot.framework.core.annotation;

/**
 * Annotation parser for class
 * @author Ardon
 */
public interface AnnotationParser {

	/**
	 * Use this method to parse useful annotations
	 * @param clazz Current class ready to parse
	 */
	void parse(Class<?> clazz);

}
