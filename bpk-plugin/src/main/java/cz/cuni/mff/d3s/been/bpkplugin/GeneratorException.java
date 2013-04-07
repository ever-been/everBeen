package cz.cuni.mff.d3s.been.bpkplugin;

/**
 * Used when BPK cannot be generated from some reason.
 * 
 * @author Tadeas Palusga
 * 
 */
class GeneratorException extends Exception {

	/**
	 * SERIAL VERSION UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	public GeneratorException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public GeneratorException(Throwable cause) {
		super(cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public GeneratorException(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public GeneratorException(String message, Throwable cause) {
		super(message, cause);
	}

}
