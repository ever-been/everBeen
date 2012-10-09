package cz.cuni.mff.d3s.been.bpkplugin;

/**
 * BPK module dependency specification.
 *
 */
public final class BpkModuleDependency {

	/**
	 *
	 * Name of the module.
	 *
	 * @param
	 * @required
	 */
	private String name;

	/**
	 *
	 * Version of the module.
	 *
	 * @param
	 * @required
	 */
	private String version;

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}
}