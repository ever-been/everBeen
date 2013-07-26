package cz.cuni.mff.d3s.been.taskapi;

import cz.cuni.mff.d3s.been.logging.LogLevel;
import static cz.cuni.mff.d3s.been.logging.LogLevel.*;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 * 
 * @author Kuba Brecka
 */
abstract class TaskLoggerBase extends MarkerIgnoringBase {

	/** effective log level */
	protected LogLevel currentLogLevel = INFO;

	/** Are {@code trace} messages currently enabled? */
	@Override
	public boolean isTraceEnabled() {
		return isLevelEnabled(TRACE);
	}

	/**
	 * A simple implementation which logs messages of level TRACE according to the
	 * format outlined above.
	 */
	@Override
	public void trace(String msg) {
		if (isTraceEnabled())
			log(TRACE.ordinal(), msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * TRACE according to the format outlined above.
	 */
	@Override
	public void trace(String format, Object param1) {
		formatAndLog(TRACE, format, param1, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * TRACE according to the format outlined above.
	 */
	@Override
	public void trace(String format, Object param1, Object param2) {
		formatAndLog(TRACE, format, param1, param2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * TRACE according to the format outlined above.
	 */
	@Override
	public void trace(String format, Object... argArray) {
		formatAndLog(TRACE, format, argArray);
	}

	/** Log a message of level TRACE, including an exception. */
	@Override
	public void trace(String msg, Throwable t) {
		if (isTraceEnabled())
			log(TRACE.ordinal(), msg, t);
	}

	/** Are {@code debug} messages currently enabled? */
	@Override
	public boolean isDebugEnabled() {
		return isLevelEnabled(DEBUG);
	}

	/**
	 * A simple implementation which logs messages of level DEBUG according to the
	 * format outlined above.
	 */
	@Override
	public void debug(String msg) {
		if (isDebugEnabled())
			log(DEBUG.ordinal(), msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * DEBUG according to the format outlined above.
	 */
	@Override
	public void debug(String format, Object param1) {
		formatAndLog(DEBUG, format, param1, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * DEBUG according to the format outlined above.
	 */
	@Override
	public void debug(String format, Object param1, Object param2) {
		formatAndLog(DEBUG, format, param1, param2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * DEBUG according to the format outlined above.
	 */
	@Override
	public void debug(String format, Object... argArray) {
		formatAndLog(DEBUG, format, argArray);
	}

	/** Log a message of level DEBUG, including an exception. */
	@Override
	public void debug(String msg, Throwable t) {
		if (isDebugEnabled())
			log(DEBUG.ordinal(), msg, t);
	}

	/** Are {@code info} messages currently enabled? */
	@Override
	public boolean isInfoEnabled() {
		return isLevelEnabled(INFO);
	}

	/**
	 * A simple implementation which logs messages of level INFO according to the
	 * format outlined above.
	 */
	@Override
	public void info(String msg) {
		if (isInfoEnabled())
			log(INFO.ordinal(), msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * INFO according to the format outlined above.
	 */
	@Override
	public void info(String format, Object arg) {
		formatAndLog(INFO, format, arg, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * INFO according to the format outlined above.
	 */
	@Override
	public void info(String format, Object arg1, Object arg2) {
		formatAndLog(INFO, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * INFO according to the format outlined above.
	 */
	@Override
	public void info(String format, Object... argArray) {
		formatAndLog(INFO, format, argArray);
	}

	/** Log a message of level INFO, including an exception. */
	@Override
	public void info(String msg, Throwable t) {
		if (isInfoEnabled())
			log(INFO.ordinal(), msg, t);
	}

	/** Are {@code warn} messages currently enabled? */
	@Override
	public boolean isWarnEnabled() {
		return isLevelEnabled(WARN);
	}

	/**
	 * A simple implementation which always logs messages of level WARN according
	 * to the format outlined above.
	 */
	@Override
	public void warn(String msg) {
		if (isWarnEnabled())
			log(WARN.ordinal(), msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * WARN according to the format outlined above.
	 */
	@Override
	public void warn(String format, Object arg) {
		formatAndLog(WARN, format, arg, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * WARN according to the format outlined above.
	 */
	@Override
	public void warn(String format, Object arg1, Object arg2) {
		formatAndLog(WARN, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * WARN according to the format outlined above.
	 */
	@Override
	public void warn(String format, Object... argArray) {
		formatAndLog(WARN, format, argArray);
	}

	/** Log a message of level WARN, including an exception. */
	@Override
	public void warn(String msg, Throwable t) {
		if (isWarnEnabled())
			log(WARN.ordinal(), msg, t);
	}

	/** Are {@code error} messages currently enabled? */
	@Override
	public boolean isErrorEnabled() {
		return isLevelEnabled(ERROR);
	}

	/**
	 * A simple implementation which always logs messages of level ERROR according
	 * to the format outlined above.
	 */
	@Override
	public void error(String msg) {
		if (isErrorEnabled())
			log(ERROR.ordinal(), msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	@Override
	public void error(String format, Object arg) {
		formatAndLog(ERROR, format, arg, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	@Override
	public void error(String format, Object arg1, Object arg2) {
		formatAndLog(ERROR, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	@Override
	public void error(String format, Object... argArray) {
		formatAndLog(ERROR, format, argArray);
	}

	/** Log a message of level ERROR, including an exception. */
	@Override
	public void error(String msg, Throwable t) {
		if (isErrorEnabled())
			log(ERROR.ordinal(), msg, t);
	}

	protected boolean isLevelEnabled(LogLevel logLevel) {
		// log level are numerically ordered so can use simple numeric
		// comparison
		return (logLevel.ordinal() >= currentLogLevel.ordinal());
	}

	private void formatAndLog(LogLevel level, String format, Object arg1, Object arg2) {
		if (!isLevelEnabled(level)) {
			return;
		}
		FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
		log(level.ordinal(), tp.getMessage(), tp.getThrowable());
	}

	private void formatAndLog(LogLevel level, String format, Object... arguments) {
		if (!isLevelEnabled(level)) {
			return;
		}
		FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
		log(level.ordinal(), tp.getMessage(), tp.getThrowable());
	}

	final void setLogLevel(LogLevel level) {
		this.currentLogLevel = level;
	}

	abstract void log(int level, String message, Throwable t);
}
