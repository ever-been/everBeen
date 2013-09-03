package cz.cuni.mff.d3s.been.persistence;

/**
 * General persistence layer exception
 */
public class DAOException extends Exception {

	/** Version ID for serialization purposes */
	private static final long serialVersionUID = 2307462277189979767L;

	/**
	 * Create a DAO exception
	 */
	public DAOException() {
		super();
	}

	/**
	 * Create a DAO exception with an error message
	 *
	 * @param message Error message
	 */
	public DAOException(String message) {
		super(message);
	}

	/**
	 * Create a DAO exception with an error message and a cause
	 *
	 * @param message Error message
	 * @param cause Cause for this exception
	 */
	public DAOException(String message, Throwable cause) {
		super(message, cause);
	}

}
