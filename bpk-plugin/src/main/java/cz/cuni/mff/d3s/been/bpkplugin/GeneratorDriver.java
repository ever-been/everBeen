package cz.cuni.mff.d3s.been.bpkplugin;

import org.apache.maven.plugin.logging.Log;

/**
 * 
 * @author Tadeas Palusga
 * 
 */
class GeneratorDriver {

	private Log log;

	public GeneratorDriver(Log log) {
		this.log = log;
	}

	public void generate(String runtimeTypeName, Configuration configuration) throws ConfigurationException, GeneratorException {
		selectCorrectGenerator(runtimeTypeName).generate(configuration);
	}

	GeneratorImpl selectCorrectGenerator(String runtimeTypeName) throws ConfigurationException {
		RuntimeType runtimeType = RuntimeType.determine(runtimeTypeName);
		if (runtimeType == null) {
			throw new ConfigurationException(String.format("Runtime type '%s' is not defined", runtimeTypeName));
		}
		switch (runtimeType) {
			case JAVA:
				return new JavaGenerator(log);
			case NATIVE:
				return new NativeGenerator(log);
			default:
				// should not happen but just for sure
				throw new IllegalArgumentException(String.format("BPK generator for runtime type '%s' is not implemented", runtimeTypeName));
		}
	}

}
