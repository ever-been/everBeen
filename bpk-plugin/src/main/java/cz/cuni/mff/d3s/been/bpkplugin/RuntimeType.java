package cz.cuni.mff.d3s.been.bpkplugin;

/**
 *
 * Types of supported run time "modes".
 *
 * The types should correspond to the types extended from BpkRuntime (as defined
 * in BpkConfiguration's XSD file).
 *
 * @author Martin Sixta
 */
public enum RuntimeType {
	/** Java based task */
	JAVA

	//TODO: add support for other types. how? (Also change CreateBeenPackageMojo.execute())
}
