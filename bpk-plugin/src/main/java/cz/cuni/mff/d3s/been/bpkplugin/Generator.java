package cz.cuni.mff.d3s.been.bpkplugin;

/**
 * 
 * BPK Generator. See abstract implementation {@link Generator} and real
 * implementations {@link JavaGenerator} and {@link NativeGenerator}
 * 
 * @author Tadeas Palusga
 * 
 */
public interface Generator {

	/**
	 * Generates and BPK based on given configuration. Where the generated BPK
	 * file is saved is based on settings in given configuration.
	 * 
	 * @param configuration
	 *          configuration from which the BPK is generated
	 * @throws GeneratorException
	 *           when some exception occures while generating BPK.
	 * @throws ConfigurationException
	 *           when configuration is wrong (Illegal dependencies between
	 *           parameters, Missing parameters, etc.)
	 */
	void generate(Configuration configuration) throws GeneratorException, ConfigurationException;

}
