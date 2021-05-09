package org.iotcity.iot.framework.core.config;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This auto configuration manager annotation is used to implement an instance of the {@link ConfigureManager } interface, and the framework will automatically use this configuration manager for data configuration.
 * @author Ardon
 * @date 2021-04-25
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface AutoConfigureManager {

	/**
	 * Whether the auto configuration function is enabled (optional, true by default).
	 * @return Returns true if automatic configuration is supported; otherwise, returns false.
	 */
	boolean enabled() default true;

}
