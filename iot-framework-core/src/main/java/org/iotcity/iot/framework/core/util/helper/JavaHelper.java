package org.iotcity.iot.framework.core.util.helper;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * JAVA encoding util
 * @author Ardon
 */
public final class JavaHelper {

	/**
	 * The the initial number for map
	 * @param count Estimated capacity
	 * @return int Initial number result
	 */
	public static int getMapInitialCapacity(int count) {
		return (int) (count / 0.75 + 1);
	}

	/**
	 * Gets throwable object stack trace information
	 * @param e Throwable object (e.g. Exception object)
	 * @return String Stack trace information
	 */
	public static String getThrowableTrace(Throwable e) {
		if (e != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		}
		return "none";
	}

}
