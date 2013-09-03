package cz.cuni.mff.d3s.been.core;

/**
 * Exit status codes.
 * 
 * @author Martin Sixta
 */
public enum StatusCode {

	/** Process terminated normally */
	EX_OK(0),

	/** General catch-all error */
	EX_UNKNOWN(1),

	/** Process was incorrectly invoked, i.e. with bad command line argumens. */
	EX_USAGE(67),

	/** A component of this process has failed to run. */
	EX_COMPONENT_FAILED(128),

	/** Something is wrong with this computer's network configuration */
	EX_NETWORK_ERROR(129);

	/** The exit code */
	private int code;

	/**
	 * Creates status code.
	 * 
	 * @param code
	 *          the code associated with the status.
	 */
	private StatusCode(int code) {
		this.code = code;
	}

	/**
	 * Returns the code associated with the status.
	 * 
	 * @return the code associated with the status
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Will exit the JVM with the code associated with the status.
	 */
	public void sysExit() {
		System.exit(code);
	}
}
