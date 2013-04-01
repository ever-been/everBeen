package cz.cuni.mff.d3s.been.resultsrepository;

import cz.cuni.mff.d3s.been.cluster.ServiceException;

/**
 * An exception related to the Results Repository service.
 * 
 * @author darklight
 * 
 */
@SuppressWarnings("serial")
public class ResultsRepositoryException extends ServiceException {

	public ResultsRepositoryException() {}

	public ResultsRepositoryException(String message) {
		super(message);
	}

	public ResultsRepositoryException(Throwable cause) {
		super(cause);
	}

	public ResultsRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResultsRepositoryException(
			String message,
			Throwable cause,
			boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
