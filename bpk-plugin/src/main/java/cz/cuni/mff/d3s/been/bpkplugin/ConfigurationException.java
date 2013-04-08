package cz.cuni.mff.d3s.been.bpkplugin;

/**
 * Used when provided configuration is invalid and validity cannot be
 * successfully checked by Mojo Maven Plugin annotations.
 * 
 * @author Tadeas Palusga
 * 
 */
class ConfigurationException extends Exception {

	/**
	 * SERIAL VERSION UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	public ConfigurationException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public ConfigurationException(Throwable cause) {
		super(cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public ConfigurationException(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

}
