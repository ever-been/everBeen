package cz.cuni.mff.d3s.been.benchmarkapi;

/**
 * @author Kuba Brecka
 */
public class BenchmarkException extends Exception {

	/** {@inheritDoc} */
	public BenchmarkException() {
		super();
	}

	/** {@inheritDoc} */
	public BenchmarkException(String message) {
		super(message);
	}

	/** {@inheritDoc} */
	public BenchmarkException(String message, Throwable cause) {
		super(message, cause);
	}

	/** {@inheritDoc} */
	public BenchmarkException(Throwable cause) {
		super(cause);
	}

	/** {@inheritDoc} */
	protected BenchmarkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
