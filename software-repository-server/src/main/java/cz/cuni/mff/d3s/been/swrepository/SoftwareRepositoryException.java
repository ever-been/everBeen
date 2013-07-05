package cz.cuni.mff.d3s.been.swrepository;

/**
 * An exception indicating a problem with BEEN software repository.
 * 
 * @author darklight
 *
 */
public class SoftwareRepositoryException extends Exception {
	private final String message;

	SoftwareRepositoryException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
