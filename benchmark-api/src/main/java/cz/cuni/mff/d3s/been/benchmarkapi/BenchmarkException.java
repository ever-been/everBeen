package cz.cuni.mff.d3s.been.benchmarkapi;

import cz.cuni.mff.d3s.been.taskapi.TaskException;

/**
 * This exception indicates an error that occurred within the benchmark
 * generator.
 * 
 * @author Kuba Brecka
 */
public class BenchmarkException extends TaskException {

	/**
	 * Constructs a new empty exception.
	 */
	public BenchmarkException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *          the detail message
	 */
	public BenchmarkException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * 
	 * @param message
	 *          the detail message
	 * @param cause
	 *          cause of the exception
	 */
	public BenchmarkException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified {@link Throwable} as a cause.
	 * 
	 * @param cause
	 *          cause of the exception
	 */
	public BenchmarkException(Throwable cause) {
		super(cause);
	}

}
