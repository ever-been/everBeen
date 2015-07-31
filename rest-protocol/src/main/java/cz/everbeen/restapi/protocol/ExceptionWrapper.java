package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * A protocol object denoting an exception has occurred. Carries the exception stacktrace.
 *
 * @author darklight
 */
public class ExceptionWrapper implements ProtocolObject {

	@JsonProperty("trace")
	private final String[] trace;

	@JsonCreator
	public ExceptionWrapper(
		@JsonProperty("trace") String [] trace
	) {
		this.trace = trace;
	}

	/**
	 * Create a trace wrapper from an exception
	 * @param t Exception whose trace to wrap
	 * @return A new exception wrapper over error message trace
	 */
	public static ExceptionWrapper fromException(Throwable t) {
		final List<String> trace = new ArrayList<>();
		for (Throwable r = t; r != null; r = r.getCause()) trace.add(r.getMessage());
		final String [] traceArray = new String [trace.size()];
		return new ExceptionWrapper(trace.toArray(traceArray));
	}
}
