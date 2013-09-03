package cz.cuni.mff.d3s.been.logging;

import java.util.ServiceLoader;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Logback appender to BEEN shared memory
 * 
 * @author darklight
 */
public class BeenAppender extends AppenderBase<ILoggingEvent> {

	private final LogLevelConverter levelConverter;
	private final ServiceLogHandler logHandler;

	/**
	 * Creates new BeenAppender.
	 */
	public BeenAppender() {
		logHandler = ServiceLoader.load(ServiceLogHandler.class).iterator().next();
		levelConverter = new LogLevelConverter();
	}

	@Override
	protected void append(ILoggingEvent eventObject) {
		if (logHandler == null) {
			System.err.println(String.format(
					"Could not log following message, because no '%s' instance was found:\n%s",
					ServiceLogHandler.class.getSimpleName(),
					eventObject.getMessage()));
		}

		final String loggerName = eventObject.getLoggerName();

		// workaround for issue #188
		if (loggerName != null && loggerName.startsWith("com.hazelcast")) {
			return;
		}

		final LogMessage message = new LogMessage();
		message.setLevel(levelConverter.getBeenLogLevel(eventObject.getLevel()));
		message.setMessage(eventObject.getFormattedMessage());
		message.setName(eventObject.getLoggerName());
		message.setThreadName(eventObject.getThreadName());
		final StringBuilder traceBuilder = new StringBuilder();
		for (StackTraceElement traceElm : eventObject.getCallerData()) {
			traceBuilder.append(traceElm.toString());
			traceBuilder.append(Character.LINE_SEPARATOR);
		}
		message.setErrorTrace(traceBuilder.toString());
		try {
			logHandler.log(message);
		} catch (Exception e) {
			System.err.println(String.format(
					"Could not log following message because of an exception:\n%s",
					message.toString()));
			e.printStackTrace();
		}
	}
}
