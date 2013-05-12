package cz.cuni.mff.d3s.been.swrepoclient;

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
