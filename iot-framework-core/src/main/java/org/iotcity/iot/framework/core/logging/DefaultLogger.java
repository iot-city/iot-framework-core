package org.iotcity.iot.framework.core.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * The default logger using console to output information.<br/>
 * Use System.out.println() and System.err.println() to log messages
 * @author Ardon
 */
public class DefaultLogger implements Logger {

	// --------------------------- Private fields ----------------------------

	/**
	 * The logger name.
	 */
	protected final String name;
	/**
	 * Whether to use multiple colors to display log information.
	 */
	protected final boolean colorful;
	/**
	 * The Class whose name should be used in message. If null it will default to the calling class.
	 */
	protected final Class<?> clazz;
	/**
	 * The depth from get logger method to the logging message method.
	 */
	protected final int callerDepth;
	/**
	 * The configure map for all levels, the key is level name with upper case, the value is level configure (not null).
	 */
	protected final Map<String, LogLevel> levels;
	/**
	 * Date format object.
	 */
	protected final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for default logger.
	 * @param name Logger name.
	 * @param colorful Whether to use multiple colors to display log information.
	 * @param clazz The Class whose name should be used in message. If null it will default to the calling class.
	 * @param callerDepth The depth from get logger method to the logging message method (0 by default).
	 * @param levels The configure map for all levels, the key is level name with upper case, the value is level configure (required, not null).
	 */
	public DefaultLogger(String name, boolean colorful, Class<?> clazz, int callerDepth, Map<String, LogLevel> levels) {
		this.name = name == null ? "" : name;
		this.colorful = colorful;
		this.clazz = clazz;
		this.callerDepth = (callerDepth < 0 ? 0 : callerDepth) + 3;
		this.levels = levels == null ? new HashMap<>() : levels;
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public boolean config(LogLevel[] data, boolean reset) {
		if (data == null) return false;
		if (reset) this.levels.clear();
		for (LogLevel level : data) {
			if (level == null || StringHelper.isEmpty(level.name)) continue;
			this.levels.put(level.name.toUpperCase(), level);
		}
		return true;
	}

	@Override
	public boolean colorful() {
		return colorful;
	}

	@Override
	public Logger newInstance(String name, Class<?> clazz, int callerDepth) {
		return new DefaultLogger(name, colorful, clazz, callerDepth, levels);
	}

	@Override
	public void log(Object message) {
		this.outputMessage(LogLevel.LOG, message, null);
	}

	@Override
	public void log(Object message, Throwable e) {
		this.outputMessage(LogLevel.LOG, message, e);
	}

	@Override
	public final void trace(Object message) {
		this.outputMessage(LogLevel.TRACE, message, null);
	}

	@Override
	public final void trace(Object message, Throwable e) {
		this.outputMessage(LogLevel.TRACE, message, e);
	}

	@Override
	public final void debug(Object message) {
		this.outputMessage(LogLevel.DEBUG, message, null);
	}

	@Override
	public final void debug(Object message, Throwable e) {
		this.outputMessage(LogLevel.DEBUG, message, e);
	}

	@Override
	public final void info(Object message) {
		this.outputMessage(LogLevel.INFO, message, null);
	}

	@Override
	public final void info(Object message, Throwable e) {
		this.outputMessage(LogLevel.INFO, message, e);
	}

	@Override
	public final void warn(Object message) {
		this.outputMessage(LogLevel.WARN, message, null);
	}

	@Override
	public final void warn(Object message, Throwable e) {
		this.outputMessage(LogLevel.WARN, message, e);
	}

	@Override
	public final void error(Object message) {
		this.outputMessage(LogLevel.ERROR, message, null);
	}

	@Override
	public final void error(Object message, Throwable e) {
		this.outputMessage(LogLevel.ERROR, message, e);
	}

	@Override
	public final void fatal(Object message) {
		this.outputMessage(LogLevel.FATAL, message, null);
	}

	@Override
	public final void fatal(Object message, Throwable e) {
		this.outputMessage(LogLevel.FATAL, message, e);
	}

	/**
	 * Output message to console
	 * @param level Log level (reference: LogLevel.XXXX)
	 * @param message The message object to log.
	 * @param e The exception to log, including its stack trace.
	 */
	protected void outputMessage(String level, Object message, Throwable e) {
		// Get level config
		LogLevel config = this.levels.get(level);
		if (config == null) return;

		StringBuilder sb = new StringBuilder();
		StringBuilder sbColor = new StringBuilder();

		if (colorful) {
			// Set color
			if (config.fontColor != LogLevel.COLOR_FONT_DEFAULT || config.bgColor != LogLevel.COLOR_BG_DEFAULT) {
				// Font color format: \033[XX;XX;XXm
				sbColor.append("\033[");
				// Add font color
				if (config.fontColor != LogLevel.COLOR_FONT_DEFAULT) {
					sbColor.append(config.fontColor).append(";");
				}
				if (config.bgColor != LogLevel.COLOR_BG_DEFAULT) {
					sbColor.append(config.bgColor).append(";");
				}
				sbColor.append("m");
			}
			sb.append(sbColor);
		}

		// Append messages
		sb.append("[").append(format.format(new Date())).append("] ");
		// Append level name
		if (!LogLevel.LOG.equals(level)) {
			sb.append(level).append(": ");
		}
		if (this.name.length() > 0) sb.append("(").append(name).append(") ");
		sb.append(message);

		// Append method info
		if (config.methodTracking) {
			StackTraceElement[] traces = Thread.currentThread().getStackTrace();
			if (traces.length > this.callerDepth) {
				StackTraceElement ele = traces[this.callerDepth];
				sb.append(" [FROM: ").append(getSimpleClassName(ele.getClassName())).append(".").append(ele.getMethodName()).append("(...) -");
				getSourceFileLink(sb, colorful, sbColor, ele.getFileName(), ele.getLineNumber());
				sb.append("]");
			}
		} else if (config.classTracking) {
			// Append class info
			if (this.clazz == null) {
				StackTraceElement[] traces = Thread.currentThread().getStackTrace();
				if (traces.length > this.callerDepth) {
					StackTraceElement ele = traces[this.callerDepth];
					sb.append(" [FROM: ").append(getSimpleClassName(ele.getClassName())).append(".class -");
					getSourceFileLink(sb, colorful, sbColor, ele.getFileName(), ele.getLineNumber());
					sb.append("]");
				}
			} else {
				sb.append(" [FROM: ").append(this.clazz.getSimpleName()).append(".class -");
				getSourceFileLink(sb, colorful, sbColor, this.clazz.getSimpleName() + ".java", 0);
				sb.append("]");
			}
		}
		// End of color
		if (sbColor.length() > 0) {
			sb.append("\033[0m");
		}
		// Output info
		System.out.println(sb.toString());
		if (e != null) e.printStackTrace();
	}

	// --------------------------- Private static methods ----------------------------

	/**
	 * Get simple name of class path name
	 */
	private static final String getSimpleClassName(String className) {
		int pos = className.lastIndexOf(".");
		return pos == -1 ? className : className.substring(pos + 1);
	}

	/**
	 * Get java source file link
	 */
	private static final void getSourceFileLink(StringBuilder sb, boolean colorful, StringBuilder sbColor, String fileName, int lineNumber) {
		if (fileName != null && lineNumber >= 0) {
			if (colorful) {
				sb.append("\033[94;2m (");
				sb.append(fileName).append(":").append(lineNumber).append(")\033[0m");
				if (sbColor.length() > 0) sb.append(sbColor);
			} else {
				sb.append(" (").append(fileName).append(":").append(lineNumber).append(")");
			}
		} else {
			if (fileName == null) {
				sb.append(" (Unknown Source)");
			} else {
				sb.append(" (").append(fileName).append(")");
			}
		}
	}

}
