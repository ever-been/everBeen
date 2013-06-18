package cz.cuni.mff.d3s.been.persistence;

public final class DAOException extends Exception {

	/** Version ID for serialization purposes */
	private static final long serialVersionUID = 2307462277189979767L;

	public DAOException() {
		super();
	}

	public DAOException(String message) {
		super(message);
	}

	public DAOException(String message, Throwable cause) {
		super(message, cause);
	}

}
