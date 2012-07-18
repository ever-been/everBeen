/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2006 Distributed Systems Research Group,
 *  Faculty of Mathematics and Physics, Charles University in Prague
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA  02111-1307  USA
 */
package cz.cuni.mff.been.benchmarkmanagerng.module.generator.xamplerswrep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorException;
import cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorPluggableModule;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor.DatasetType;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle.DataType;

/**
 * Generator module for Xampler Sortware repository.
 * 
 * Xampler SW repository analysis does not run any tasks,
 * it just creates omniorb_src, xampler_src, omniorb_bin, xampler_bin,
 * and planned_builds datasets. 
 *  Other analyses are 
 * allowed to store omniorb and xampler sources  and binaries 
 * and planner build log in these datasets.
 * (so that omniorb and xampler sources does not have to be
 * downloaded and built repeatedly)  
 * 
 *
 * @author Jan Tattermusch
 */
public class XamplerSWRepositoryGenerator extends GeneratorPluggableModule {
	
	
	/**
	 * name of dataset for storing OmniORB sources
	 */
	private static String OMNIORB_SRC_DATASET_NAME = "omniorb_src";
	
	/**
	 * name of dataset for storing OmniORB binaries
	 */
	private static String OMNIORB_BIN_DATASET_NAME = "omniorb_bin";
	
	/**
	 * name of dataset for storing Xampler sources
	 */
	private static String XAMPLER_SRC_DATASET_NAME = "xampler_src";
	
	/**
	 * name of dataset for storing Xampler binaries
	 */
	private static String XAMPLER_BIN_DATASET_NAME = "xampler_bin";
	
	/**
	 * name of dataset for planned builds
	 */
	private static String PLANNED_BUILDS_DATASET_NAME = "planned_builds";
	
	/** name of omniorb timestamp field  */
    private static final String OMNIORB_TIMESTAMP_FIELD_NAME = "omniorb_timestamp";
    
    /**
     * name of build number field
     */
    private static final String BUILD_NUMBER_FIELD_NAME = "build_number";
    
    /** name of xampler revision field */
    private static final String XAMPLER_REVISION_FIELD_NAME = "xampler_revision";

	/**
	 * name of timestamp field in omniorb source dataset
	 */
	private static final String SOURCE_TIMESTAMP_FIELD_NAME = "timestamp";
	
	/**
	 * name of revision field in xampler source dataset 
	 */
	private static final String SOURCE_REVISION_FIELD_NAME = "revision";
	
    
    /** dataset's field for storing file id */
    private static final String FILE_ID_FIELD_NAME = "fileid";
	
	/**
	 * Construct new instance of xampler generator
	 * @param manager pluggable module manager
	 */
	public XamplerSWRepositoryGenerator(PluggableModuleManager manager) {
		super(manager);
	}

	/**
	 * Lists datasets that xampler SW repository generator creates:
	 * <ul>
	 * <li>dataset for storing omniorb sources</li>
	 * <li>dataset for storing xampler sources</li>
	 * </ul>
	 * @return  list of datasets that should be created  
	 */
	@Override
	protected Map<String, DatasetDescriptor> doCreateDatasets() throws GeneratorException {
		Map<String, DatasetDescriptor> datasets = new HashMap<String, DatasetDescriptor> ();
		
		/* dataset for storing omniORB sources */
		DatasetDescriptor omniorbSrcDataset = new DatasetDescriptor();
		omniorbSrcDataset.put(SOURCE_TIMESTAMP_FIELD_NAME, DataType.STRING, true);
		omniorbSrcDataset.put(FILE_ID_FIELD_NAME, DataType.FILE, false);
		datasets.put(OMNIORB_SRC_DATASET_NAME, omniorbSrcDataset);
		
		/* dataset for storing omniORB binaries */
		DatasetDescriptor omniorbBinDataset = new DatasetDescriptor();
		omniorbBinDataset.put(OMNIORB_TIMESTAMP_FIELD_NAME, DataType.STRING, true);
		omniorbBinDataset.put(BUILD_NUMBER_FIELD_NAME, DataType.INT, true);
		omniorbBinDataset.put(FILE_ID_FIELD_NAME, DataType.FILE, false);
		datasets.put(OMNIORB_BIN_DATASET_NAME, omniorbBinDataset);
		
		/* dataset for storing Xampler sources */
		DatasetDescriptor xamplerSrcDataset = new DatasetDescriptor();
		xamplerSrcDataset.put(SOURCE_REVISION_FIELD_NAME, DataType.LONG, true);
		xamplerSrcDataset.put(FILE_ID_FIELD_NAME, DataType.FILE, false);
		datasets.put(XAMPLER_SRC_DATASET_NAME, xamplerSrcDataset);
		
		/* dataset for storing Xampler binaries */
		DatasetDescriptor xamplerBinDataset = new DatasetDescriptor();
		xamplerBinDataset.put(OMNIORB_TIMESTAMP_FIELD_NAME, DataType.STRING, true);
		xamplerBinDataset.put(XAMPLER_REVISION_FIELD_NAME, DataType.LONG, true);
		xamplerBinDataset.put(BUILD_NUMBER_FIELD_NAME, DataType.INT, true);
		xamplerBinDataset.put(FILE_ID_FIELD_NAME, DataType.FILE, false);
		datasets.put(XAMPLER_BIN_DATASET_NAME, xamplerBinDataset);
		
		/* dataset for storing planned builds */
		DatasetDescriptor plannedBuildsDataset = new DatasetDescriptor();
		plannedBuildsDataset.setDatasetType(DatasetType.TRANSACTION_ENABLED);
		plannedBuildsDataset.put(OMNIORB_TIMESTAMP_FIELD_NAME, DataType.STRING, true);
		plannedBuildsDataset.put(BUILD_NUMBER_FIELD_NAME, DataType.INT, true);
		datasets.put(PLANNED_BUILDS_DATASET_NAME, plannedBuildsDataset);
		
		return datasets;
	}

	/**
	 * Configuration validation method
	 * @return empty error list (this generator has no configuration)
	 */
	@Override
	protected Collection<String> doValidateConfiguration(Configuration configuration) {
		return new ArrayList<String>();
	}

	/**
	 * This method does not generate anything.
	 * 
	 * @return empty collection
	 */
	@Override
	protected Collection<TaskDescriptor> doGenerate() throws GeneratorException {
		
		return new ArrayList<TaskDescriptor>();
	}	
	
}
