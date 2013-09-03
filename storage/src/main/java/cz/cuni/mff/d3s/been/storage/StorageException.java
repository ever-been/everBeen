package cz.cuni.mff.d3s.been.storage;

import cz.cuni.mff.d3s.been.cluster.ServiceException;

/**
 * An exception saying that the initialization of the Results Repository storage
 * has failed.
 * 
 * @author darklight
 */
public class StorageException extends ServiceException {

	/** Version ID (serialization) */
	private static final long serialVersionUID = 7753981887709379320L;

	public StorageException() {}

	public StorageException(String message) {
		super(message);
	}

	public StorageException(Throwable cause) {
		super(cause);
	}

	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}

}
