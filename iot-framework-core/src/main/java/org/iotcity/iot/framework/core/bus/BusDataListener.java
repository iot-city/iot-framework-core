package org.iotcity.iot.framework.core.bus;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This bus data listener annotation is used to implement an instance of the {@link BusEventListener }, and the framework will automatically use this annotation to add the bus listener to bus event publisher.<br/>
 * <br/>
 * Example 1, set up a simple data event listener for class:<br/>
 * 
 * <pre>
 *    &#064;BusDataListener(ExampleEventData.class)
 *    public class ExampleEventListener implements BusEventListener {
 *    
 *        &#64;Override
 *        public boolean onEvent(BusEvent event) {
 *            ExampleEventData data = event.getData();
 *            ...
 *        }
 *    
 *    }
 * </pre>
 * 
 * <br/>
 * Example 2, set up a filter event listener for data class:<br/>
 * 
 * <pre>
 *    &#064;BusDataListener(value = ExampleEventData.class, filterEvent = ExampleEvent.clss)
 *    public class ExampleEventListener implements BusEventListener {
 *    
 *        &#64;Override
 *        public boolean onEvent(BusEvent event) {
 *            ExampleEvent exampleEvent = (ExampleEvent) event;
 *            String name = exampleEvent.getEaxmpleName();
 *            ExampleEventData data = exampleEvent.getData();
 *            ...
 *        }
 *    
 *    }
 * </pre>
 * 
 * <br/>
 * Example 3, set up a multi-options data event listener for class:<br/>
 * 
 * <pre>
 *    &#064;BusDataListener(value = ExampleEventData.class, priority = 1, filterEvent = ExampleEvent.clss, enabled = true)
 *    public class ExampleEventListener implements BusEventListener {
 *    
 *        &#64;Override
 *        public boolean onEvent(BusEvent event) {
 *            ExampleEventData data = event.getData();
 *            ...
 *        }
 *    
 *    }
 * </pre>
 * 
 * <b>NOTICE: </b><br/>
 * <b>This annotation needs to be used with the <b>{@link BusEventListener }</b> interface in class definition.<br/>
 * @author ardon
 * @date 2021-05-09
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface BusDataListener {

	/**
	 * The data class type in event object to listen on (required, the listener will respond to events of the currently specified data type and inherited subclass types).
	 */
	Class<?> value();

	/**
	 * The execution order priority for this listener (optional, the priority with the highest value is called first, 0 by default).
	 */
	int priority() default 0;

	/**
	 * The class type of event to listen on (optional, the listener will respond to events of the currently specified event type and inherited subclass types).
	 */
	Class<? extends BusEvent> filterEvent() default BusEvent.class;

	/**
	 * Whether the bus data listener is enabled (optional, true by default).
	 * @return Returns true if the bus data listener is enabled; otherwise, returns false.
	 */
	boolean enabled() default true;

}
