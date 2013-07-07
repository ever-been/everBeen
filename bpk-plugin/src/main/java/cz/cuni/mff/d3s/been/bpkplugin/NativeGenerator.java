package cz.cuni.mff.d3s.been.bpkplugin;

import static cz.cuni.mff.d3s.been.bpk.BpkNames.FILES_DIR;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.cuni.mff.d3s.been.util.FileToArchive;
import cz.cuni.mff.d3s.been.util.ItemToArchive;
import org.apache.maven.plugin.logging.Log;

import cz.cuni.mff.d3s.been.bpk.BpkDependencies;
import cz.cuni.mff.d3s.been.bpk.BpkRuntime;
import cz.cuni.mff.d3s.been.bpk.NativeRuntime;
import cz.cuni.mff.d3s.been.bpk.ObjectFactory;

/**
 * 
 * @author Tadeas Palusga
 * 
 */
class NativeGenerator extends GeneratorImpl {

	public NativeGenerator(Log log) {
		super(log);
	}

	@Override
	void validateRuntimeSpecific(Configuration config) throws ConfigurationException {
		StringBuilder result = new StringBuilder();

		if (config.binary == null) {
			result.append("parameter 'binary' must not be null \n");
		} else if (!config.binary.exists()) {
			result.append(String.format("file '%s' specified in parameter 'binary' does not exists \n", config.binary));
		}

		if (config.filesToArchive == null) {
			result.append("parameter 'filesToArchive' must not be null \n");
		}

		if (result.length() > 0) {
			throw new ConfigurationException(result.toString());
		}

	}

	@Override
	Collection<ItemToArchive> getItemsForArchivation(Configuration config) {
		List<ItemToArchive> itemsToArchive = new ArrayList<ItemToArchive>();

		// add task jar
		itemsToArchive.add(getFileToArchiveFromBinary(config));

		// add other files specified in configuration
		itemsToArchive.addAll(getOtherFilesToArchive(config));

		return itemsToArchive;
	}

	private ItemToArchive getFileToArchiveFromBinary(Configuration config) {
		String nameInBpk = FILES_DIR + File.separator + config.binary.getName();
		return new FileToArchive(nameInBpk, config.binary);
	}

	private Collection<? extends ItemToArchive> getOtherFilesToArchive(
			Configuration config) {
		List<ItemToArchive> itemsToArchive = new ArrayList<ItemToArchive>();
		for (FileItem fileItem : config.filesToArchive) {
			itemsToArchive.addAll(fileItem.getFilesToArchive());
		}
		return itemsToArchive;
	}

	@Override
	BpkRuntime createRuntime(Configuration config) {

		ObjectFactory objectFactory = new ObjectFactory();
		NativeRuntime runtime = objectFactory.createNativeRuntime();

		runtime.setBinary(config.binary.getName());

		BpkDependencies bpkDependencies = objectFactory.createBpkDependencies();

		bpkDependencies.getDependency().addAll(config.bpkDependencies);

		runtime.setBpkDependencies(bpkDependencies);

		return runtime;
	}

}
