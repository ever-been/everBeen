package cz.cuni.mff.been.pluggablemodule;


/**
 * Pluggable module manager mock used for testing.
 * @author Jan Tattermusch
 *
 */
public class MockPluggableModuleManager implements PluggableModuleManager {
	public ClassLoader getClassLoader() {
		return this.getClass().getClassLoader();
	}

	public PluggableModule getModule(PluggableModuleDescriptor moduleDescriptor) throws PluggableModuleException {
		return null;
	}

	public boolean isModuleLoaded(PluggableModuleDescriptor moduleDescriptor) {
		return false;
	}

	public PluggableModule loadModule(PluggableModuleDescriptor moduleDescriptor) throws PluggableModuleException {
		return null;
	}
}
