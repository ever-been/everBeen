package cz.cuni.mff.d3s.been.bpkplugin;

/**
 * Represents pluggable module configuration in bpk-plugin
 *
 * @author Martin Sixta
 */


import java.util.List;

public final class BpkModuleConfig {


	/**
	 * List of exported interfaces.
	 *
	 * @parameter
	 */
	public List<String> interfaces;


	/**
	 * List of module dependencies.
	 *
	 * @parameter
	 */
	public List<BpkModuleDependency> dependencies;


	/**
	 * Contentet of the file will be in module-config.xml
	 *
	 * @parameter
	 */
	public String config;





}

