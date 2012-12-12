package cz.cuni.mff.d3s.been.swrepository;

@SuppressWarnings("serial")
public class HttpServerException extends RuntimeException {

	public HttpServerException(String message) {
		super(message);
	}

	public HttpServerException(String message, Throwable cause) {
		super(message, cause);
	}

}
