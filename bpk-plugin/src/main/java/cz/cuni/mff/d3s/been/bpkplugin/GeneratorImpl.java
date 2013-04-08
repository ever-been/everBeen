package cz.cuni.mff.d3s.been.bpkplugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import cz.cuni.mff.d3s.been.bpk.BpkConfigUtils;
import cz.cuni.mff.d3s.been.bpk.BpkConfiguration;
import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.bpk.BpkRuntime;
import cz.cuni.mff.d3s.been.bpk.JavaRuntime;
import cz.cuni.mff.d3s.been.bpk.MetaInf;
import cz.cuni.mff.d3s.been.bpk.NativeRuntime;
import cz.cuni.mff.d3s.been.bpk.ObjectFactory;
import cz.cuni.mff.d3s.been.bpk.PackageNames;

/**
 * 
 * Base BPK generator. Extend to implement your own generator.
 * 
 * @author Tadeas Palusga
 * 
 */

public abstract class GeneratorImpl implements Generator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate(Configuration configuration) throws GeneratorException, ConfigurationException {
		validate(configuration);
		exportBpk(configuration);
	}

	/**
	 * validates configuration
	 * 
	 * @param configuration
	 *          to be validated
	 * @throws ConfigurationException
	 *           if configuration is not valid (invalid parameter references,
	 *           invalid parameter values, missing parameters)
	 */
	private void validate(Configuration configuration) throws ConfigurationException {
		validateRuntimeSpecific(configuration);
	}

	/**
	 * This is the main executing method of the generator. Generates
	 * {@link BpkConfiguration} from given {@link Configuration}, collects all
	 * items ({@link ItemToArchive}) which should be added to result BPK and saves
	 * it all to the file. Selection of file where the result BPK will be saved is
	 * based on informations in given {@link Configuration}
	 * 
	 * @param configuration
	 *          configuration from which the {@link BpkConfiguration} and list of
	 *          {@link ItemToArchive} is generated
	 * @throws GeneratorException
	 *           when some exception occures while generating BPK
	 */
	private void exportBpk(Configuration configuration) throws GeneratorException {
		BpkConfiguration bpkCfg = generateBpkConfiguration(configuration);

		Collection<ItemToArchive> items = getItemsForArchivation(configuration);
		// we have to add generated config.xml file
		items.add(new StringToArchive(PackageNames.CONFIG_FILE, toXml(bpkCfg)));

		try {
			save(items, configuration);
		} catch (IOException e) {
			throw new GeneratorException("Exception occured while saving generated BPK to file", e);
		}
	}

	/**
	 * From given {@link Configuration} generates {@link BpkConfiguration} with
	 * correctly generated {@link MetaInf} and {@link Runtime}.
	 * 
	 * @param cfg
	 *          from which the {@link BpkConfiguration} will be generated
	 * @return generated {@link BpkConfiguration}
	 */
	private BpkConfiguration generateBpkConfiguration(Configuration cfg) {
		BpkConfiguration bpkConfiguration = new ObjectFactory().createBpkConfiguration();
		bpkConfiguration.setMetaInf(createMetaInf(cfg));
		bpkConfiguration.setRuntime(createRuntime(cfg));
		return bpkConfiguration;
	}

	/**
	 * Generates XMl representation of given {@link BpkConfiguration}.
	 * 
	 * @param bpkConfiguration
	 *          from which the XMl will be generated
	 * @return generated XML string
	 * @throws GeneratorException
	 *           when XML string cannot be properly generated from given
	 *           {@link BpkConfiguration}
	 */
	private String toXml(BpkConfiguration bpkConfiguration) throws GeneratorException {
		try {
			return BpkConfigUtils.toXml(bpkConfiguration);
		} catch (BpkConfigurationException e) {
			throw new GeneratorException(e);
		}
	}

	/**
	 * Saves items to BPK file. Where the file will be saved
	 * 
	 * @param items
	 *          to be saved in result BPK
	 * @param cfg
	 *          from which the name of the result BPK file will be generated
	 * @throws IOException
	 *           when generated file cannot be saved
	 */
	private void save(Collection<ItemToArchive> items, Configuration cfg) throws IOException {
		File bpkFile = createEmptyBpkFile(cfg);
		ZipUtil.createZip(items, bpkFile);
	}

	/**
	 * Prepares new file to which the result BPK will be saved.
	 * 
	 * @param cfg
	 *          configuration from which the name of the file will be generated
	 * @return prepared file
	 */
	private File createEmptyBpkFile(Configuration cfg) {
		if (!cfg.buildDirectory.exists()) {
			cfg.buildDirectory.mkdirs();
		}
		return new File(cfg.buildDirectory, cfg.finalName + ".bpk");
	}

	/**
	 * Creates {@link MetaInf} about generated BPK (contains groupId, bpkId and
	 * version);
	 * 
	 * @param config
	 * @return generated {@link MetaInf}
	 */
	private MetaInf createMetaInf(Configuration config) {
		MetaInf metaInf = new ObjectFactory().createMetaInf();
		metaInf.setGroupId(config.groupId);
		metaInf.setBpkId(config.bpkId);
		metaInf.setVersion(config.version);
		return metaInf;
	}

	/**
	 * Validates given configuration. Checks for invalid parameter dependencies,
	 * missing parameters etc. This validation should be only runtime scope (for
	 * example if runtime type is of type {@link RuntimeType#JAVA}, validates only
	 * java related parameters.)
	 * 
	 * @param config
	 *          configuration which is being validate
	 * @throws ConfigurationException
	 *           when configuration is not valid
	 */
	abstract void validateRuntimeSpecific(Configuration config) throws ConfigurationException;

	/**
	 * Collects set of {@link ItemToArchive} which should be added to result BPK
	 * file. {@link BpkConfiguration} config.xml file should not be present in
	 * returned set of items.
	 * 
	 * @param cfg
	 * @return collected set of items
	 */
	abstract Collection<ItemToArchive> getItemsForArchivation(Configuration cfg);

	/**
	 * Generates {@link BpkRuntime} from given configuration. Type of runtime is
	 * dependent on type of BPK. See {@link RuntimeType}. Possible return types
	 * are for example {@link JavaRuntime} or {@link NativeRuntime}
	 * 
	 * @param cfg
	 * @return generated runtime
	 */
	abstract BpkRuntime createRuntime(Configuration cfg);

}
