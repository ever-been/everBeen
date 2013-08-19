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

	public GeneratorException() {
		super();
	}

	public GeneratorException(Throwable cause) {
		super(cause);
	}

	public GeneratorException(String message) {
		super(message);
	}

	public GeneratorException(String message, Throwable cause) {
		super(message, cause);
	}

}
