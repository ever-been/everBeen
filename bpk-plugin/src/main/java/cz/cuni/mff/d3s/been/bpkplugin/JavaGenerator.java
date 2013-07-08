package cz.cuni.mff.d3s.been.bpkplugin;

import static cz.cuni.mff.d3s.been.bpk.BpkNames.FILES_DIR;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.cuni.mff.d3s.been.util.FileToArchive;
import cz.cuni.mff.d3s.been.util.ItemToArchive;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;

import cz.cuni.mff.d3s.been.bpk.*;

/**
 * 
 * @author Tadeas Palusga
 * 
 */
class JavaGenerator extends GeneratorImpl {

	public JavaGenerator(Log log) {
        super(log);
	}

	@Override
	void validateRuntimeSpecific(Configuration config) throws ConfigurationException {
		StringBuilder result = new StringBuilder();

		if (config.packageJarFile == null) {
			result.append("parameter 'packageJarFile' must not be null \n");
		} else if (!config.packageJarFile.exists()) {
			result.append(String.format("file '%s' specified in parameter 'packageJarFile' does not exists \n", config.packageJarFile));
		}

		try {
			config.mainClass = (config.mainClass == null
					? MainClassExtractor.getMainClass(config.packageJarFile.toPath())
					: config.mainClass);
		} catch (IOException e) {
			throw new ConfigurationException(String.format("Invalid configuration: ", e.getMessage()), e);
		}

		if (config.mainClass == null) {
			result.append("parameter 'mainClass' must not be null \n");
		}

		if (config.filesToArchive == null) {
			result.append("parameter 'filesToArchive' must not be null \n");
		}

		if (config.artifacts == null) {
			result.append("parameter 'artifacts' must not be null \n");
		}

		if (config.bpkDependencies == null) {
			result.append("parameter 'bpkDependencies' must not be null \n");
		}

		if (result.length() > 0) {
			throw new ConfigurationException(result.toString());
		}

	}

	@Override
	Collection<ItemToArchive> getItemsForArchivation(Configuration config) {
		List<ItemToArchive> itemsToArchive = new ArrayList<ItemToArchive>();

		// add task jar
		itemsToArchive.add(getFileToArchiveFromPackageJar(config));

		// add task dependencies
		itemsToArchive.addAll(getLibsToArchive(config));

		// add other files specified in configuration
		itemsToArchive.addAll(getOtherFilesToArchive(config));

		return itemsToArchive;
	}

	private Collection<? extends ItemToArchive> getOtherFilesToArchive(
			Configuration config) {
		List<ItemToArchive> itemsToArchive = new ArrayList<ItemToArchive>();
		for (FileItem fileItem : config.filesToArchive) {
			itemsToArchive.addAll(fileItem.getFilesToArchive());
		}
		return itemsToArchive;
	}

	FileToArchive getFileToArchiveFromPackageJar(Configuration config) {
		String nameInBpk = FILES_DIR + File.separator + config.packageJarFile.getName();
		return new FileToArchive(nameInBpk, config.packageJarFile);
	}

	/**
	 * Get maven dependency artifacts.
	 * 
	 * @return A list of {@link FileToArchive} for the project's maven
	 *         dependencies.
	 */
	Collection<FileToArchive> getLibsToArchive(Configuration config) {
		List<FileToArchive> libs = new ArrayList<FileToArchive>();

		for (Artifact artifact : config.artifacts) {
			File artifactFile = artifact.getFile();
			File destinationFile = new File(BpkNames.LIB_DIR, artifactFile.getName());
			libs.add(new FileToArchive(destinationFile.getPath(), artifactFile));
		}

		return libs;
	}

	@Override
	JavaRuntime createRuntime(Configuration config) {
		JavaRuntime runtime = new ObjectFactory().createJavaRuntime();

		runtime.setJarFile(config.packageJarFile.getName());
		runtime.setMainClass(config.mainClass);
		runtime.setBpkArtifacts(createBpkArtifacts(config));

		return runtime;
	}

	private BpkArtifacts createBpkArtifacts(Configuration config) {
		ObjectFactory objectFactory = new ObjectFactory();
		BpkArtifacts bpkArtifacts = objectFactory.createBpkArtifacts();

		for (Artifact artifact : config.artifacts) {
			ArtifactIdentifier bpkArtifact = objectFactory.createArtifactIdentifier();

			bpkArtifact.setGroupId(artifact.getGroupId());
			bpkArtifact.setArtifactId(artifact.getArtifactId());
			bpkArtifact.setVersion(artifact.getVersion());

			bpkArtifacts.getArtifact().add(bpkArtifact);
		}

		return bpkArtifacts;
	}

}
