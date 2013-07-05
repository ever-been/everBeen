package cz.cuni.mff.d3s.been.swrepository;

import cz.cuni.mff.d3s.been.cluster.ServiceException;

@SuppressWarnings("serial")
public class HttpServerException extends ServiceException {

	public HttpServerException(String message) {
		super(message);
	}

	public HttpServerException(String message, Throwable cause) {
		super(message, cause);
	}

}
