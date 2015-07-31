package cz.cuni.mff.d3s.been.results;

/**
 * Exception throw when an error occurs in the mapping of result values to {@link cz.cuni.mff.d3s.been.results.PrimitiveType}.
 *
 * @author darklight
 */
public class PrimitiveTypeException extends Exception {
	PrimitiveTypeException(String message) {
		super(message);
	}

	PrimitiveTypeException(String message, Throwable cause) {
		super(message, cause);
	}
}
