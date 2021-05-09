package org.iotcity.iot.framework.core.bus;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This bus data listener annotation is used to implement an instance of the {@link BusEventListener } interface, and the framework will automatically use this annotation to add the bus listener to bus event publisher.
 * @author ardon
 * @date 2021-05-09
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface BusDataListener {

	/**
	 * The data class type to listen on.
	 */
	Class<?> dataType();

	/**
	 * The execution order priority for this listener (the priority with the highest value is called first, 0 by default).
	 */
	int priority() default 0;

	/**
	 * Whether the bus data listener is enabled (optional, true by default).
	 * @return Returns true if the bus data listener is enabled; otherwise, returns false.
	 */
	boolean enabled() default true;

}
