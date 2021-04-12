package org.iotcity.iot.framework.core.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.iotcity.iot.framework.core.logging.DefaultLoggerConfig.LevelConfig;

/**
 * The default logger using console to output information.<br/>
 * Use System.out.println() and System.err.println() to log messages
 * @author Ardon
 */
public class DefaultLogger implements Logger {

	// --------------------------- Private fields ----------------------------

	/**
	 * The logger name
	 */
	private final String name;
	/**
	 * Date format object
	 */
	private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	/**
	 * The Class whose name should be used in message. If null it will default to the calling class.
	 */
	private final Class<?> clazz;
	/**
	 * The depth from get logger method to the logging message method.
	 */
	private final int callerDepth;
	/**
	 * Logger configure object (not null)
	 */
	private final DefaultLoggerConfig config;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for default logger
	 * @param name Logger name
	 * @param clazz The Class whose name should be used in message. If null it will default to the calling class.
	 * @param callerDepth The depth from get logger method to the logging message method (0 by default)
	 * @param config Logger configure object (required, not null)
	 */
	public DefaultLogger(String name, Class<?> clazz, int callerDepth, DefaultLoggerConfig config) {
		this.name = name == null ? "" : name;
		this.clazz = clazz;
		this.callerDepth = (callerDepth < 0 ? 0 : callerDepth) + 3;
		this.config = config;
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void trace(Object message) {
		this.outputMessage(LogLevel.TRACE, message, null);
	}

	@Override
	public void trace(Object message, Throwable e) {
		this.outputMessage(LogLevel.TRACE, message, e);
	}

	@Override
	public void debug(Object message) {
		this.outputMessage(LogLevel.DEBUG, message, null);
	}

	@Override
	public void debug(Object message, Throwable e) {
		this.outputMessage(LogLevel.DEBUG, message, e);
	}

	@Override
	public void info(Object message) {
		this.outputMessage(LogLevel.INFO, message, null);
	}

	@Override
	public void info(Object message, Throwable e) {
		this.outputMessage(LogLevel.INFO, message, e);
	}

	@Override
	public void warn(Object message) {
		this.outputMessage(LogLevel.WARN, message, null);
	}

	@Override
	public void warn(Object message, Throwable e) {
		this.outputMessage(LogLevel.WARN, message, e);
	}

	@Override
	public void error(Object message) {
		this.outputMessage(LogLevel.ERROR, message, null);
	}

	@Override
	public void error(Object message, Throwable e) {
		this.outputMessage(LogLevel.ERROR, message, e);
	}

	@Override
	public void fatal(Object message) {
		this.outputMessage(LogLevel.FATAL, message, null);
	}

	@Override
	public void fatal(Object message, Throwable e) {
		this.outputMessage(LogLevel.FATAL, message, e);
	}

	/**
	 * Output message to console
	 */
	private void outputMessage(String level, Object message, Throwable e) {
		LevelConfig levelConfig = this.config.getLevel(level);
		if (levelConfig == null) return;

		StringBuilder sb = new StringBuilder();
		StringBuilder sbColor = new StringBuilder();

		// Set color
		if (levelConfig.fontColor != LogLevelColor.FONT_DEFAULT || levelConfig.bgColor != LogLevelColor.BG_DEFAULT) {
			// Font color format: \033[XX;XX;XXm
			sbColor.append("\033[");
			// Add font color
			if (levelConfig.fontColor != LogLevelColor.FONT_DEFAULT) {
				sbColor.append(levelConfig.fontColor).append(";");
			}
			if (levelConfig.bgColor != LogLevelColor.BG_DEFAULT) {
				sbColor.append(levelConfig.bgColor).append(";");
			}
			sbColor.append("m");
		}
		sb.append(sbColor);

		// Append messages
		sb.append("[").append(format.format(new Date())).append("] ").append(level).append(": ");
		if (this.name.length() > 0) sb.append("(").append(name).append(") ");
		sb.append(message);

		// Append method info
		if (levelConfig.methodTracking) {
			StackTraceElement[] traces = Thread.currentThread().getStackTrace();
			if (traces.length > this.callerDepth) {
				StackTraceElement ele = traces[this.callerDepth];
				sb.append(" [FROM: ").append(this.getSimpleClassName(ele.getClassName())).append(".").append(ele.getMethodName()).append("(...) -");
				this.getSourceFileLink(sb, sbColor, ele.getFileName(), ele.getLineNumber());
				sb.append("]");
			}
		} else if (levelConfig.classTracking) {
			// Append class info
			if (this.clazz == null) {
				StackTraceElement[] traces = Thread.currentThread().getStackTrace();
				if (traces.length > this.callerDepth) {
					StackTraceElement ele = traces[this.callerDepth];
					sb.append(" [FROM: ").append(this.getSimpleClassName(ele.getClassName())).append(".class -");
					this.getSourceFileLink(sb, sbColor, ele.getFileName(), ele.getLineNumber());
					sb.append("]");
				}
			} else {
				sb.append(" [FROM: ").append(this.clazz.getSimpleName()).append(".class -");
				this.getSourceFileLink(sb, sbColor, this.clazz.getSimpleName() + ".java", 0);
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

	/**
	 * Get simple name of class path name
	 */
	private String getSimpleClassName(String className) {
		int pos = className.lastIndexOf(".");
		return pos == -1 ? className : className.substring(pos + 1);
	}

	/**
	 * Get java source file link
	 */
	private void getSourceFileLink(StringBuilder sb, StringBuilder sbColor, String fileName, int lineNumber) {
		if (fileName != null && lineNumber >= 0) {
			sb.append("\033[94;4m (");
			sb.append(fileName).append(":").append(lineNumber).append(") \033[0m");
			if (sbColor.length() > 0) sb.append(sbColor);
		} else {
			if (fileName == null) {
				sb.append(" (Unknown Source) ");
			} else {
				sb.append(" (").append(fileName).append(") ");
			}
		}
	}

}
