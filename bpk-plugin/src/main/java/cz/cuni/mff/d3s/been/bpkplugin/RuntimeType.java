package cz.cuni.mff.d3s.been.bpkplugin;

/**
 * 
 * Types of supported run time "modes".
 * 
 * The types should correspond to the types extended from BpkRuntime (as defined
 * in BpkConfiguration's XSD file).
 * 
 * @author Tadeas Palusga
 * @author Martin Sixta
 */
enum RuntimeType {

	/** Java based task */
	JAVA,
	/**
	 * Native task (executable shell script, bat script, executable binary file
	 * etc...)
	 */
	NATIVE;

	/**
	 * Returns RuntimeType enum value corresponding to given String value. Method
	 * is not case sensitive.
	 * 
	 * This method never throws {@link IllegalArgumentException} (
	 * {@link RuntimeType#valueOf(String)} does)
	 * 
	 * @param value
	 *          string representation of enum value.
	 * @return corresponding enum value for given string or <b>null</b> if
	 *         corresponding enum value was not found.
	 */
	public static RuntimeType determine(String value) {
		try {
			return valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			// we do not care about these exceptions
		}
		return null;
	}

}
