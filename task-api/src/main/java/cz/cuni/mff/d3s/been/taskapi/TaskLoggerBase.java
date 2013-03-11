package cz.cuni.mff.d3s.been.taskapi;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 11.03.13
 * Time: 12:29
 * To change this template use File | Settings | File Templates.
 */
public abstract class TaskLoggerBase extends MarkerIgnoringBase {

	private static final int LOG_LEVEL_TRACE = 1;
	private static final int LOG_LEVEL_DEBUG = 2;
	private static final int LOG_LEVEL_INFO = 3;
	private static final int LOG_LEVEL_WARN = 4;
	private static final int LOG_LEVEL_ERROR = 5;

	protected int currentLogLevel = LOG_LEVEL_INFO;

	/** Are {@code trace} messages currently enabled? */
	public boolean isTraceEnabled() {
		return isLevelEnabled(LOG_LEVEL_TRACE);
	}

	/**
	 * A simple implementation which logs messages of level TRACE according
	 * to the format outlined above.
	 */
	public void trace(String msg) {
		log(LOG_LEVEL_TRACE, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * TRACE according to the format outlined above.
	 */
	public void trace(String format, Object param1) {
		formatAndLog(LOG_LEVEL_TRACE, format, param1, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * TRACE according to the format outlined above.
	 */
	public void trace(String format, Object param1, Object param2) {
		formatAndLog(LOG_LEVEL_TRACE, format, param1, param2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * TRACE according to the format outlined above.
	 */
	public void trace(String format, Object... argArray) {
		formatAndLog(LOG_LEVEL_TRACE, format, argArray);
	}

	/** Log a message of level TRACE, including an exception. */
	public void trace(String msg, Throwable t) {
		log(LOG_LEVEL_TRACE, msg, t);
	}

	/** Are {@code debug} messages currently enabled? */
	public boolean isDebugEnabled() {
		return isLevelEnabled(LOG_LEVEL_DEBUG);
	}

	/**
	 * A simple implementation which logs messages of level DEBUG according
	 * to the format outlined above.
	 */
	public void debug(String msg) {
		log(LOG_LEVEL_DEBUG, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * DEBUG according to the format outlined above.
	 */
	public void debug(String format, Object param1) {
		formatAndLog(LOG_LEVEL_DEBUG, format, param1, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * DEBUG according to the format outlined above.
	 */
	public void debug(String format, Object param1, Object param2) {
		formatAndLog(LOG_LEVEL_DEBUG, format, param1, param2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * DEBUG according to the format outlined above.
	 */
	public void debug(String format, Object... argArray) {
		formatAndLog(LOG_LEVEL_DEBUG, format, argArray);
	}

	/** Log a message of level DEBUG, including an exception. */
	public void debug(String msg, Throwable t) {
		log(LOG_LEVEL_DEBUG, msg, t);
	}

	/** Are {@code info} messages currently enabled? */
	public boolean isInfoEnabled() {
		return isLevelEnabled(LOG_LEVEL_INFO);
	}

	/**
	 * A simple implementation which logs messages of level INFO according
	 * to the format outlined above.
	 */
	public void info(String msg) {
		log(LOG_LEVEL_INFO, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * INFO according to the format outlined above.
	 */
	public void info(String format, Object arg) {
		formatAndLog(LOG_LEVEL_INFO, format, arg, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * INFO according to the format outlined above.
	 */
	public void info(String format, Object arg1, Object arg2) {
		formatAndLog(LOG_LEVEL_INFO, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * INFO according to the format outlined above.
	 */
	public void info(String format, Object... argArray) {
		formatAndLog(LOG_LEVEL_INFO, format, argArray);
	}

	/** Log a message of level INFO, including an exception. */
	public void info(String msg, Throwable t) {
		log(LOG_LEVEL_INFO, msg, t);
	}

	/** Are {@code warn} messages currently enabled? */
	public boolean isWarnEnabled() {
		return isLevelEnabled(LOG_LEVEL_WARN);
	}

	/**
	 * A simple implementation which always logs messages of level WARN according
	 * to the format outlined above.
	 */
	public void warn(String msg) {
		log(LOG_LEVEL_WARN, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * WARN according to the format outlined above.
	 */
	public void warn(String format, Object arg) {
		formatAndLog(LOG_LEVEL_WARN, format, arg, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * WARN according to the format outlined above.
	 */
	public void warn(String format, Object arg1, Object arg2) {
		formatAndLog(LOG_LEVEL_WARN, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * WARN according to the format outlined above.
	 */
	public void warn(String format, Object... argArray) {
		formatAndLog(LOG_LEVEL_WARN, format, argArray);
	}

	/** Log a message of level WARN, including an exception. */
	public void warn(String msg, Throwable t) {
		log(LOG_LEVEL_WARN, msg, t);
	}

	/** Are {@code error} messages currently enabled? */
	public boolean isErrorEnabled() {
		return isLevelEnabled(LOG_LEVEL_ERROR);
	}

	/**
	 * A simple implementation which always logs messages of level ERROR according
	 * to the format outlined above.
	 */
	public void error(String msg) {
		log(LOG_LEVEL_ERROR, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	public void error(String format, Object arg) {
		formatAndLog(LOG_LEVEL_ERROR, format, arg, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	public void error(String format, Object arg1, Object arg2) {
		formatAndLog(LOG_LEVEL_ERROR, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	public void error(String format, Object... argArray) {
		formatAndLog(LOG_LEVEL_ERROR, format, argArray);
	}

	/** Log a message of level ERROR, including an exception. */
	public void error(String msg, Throwable t) {
		log(LOG_LEVEL_ERROR, msg, t);
	}

	protected boolean isLevelEnabled(int logLevel) {
		// log level are numerically ordered so can use simple numeric
		// comparison
		return (logLevel >= currentLogLevel);
	}

	private void formatAndLog(int level, String format, Object arg1,
							  Object arg2) {
		if (!isLevelEnabled(level)) {
			return;
		}
		FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
		log(level, tp.getMessage(), tp.getThrowable());
	}

	private void formatAndLog(int level, String format, Object... arguments) {
		if (!isLevelEnabled(level)) {
			return;
		}
		FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
		log(level, tp.getMessage(), tp.getThrowable());
	}

	abstract void log(int level, String message, Throwable t);
}
