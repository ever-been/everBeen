package cz.cuni.mff.d3s.been.swrepoclient;

/**
 * Exception that can arise when the <em>Software Repository Client</em> fails to connect or retrieve requested package
 */
public class SwRepositoryClientException extends Exception {

	SwRepositoryClientException() {
		super();
	}

	SwRepositoryClientException(String message) {
		super(message);
	}

	SwRepositoryClientException(Throwable t) {
		super(t);
	}

	SwRepositoryClientException(String message, Throwable t) {
		super(message, t);
	}

}
