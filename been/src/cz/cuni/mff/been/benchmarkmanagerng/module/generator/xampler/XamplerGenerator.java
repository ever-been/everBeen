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
package cz.cuni.mff.been.benchmarkmanagerng.module.generator.xampler;

import java.io.File;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;

import cz.cuni.mff.been.benchmarkmanagerng.Analysis;
import cz.cuni.mff.been.benchmarkmanagerng.AnalysisException;
import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.benchmarkmanagerng.common.planner.BuildActivity;
import cz.cuni.mff.been.benchmarkmanagerng.common.planner.BuildKey;
import cz.cuni.mff.been.benchmarkmanagerng.common.planner.BuildProps;
import cz.cuni.mff.been.benchmarkmanagerng.common.planner.DBRPlanner;
import cz.cuni.mff.been.benchmarkmanagerng.common.planner.DownloadActivity;
import cz.cuni.mff.been.benchmarkmanagerng.common.planner.RunActivity;
import cz.cuni.mff.been.benchmarkmanagerng.common.planner.RunKey;
import cz.cuni.mff.been.benchmarkmanagerng.common.planner.RunProps;
import cz.cuni.mff.been.benchmarkmanagerng.common.planner.SimpleDownloadBuildRunPlanner;
import cz.cuni.mff.been.benchmarkmanagerng.common.planner.SourceKey;
import cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorException;
import cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorPluggableModule;
import cz.cuni.mff.been.benchmarkmanagerng.module.generator.xampler.versionprovider.DefaultOmniorbCVSVersionProvider;
import cz.cuni.mff.been.common.Debug;
import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.common.rsl.Condition;
import cz.cuni.mff.been.common.rsl.ParseException;
import cz.cuni.mff.been.common.rsl.ParserWrapper;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.jaxb.td.TaskExclusivity;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.RRDataInterface;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryService;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor.DatasetType;
import cz.cuni.mff.been.resultsrepositoryng.condition.Restrictions;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle.DataType;
import cz.cuni.mff.been.resultsrepositoryng.transaction.RRTransaction;
import cz.cuni.mff.been.task.Service;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.task.xampler.execute.XamplerExecute;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper;

/**
 * Generator module for Xampler.
 *
 * @author Jan Tattermusch
 */
public class XamplerGenerator extends GeneratorPluggableModule {
	
	/**
	 * name of dataset for planned builds
	 */
	private static String PLANNED_BUILDS_DATASET_NAME = "planned_builds";
	
	/**
	 * name of dataset for planned runds
	 */
	private static String PLANNED_RUNS_DATASET_NAME = "planned_runs";	
	
	
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
	 * name of dataset for storing results
	 */
	private static String XAMPLER_RESULTS_DATASET_NAME = "xampler_results";

	/**
	 * name of timestamp field in omniorb source dataset
	 */
	private static final String SOURCE_TIMESTAMP_FIELD_NAME = "timestamp";
	
	/**
	 * name of revision field in xampler source dataset 
	 */
	private static final String SOURCE_REVISION_FIELD_NAME = "revision";
	
	/** name of omniorb timestamp field  */
    private static final String OMNIORB_TIMESTAMP_FIELD_NAME = "omniorb_timestamp";
    
    /** name of xampler revision field */
    private static final String XAMPLER_REVISION_FIELD_NAME = "xampler_revision";
    
    /**
     * name of build number field
     */
    private static final String BUILD_NUMBER_FIELD_NAME = "build_number";
    
    /**
     * name of run number field
     */
    private static final String RUN_NUMBER_FIELD_NAME = "run_number";
    
    /**
     * name of suite name field
     */
    private static final String SUITE_FIELD_NAME = "suite_name";
    
    /** dataset's field for storing file id */
    private static final String FILE_ID_FIELD_NAME = "fileid";
    
    /** directory to which omniorb is deployed by xampler-deploy */
    private static final String OMNIORB_DIR = "omniorb";
    
    /** directory to which xampler is deployed by xampler-deploy */
    private static final String XAMPLER_DIR = "xampler";
    
	/**
	 * reference to RR data interface
	 */
    private RRDataInterface resultsRepositoryData; 
    
    /**
     * simulate flag
     */
	private boolean simulate = false;

	
	/**
	 * xampler configuration field
	 */
	private Float runBuildRatio;
	
	/**
	 * xampler configuration field
	 */
	private Integer maxNumberOfRuns;

	/**
	 * xampler configuration field
	 */
	private Integer maxNumberOfBuilds;

	/**
	 * xampler configuration field
	 */
	private Integer workloadUnits;
	
	/**
	 * xampler configuration field
	 */
	private Integer downloadCost;
	
	/**
	 * xampler configuration field
	 */
	private Integer buildCost;
	
	/**
	 * xampler configuration field
	 */
	private Integer runCost;
	
	/**
	 * xampler client params
	 */
	private String xamplerClientParams;
	
	/**
	 * xampler server params
	 */
	private String xamplerServerParams;
	
	/**
	 * xampler subsuite regex
	 */
	private Pattern subsuiteRegex;
	
	/**
	 * xampler configuration field
	 */
	private String omniorbCvsBranch;

	/**
	 * xampler configuration field
	 */
	private String omniorbCvsModule;

	/**
	 * xampler configuration field
	 */
	private String omniorbCvsPassword;

	/**
	 * xampler configuration field
	 */
	private String omniorbCvsRepository;

	/**
	 * xampler configuration field
	 */
	private String xamplerSvnUrl;

	/**
	 * xampler configuration field
	 */
	private Long xamplerSvnRevision;

	/**
	 * xampler configuration field
	 */
	private String xamplerSuiteSubdir;

	/**
	 * xampler configuration field
	 */
	private Condition downloadRsl;

	/**
	 * xampler configuration field
	 */
	private Condition clientRsl;

	/**
	 * xampler configuration field
	 */
	private Condition buildRsl;

	/**
	 * xampler configuration field
	 */
	private Condition serverRsl;
	
	/**
	 * xampler configuration field
	 */
	private Date versionsFromDate;
	
	/**
	 * xampler configuration field
	 */
	private Set<SourceKey> versionBlacklist;
	
	/**
	 * xampler configuration field
	 */
	private Set<String> suites;

	/**
	 * xampler configuration field
	 */
	private boolean omniorbApplyPatch;

	/**
	 * xampler configuration field
	 */
	private boolean useSourcesRepositoryAnalysis;
	
	/**
	 * xampler configuration field
	 */
	private boolean useBinaryRepositoryAnalysis;

	/**
	 * xampler configuration field
	 */
	private String sourcesRepositoryAnalysisSafeName;
	
	/**
	 * xampler configuration field
	 */
	private String binaryRepositoryAnalysisSafeName;
    
	/**
	 * Construct new instance of xampler generator
	 * @param manager pluggable module manager
	 */
	public XamplerGenerator(PluggableModuleManager manager) {
		super(manager);
	}

	/**
	 * Lists datasets that xampler generator works with:
	 * <ul>
	 * <li>dataset for storing planned runs</li>
	 * <li>dataset for storing planned builds</li>
	 * <li>dataset for storing omniorb sources</li>
	 * <li>dataset for storing omniorb binaries</li>
	 * <li>dataset for storing xampler sources</li>
	 * <li>dataset for storing xampler binaries</li>
	 * <li>dataset for storing results</li>
	 * </ul>
	 * @return  list of datasets that should be created  
	 */
	@Override
	protected Map<String, DatasetDescriptor> doCreateDatasets() throws GeneratorException {
		Map<String, DatasetDescriptor> datasets = new HashMap<String, DatasetDescriptor> ();
		
		/* dataset for storing planned builds */
		DatasetDescriptor plannedBuildsDataset = new DatasetDescriptor();
		plannedBuildsDataset.setDatasetType(DatasetType.TRANSACTION_ENABLED);
		plannedBuildsDataset.put(OMNIORB_TIMESTAMP_FIELD_NAME, DataType.STRING, true);
		plannedBuildsDataset.put(BUILD_NUMBER_FIELD_NAME, DataType.INT, true);
		datasets.put(PLANNED_BUILDS_DATASET_NAME, plannedBuildsDataset);
		
		/* dataset for storing planned runs */
		DatasetDescriptor plannedRunsDataset = new DatasetDescriptor();
		plannedRunsDataset.setDatasetType(DatasetType.TRANSACTION_ENABLED);
		plannedRunsDataset.put(OMNIORB_TIMESTAMP_FIELD_NAME, DataType.STRING, true);
		plannedRunsDataset.put(BUILD_NUMBER_FIELD_NAME, DataType.INT, true);
		plannedRunsDataset.put(RUN_NUMBER_FIELD_NAME, DataType.INT, true);
		plannedRunsDataset.put(SUITE_FIELD_NAME, DataType.STRING, true);
		datasets.put(PLANNED_RUNS_DATASET_NAME, plannedRunsDataset);
		
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
		
		
		/* dataset for storing results */
		DatasetDescriptor resultsDataset = new DatasetDescriptor();
		resultsDataset.put(OMNIORB_TIMESTAMP_FIELD_NAME, DataType.STRING, true);
		resultsDataset.put(XAMPLER_REVISION_FIELD_NAME, DataType.LONG, true);
		resultsDataset.put(BUILD_NUMBER_FIELD_NAME, DataType.INT, true);
		resultsDataset.put(RUN_NUMBER_FIELD_NAME, DataType.INT, true);
		resultsDataset.put(SUITE_FIELD_NAME, DataType.STRING, true);
		resultsDataset.put(FILE_ID_FIELD_NAME, DataType.FILE, false);
		datasets.put(XAMPLER_RESULTS_DATASET_NAME, resultsDataset);
		return datasets;
	}

	/**
	 * Configuration validation method.
	 * @return list of configuration validation errors
	 */
	@Override
	protected Collection<String> doValidateConfiguration(Configuration configuration) {
		return readConfiguration(configuration);
	}

	/**
	 * Generates task for one generator run.
	 * 
	 * Benchmark manager insures there will be only one running generator from 
	 * analysis running at a time.
	 * 
	 * @return list of task to be submitted to taskmanager
	 */
	@Override
	protected Collection<TaskDescriptor> doGenerate() throws GeneratorException {
		
		try {
			initResultsRepositoryReference();
		} catch (RemoteException e) {
			throw new GeneratorException("Error initializing RR reference.");
		}
		
		if (readConfiguration(getConfiguration()) != null) {
			throw new GeneratorException("Could not read configuration, configuration is not valid"); 
		}
		
		List<TaskDescriptor> tasks = new ArrayList<TaskDescriptor>();
		
		TaskDescriptor downloadXampler = null;
		
		if (!isXamplerAvailable(xamplerSvnRevision)) {
			downloadXampler = downloadXampler(xamplerSvnRevision, downloadRsl);
			tasks.add(downloadXampler);
		}
		
		DBRPlanner planner = createPlanner();
		
		
		/* we need to lock only build log, run log is not shared */
		RRTransaction buildLogTransaction = getBuildLogTransaction(); 
		
		planner.plan( getCvsVersions(), getAvailableSources(), getBuildLog(buildLogTransaction), getAvailableXamplerBinaries() ,getRunLog() );
		
		storePlanToRR(planner.getDownloadActivities(), planner.getBuildActivities(), planner.getRunActivities(), buildLogTransaction);
		
		
		
		for (DownloadActivity activity : planner.getDownloadActivities()) {
			List<TaskDescriptor> activityTasks = getDownloadActivityTasks(activity);  
			/* add downloadXampler dependency, if needed */
			if (downloadXampler != null) {
				for (TaskDescriptor td : activityTasks) {
					success(td, downloadXampler.getTaskId());
				}
			}
			tasks.addAll( activityTasks);
		}
		
		for (BuildActivity activity : planner.getBuildActivities()) {
			List<TaskDescriptor> activityTasks = getBuildActivityTasks(activity, null);
			/* add downloadXampler dependency, if needed */
			if (downloadXampler != null) {
				for (TaskDescriptor td : activityTasks) {
					success(td, downloadXampler.getTaskId());
				}
			}
			tasks.addAll( activityTasks);
		}
		
		for (RunActivity activity : planner.getRunActivities()) {
			List<TaskDescriptor> activityTasks = getRunActivityTasks(activity, null);
			
			tasks.addAll( activityTasks);
		}
			
		return tasks;
	}	
	
	private RRTransaction getBuildLogTransaction() throws GeneratorException {
		try {
			return resultsRepositoryData.getTransaction( getBinaryAnalysisName(), PLANNED_BUILDS_DATASET_NAME);
		} catch (ResultsRepositoryException e) {
			e.printStackTrace();
			throw new GeneratorException("Error obtaining transaction from RR", e);
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new GeneratorException("Error obtaining transaction from RR", e);
		}
		
	}

	/**
	 * Stores execution plan to RR.
	 * @param downloadActivities download activities from planner
	 * @param buildActivities build activities from planner
	 * @param runActivities run activities from planner
	 * @param buildLogTransaction 
	 * @throws GeneratorException
	 */
	private void storePlanToRR(List<DownloadActivity> downloadActivities,
			List<BuildActivity> buildActivities, List<RunActivity> runActivities, RRTransaction buildLogTransaction) throws GeneratorException {
		
		try {
			for (DownloadActivity da : downloadActivities) {
				for (BuildActivity ba : da.getBuildActivities()) {
					storeBuildActivity(ba, buildLogTransaction);

					for (RunActivity ra : ba.getRunActivities()) {
						storeRunActivity(ra);
					}
				}
			}

			for (BuildActivity ba : buildActivities) {
				storeBuildActivity(ba, buildLogTransaction);

				for (RunActivity ra : ba.getRunActivities()) {
					storeRunActivity(ra);
				}
			}

			for (RunActivity ra : runActivities) {
				storeRunActivity(ra);
			}
			
			buildLogTransaction.commit();
			
		} catch(GeneratorException e) {
			throw new GeneratorException("Error storing execution plan to RR.", e);
		} catch (ResultsRepositoryException e) {
			throw new GeneratorException("Error storing execution plan to RR.", e);
		} catch (RemoteException e) {
			throw new GeneratorException("Error storing execution plan to RR.", e);
		}
	}
	
	/**
	 * Stores build activity to planned builds dataset
	 * @param ba build activity to store
	 * @param buildLogTransaction 
	 * @throws GeneratorException
	 */
	private void storeBuildActivity(BuildActivity ba, RRTransaction buildLogTransaction) throws GeneratorException {
		try {
			DataHandleTuple t = new DataHandleTuple();
			t.set(OMNIORB_TIMESTAMP_FIELD_NAME, DataHandle.create(DataType.STRING, ba.getVersion()));
			t.set(BUILD_NUMBER_FIELD_NAME, DataHandle.create(DataType.INT, ba.getBuildNumber()));
			buildLogTransaction.saveData(t);
		} catch (RemoteException e) {
			throw new GeneratorException("Error storing build activity to planned builds dataset.", e);
		} catch (ResultsRepositoryException e) {
			throw new GeneratorException("Error storing build activity to planned builds dataset.", e);
		}
	}
	
	/**
	 * Stores run activity to planned runs dataset
	 * @param ra run activity to store
	 * @throws GeneratorException
	 */
	private void storeRunActivity(RunActivity ra) throws GeneratorException {
		try {
			for (String suite : ra.getSuites()) {
				DataHandleTuple t = new DataHandleTuple();
				t.set(OMNIORB_TIMESTAMP_FIELD_NAME, DataHandle.create(DataType.STRING, ra.getVersion()));
				t.set(BUILD_NUMBER_FIELD_NAME, DataHandle.create(DataType.INT, ra.getBuildNumber()));
				t.set(RUN_NUMBER_FIELD_NAME, DataHandle.create(DataType.INT, ra.getRunNumber()));
				t.set(SUITE_FIELD_NAME, DataHandle.create(DataType.STRING, suite));
				resultsRepositoryData.saveData(getAnalysis().getName(), PLANNED_RUNS_DATASET_NAME, t);
			}
		} catch (RemoteException e) {
			throw new GeneratorException("Error storing run activity to planned runs dataset.", e);
		} catch (ResultsRepositoryException e) {
			throw new GeneratorException("Error storing run activity to planned runs dataset.", e);
		}
	}

	/**
	 * Retrieves set of OmniORB versions available in CVS.
	 * @return set of OmniORB versions
	 * @throws GeneratorException
	 */
	private Set<SourceKey> getCvsVersions() throws GeneratorException  {
		
		DefaultOmniorbCVSVersionProvider versionProvider = new DefaultOmniorbCVSVersionProvider();
		
		String repository = omniorbCvsRepository;
		String password = omniorbCvsPassword;
		String module = omniorbCvsModule;
		String tag = omniorbCvsBranch;	
		
		
		
		Set<SourceKey> cvsSources;
		try {
			cvsSources = versionProvider.getAvailableVersions(repository, password, null, module, tag, versionsFromDate);
		} catch (AnalysisException e) {
			e.printStackTrace();
			throw new GeneratorException("Error getting available version list from version control.", e);
		}
		
		return cvsSources;
		
	}

	/**
	 * Checks whether xampler sources have been downloaded before
	 * 
	 * @param xamplerRevision check for this xampler revision
	 * @return true if xampler sources are available 
	 * @throws GeneratorException
	 */
	private boolean isXamplerAvailable(Long xamplerRevision) throws GeneratorException {
		List<DataHandleTuple> xamplerSources = getDatasetData(getSourcesAnalysisName(), XAMPLER_SRC_DATASET_NAME, null);
		for (DataHandleTuple xamplerSource : xamplerSources) {
			try {
				if (xamplerRevision.equals(xamplerSource.get(SOURCE_REVISION_FIELD_NAME).getValue(Long.class))) {
					return true;
				}
			} catch (DataHandleException e) {
				throw new GeneratorException("Error checking whether Xampler sources are available.", e);
			}				
		}
		return false;
	}

	/**
	 * Factory method for creating planner that will be used for generating tasks.
	 * @return instance of Download-Build-Run planner to use
	 */
	private DBRPlanner createPlanner() {
		
		return new SimpleDownloadBuildRunPlanner(suites, 
													workloadUnits, downloadCost, buildCost, runCost,
													runBuildRatio, maxNumberOfBuilds , maxNumberOfRuns, versionBlacklist );
	}
	
	/**
	 * Translates run activity into corresponding list of tasks
	 * @param activity run activity to perform
	 * @param treeParent tree prefix to use for generated tasks (if null, default tree prefix will be used) 
	 * @return task tasks to perform
	 */
	private List<TaskDescriptor> getRunActivityTasks(RunActivity activity, String treeParent) {
		
		List<TaskDescriptor> tasks = new ArrayList<TaskDescriptor>();
		
		String omniorbTimestamp = activity.getVersion();
		Integer buildNumber = activity.getBuildNumber();
		Integer runNumber = activity.getRunNumber();
		
		if (treeParent == null) {
			treeParent = "run-" + omniorbTimestamp + "-" + buildNumber + "-" + runNumber;
		}
		
		boolean exclusiveRuns = true;
		if (Debug.isDebugModeOn()) {
			Task.getTaskHandle().logInfo("Generator task in debug mode. Execute tasks will be not marked as TaskExclusivity.EXCLUSIVE.");
			exclusiveRuns = false;
		}
		
		if (exclusiveRuns && simulate) {
			Task.getTaskHandle().logInfo("Analysis in simulation mode. Execute tasks will be not marked as TaskExclusivity.EXCLUSIVE.");
			exclusiveRuns = false;
		}
		
		boolean runOnSingleHost = ( serverRsl == null);
		
		for (String suiteKey : activity.getSuites()) {
			String suiteDirName = getSuiteDirName(suiteKey);
			String subsuiteDirName = getSubsuiteDirName(suiteKey);
			
			String suitePath = null;
			if (subsuiteDirName != null) {
				suitePath = suiteDirName + File.separator + xamplerSuiteSubdir + File.separator + subsuiteDirName;
			} else {
				suitePath = suiteDirName + File.separator + xamplerSuiteSubdir;
			}
			
			if (!runOnSingleHost) {
				TaskDescriptor deployXamplerServer = deployXampler(xamplerSvnRevision, omniorbTimestamp, buildNumber, serverRsl, treeParent);
				tasks.add(deployXamplerServer);

				TaskDescriptor deployXamplerClient = deployXampler(xamplerSvnRevision, omniorbTimestamp, buildNumber, clientRsl, treeParent);
				success(deployXamplerClient, deployXamplerServer.getTaskId());
				tasks.add(deployXamplerClient);

				TaskDescriptor executeXamplerServer = executeXamplerServer(deployXamplerServer.getTaskId(), suitePath, xamplerServerParams, treeParent);
				success(executeXamplerServer, deployXamplerServer.getTaskId());
				tasks.add(executeXamplerServer);
				executeXamplerServer.getHostRuntimes().setAsTask(deployXamplerServer.getTaskId());	// HRs should always be set.
				if (exclusiveRuns) {
					executeXamplerServer.setExclusive(TaskExclusivity.EXCLUSIVE);
				}

				TaskDescriptor executeXamplerClient = executeXamplerClient(deployXamplerClient.getTaskId(), suitePath, executeXamplerServer.getTaskId(), xamplerClientParams, treeParent);
				success(executeXamplerClient, deployXamplerClient.getTaskId());
				if (exclusiveRuns) {
					executeXamplerClient.setExclusive(TaskExclusivity.EXCLUSIVE);
				}

				/* wait until server creates IOR */
				TaskDescriptorHelper.addDependencyCheckpoint(
					executeXamplerClient,
					executeXamplerServer.getTaskId(),
					XamplerExecute.CHECKPOINT_SERVER_STARTED
				);

				tasks.add(executeXamplerClient);
				executeXamplerClient.getHostRuntimes().setAsTask(deployXamplerClient.getTaskId());	// HRs shold always be set.

				TaskDescriptor collectXamplerResults = collectXamplerResults(executeXamplerClient.getTaskId(),xamplerSvnRevision ,omniorbTimestamp, buildNumber, runNumber, suiteKey, treeParent);
				// collect xampler results is run even if xampler run fails
				TaskDescriptorHelper.addDependencyCheckpoint(
					collectXamplerResults,
					executeXamplerClient.getTaskId(),
					Task.CHECKPOINT_NAME_FINISHED
				);
				tasks.add(collectXamplerResults);
				collectXamplerResults.getHostRuntimes().setAsTask(executeXamplerClient.getTaskId());// HRs should always be set.
				
			} else {
				Condition hostRsl = clientRsl;
				
				TaskDescriptor deployXampler = deployXampler(xamplerSvnRevision, omniorbTimestamp, buildNumber, hostRsl, treeParent);
				tasks.add(deployXampler);

				TaskDescriptor executeXampler = executeXamplerClientAndServer(deployXampler.getTaskId(), suitePath, xamplerClientParams, xamplerServerParams, treeParent);
				success(executeXampler, deployXampler.getTaskId());
				tasks.add(executeXampler);
				executeXampler.getHostRuntimes().setAsTask(deployXampler.getTaskId());				// HRs should always be set.
				if (exclusiveRuns) {
					executeXampler.setExclusive(TaskExclusivity.EXCLUSIVE);
				}

				TaskDescriptor collectXamplerResults = collectXamplerResults(executeXampler.getTaskId(),xamplerSvnRevision ,omniorbTimestamp, buildNumber, runNumber, suiteKey, treeParent);
				// collect xampler results is run even if xampler run fails
				TaskDescriptorHelper.addDependencyCheckpoint(
					collectXamplerResults,
					executeXampler.getTaskId(),
					Task.CHECKPOINT_NAME_FINISHED
				);
				tasks.add(collectXamplerResults);
				collectXamplerResults.getHostRuntimes().setAsTask(executeXampler.getTaskId());		// HRs should always be set.
			}
		}
		
		return tasks;
	}

	/**
	 * Translates build activity into corresponding list of tasks 
	 * @param activity activity to perform
	 * @param treeParent tree prefix to use for generated tasks (if null, default tree prefix will be used) 
	 * @return task tasks to perform
	 */
	private List<TaskDescriptor> getBuildActivityTasks(
			BuildActivity activity, String treeParent) {
		
		List<TaskDescriptor> tasks = new ArrayList<TaskDescriptor>();
		
		String omniorbTimestamp = activity.getVersion();
		int buildNumber = activity.getBuildNumber();
		
		if (treeParent == null) {
			treeParent = "build-run-" + omniorbTimestamp + "-" + buildNumber;
		}
		
		TaskDescriptor buildOmniorb = buildOmniorb(omniorbTimestamp, buildNumber, buildRsl, treeParent);
		tasks.add(buildOmniorb);
		
		TaskDescriptor buildXampler = buildXampler(xamplerSvnRevision, omniorbTimestamp, buildNumber, buildRsl, treeParent);
		success(buildXampler, buildOmniorb.getTaskId());
		tasks.add(buildXampler);
		
		for (RunActivity depActivity : activity.getRunActivities()) {
			List<TaskDescriptor> dependentTasks = getRunActivityTasks(depActivity, treeParent);
			
			/* add dependency to all tasks from dependent activity */
			for (TaskDescriptor dependentTask : dependentTasks) {
				success(dependentTask, buildXampler.getTaskId());
			}
			
			tasks.addAll(dependentTasks);
		}
		
		return tasks;
	}
	
	/**
	 * Translates download activity into corresponding list of tasks 
	 * @param activity activity to perform 
	 * @return task tasks to perform
	 */
	private List<TaskDescriptor> getDownloadActivityTasks(
			DownloadActivity activity) {

		List<TaskDescriptor> tasks = new ArrayList<TaskDescriptor>();
		
		String treeParent = "download-build-run-" + activity.getVersion();
		
		TaskDescriptor downloadOmniorb = downloadOmniorb(activity.getVersion(), treeParent, downloadRsl);
		tasks.add(downloadOmniorb);
		
		for (BuildActivity depActivity : activity.getBuildActivities()) {
			List<TaskDescriptor> dependentTasks = getBuildActivityTasks(depActivity, treeParent);
			
			/* add dependency to all tasks from dependent activity */
			for(TaskDescriptor dependentTask : dependentTasks) {
				success(dependentTask, downloadOmniorb.getTaskId());
			}
			tasks.addAll(dependentTasks);
		}
		return tasks;
	}
	
	/**
	 * Retrieves all data of given dataset satisfying a condition.
	 * @param analysisName name of analysis
	 * @param datasetName name of dataset
	 * @param condition condition that has to be satfisfied
	 * @return data of dataset
	 * @throws GeneratorException
	 */
	private List<DataHandleTuple> getDatasetData(String analysisName, String datasetName, cz.cuni.mff.been.resultsrepositoryng.condition.Condition condition) throws GeneratorException {
		List<DataHandleTuple> data;
		try {
			data = resultsRepositoryData.loadData(analysisName, datasetName, condition, null, null);
		} catch (RemoteException e) {
			throw new GeneratorException("Error retrieving data from RR.", e);
		} catch (ResultsRepositoryException e) {
			throw new GeneratorException("Error retrieving data from RR.", e);
		}
		return data;
	}
	
	/**
	 * Retrieves all data of given dataset satisfying a condition.
	 * @param datasetName name of dataset
	 * @param condition condition that has to be satfisfied
	 * @return data of dataset
	 * @throws GeneratorException
	 */
	private List<DataHandleTuple> getDatasetData(String datasetName,cz.cuni.mff.been.resultsrepositoryng.condition.Condition condition) throws GeneratorException {
		return getDatasetData(getAnalysis().getName(), datasetName, Restrictions.alwaysTrue());
	}
	
	/**
	 * Retrieves all data of given dataset.
	 * @param datasetName name of dataset
	 * @return data of dataset
	 * @throws GeneratorException
	 */
	private List<DataHandleTuple> getDatasetData(String datasetName) throws GeneratorException {
		return getDatasetData(datasetName, Restrictions.alwaysTrue());
	}
	
	/**
	 * Retrieves list of available omniORB sources from omniorb sources dataset
	 * @return list of available sources
	 * @throws GeneratorException
	 */
	private Set<SourceKey> getAvailableSources() throws GeneratorException {
		Set<SourceKey> result = new HashSet<SourceKey>();
		for(DataHandleTuple tuple : getDatasetData(getSourcesAnalysisName(), OMNIORB_SRC_DATASET_NAME, null)) {
			String timestamp;
			try {
				timestamp = tuple.get(SOURCE_TIMESTAMP_FIELD_NAME).getValue(String.class).toString();
				
			} catch (DataHandleException e) {
				throw new GeneratorException("Error retrieving available sources list.", e);
			}
			result.add(new SourceKey(timestamp));
		}
		return result;
	}
	
	/**
	 * Retrieves list of already planned builds from planned builds dataset.
	 * @param buildLogTransaction 
	 * @return list of already planned builds
	 * @throws GeneratorException
	 */
	private MultiMap<BuildKey, BuildProps> getBuildLog(RRTransaction buildLogTransaction) throws GeneratorException {
		try {
			MultiMap<BuildKey, BuildProps> result = new MultiHashMap<BuildKey, BuildProps>();
			
			for(DataHandleTuple tuple : buildLogTransaction.loadData(null) ) {
				String timestamp;
				Integer buildNumber;
				try {
					timestamp = tuple.get(OMNIORB_TIMESTAMP_FIELD_NAME).getValue(String.class).toString();
					buildNumber = tuple.get(BUILD_NUMBER_FIELD_NAME).getValue(Integer.class);
				} catch (DataHandleException e) {
					throw new GeneratorException("Error retrieving available sources list.", e);
				}
				
				result.put(new BuildKey(timestamp), new BuildProps(buildNumber));
			}
			
			return result;
		} catch (ResultsRepositoryException e) {
			throw new GeneratorException("Error obtaining planner build log data", e);
		} catch (RemoteException e) {
			throw new GeneratorException("Error obtaining planner build log data", e);
		}
		
	}
	
	/**
	 * Retrieves list of available xampler binaries (that were successfully built before).
	 * @return list of available binaries
	 * @throws GeneratorException
	 */
	private MultiMap<BuildKey, BuildProps> getAvailableXamplerBinaries() throws GeneratorException {
		MultiMap<BuildKey, BuildProps> result = new MultiHashMap<BuildKey, BuildProps>();
		
		cz.cuni.mff.been.resultsrepositoryng.condition.Condition condition = Restrictions.isNotNull(FILE_ID_FIELD_NAME);
		
		for(DataHandleTuple tuple : getDatasetData( getBinaryAnalysisName(), XAMPLER_BIN_DATASET_NAME, condition )) {
			String timestamp;
			Integer buildNumber;
			try {
				timestamp = tuple.get(OMNIORB_TIMESTAMP_FIELD_NAME).getValue(String.class).toString();
				buildNumber = tuple.get(BUILD_NUMBER_FIELD_NAME).getValue(Integer.class);
			} catch (DataHandleException e) {
				throw new GeneratorException("Error retrieving available sources list.", e);
			}
			
			result.put(new BuildKey(timestamp), new BuildProps(buildNumber));
		}
		return result;
	}
	
//	/**
//	 * Retrieves list of available omniorb binaries (that were successfully built before).
//	 * @return list of available binaries
//	 * @throws GeneratorException
//	 */
//	private MultiMap<BuildKey, BuildProps> getAvailableOmniorbBinaries() throws GeneratorException {
//		MultiMap<BuildKey, BuildProps> result = new MultiHashMap<BuildKey, BuildProps>();
//		
//		cz.cuni.mff.been.resultsrepositoryng.condition.Condition condition = Restrictions.isNotNull(FILE_ID_FIELD_NAME);
//		
//		for(DataHandleTuple tuple : getDatasetData( getBinaryAnalysisName(), OMNIORB_BIN_DATASET_NAME, condition )) {
//			String timestamp;
//			Integer buildNumber;
//			try {
//				timestamp = tuple.get(OMNIORB_TIMESTAMP_FIELD_NAME).getValue(String.class).toString();
//				buildNumber = tuple.get(BUILD_NUMBER_FIELD_NAME).getValue(Integer.class);
//			} catch (DataHandleException e) {
//				throw new GeneratorException("Error retrieving available sources list.", e);
//			}
//			
//			result.put(new BuildKey(timestamp), new BuildProps(buildNumber));
//		}
//		return result;
//	}
	
	/**
	 * Retrieves list of already planned runs from planned runs.
	 * @return list of already planned runs
	 * @throws GeneratorException
	 */
	private MultiMap<RunKey, RunProps> getRunLog() throws GeneratorException {
		MultiMap<RunKey, RunProps> result = new MultiHashMap<RunKey, RunProps>();
		for(DataHandleTuple tuple : getDatasetData(PLANNED_RUNS_DATASET_NAME)) {
			String timestamp;
			Integer buildNumber;
			Integer runNumber;
			String suite;
			try {
				timestamp = tuple.get(OMNIORB_TIMESTAMP_FIELD_NAME).getValue(String.class).toString();
				buildNumber = tuple.get(BUILD_NUMBER_FIELD_NAME).getValue(Integer.class);
				runNumber = tuple.get(RUN_NUMBER_FIELD_NAME).getValue(Integer.class);
				suite = tuple.get(SUITE_FIELD_NAME).getValue(String.class);
			} catch (DataHandleException e) {
				throw new GeneratorException("Error retrieving available sources list.", e);
			}
			RunKey runKey = new RunKey(timestamp, buildNumber);
			
			result.put(runKey, new RunProps(runNumber, suite));
		}
		return result;
	}
	
	/**
	 * Task to download omniorb
	 * @param timestamp version of omniorb to download
	 * @param treeParent tree prefix to use in task's tree address
	 * @param downloadCondition RSL condition for task's host
	 * @return task's taskdescriptor
	 */
	private TaskDescriptor downloadOmniorb(String timestamp, String treeParent, Condition downloadCondition) {
		
		TaskDescriptor td = createTask("download-cvs", downloadCondition, treeParent + "/omniORB CVS download");
		TaskDescriptorHelper.addTaskProperties(
			td,
			Pair.pair("cvs.repository", omniorbCvsRepository),
			Pair.pair("cvs.module", omniorbCvsModule),
			Pair.pair("cvs.branch", omniorbCvsBranch),
			Pair.pair("timestamp", timestamp),
			Pair.pair("rr.analysis", getSourcesAnalysisName()),
			Pair.pair("rr.dataset", OMNIORB_SRC_DATASET_NAME)
		);
		if (simulate) {
			TaskDescriptorHelper.addTaskProperties(td, Pair.pair("simulate", "yes"));
		}
		return td;
	}
	
	/**
	 * Task to download xampler
	 * @param revision version of Xampler to download (SVN revision)
	 * @param downloadCondition RSL condition for task's host
	 * @return task's taskdescriptor
	 */
	private TaskDescriptor downloadXampler(Long revision, Condition downloadCondition) {
		TaskDescriptor td = createTask("download-svn", downloadCondition, "Xampler SVN donwload");
		TaskDescriptorHelper.addTaskProperties(
			td,
			Pair.pair("url", xamplerSvnUrl),
			Pair.pair("revision", xamplerSvnRevision.toString()),
			Pair.pair("rr.analysis", getSourcesAnalysisName()),
			Pair.pair("rr.dataset", XAMPLER_SRC_DATASET_NAME)
		);
		if (simulate) {
			TaskDescriptorHelper.addTaskProperties( td, Pair.pair("simulate", "yes") );
		}
		return td;
	}
		 
	/**
	 * Task to build omniORB 
	 * @param timestamp version of omniORB to build 
	 * @param buildNumber build number of build
	 * @param buildCondition RSL condition for task's host
	 * @param treeParent tree prefix that task should use
	 * @return task's taskdesriptor
	 */
	private TaskDescriptor buildOmniorb(String timestamp, Integer buildNumber, Condition buildCondition, String treeParent) {
		TaskDescriptor td = createTask("omniorb-build-linux", buildCondition, treeParent + "/omniORB build");
		TaskDescriptorHelper.addTaskProperties(
			td,
			Pair.pair("timestamp", timestamp),
			Pair.pair("build.number", buildNumber.toString()),
			Pair.pair("rr.src.analysis", getSourcesAnalysisName()),
			Pair.pair("rr.src.dataset", OMNIORB_SRC_DATASET_NAME),
			Pair.pair("rr.bin.analysis", getBinaryAnalysisName()),
			Pair.pair("rr.bin.dataset", OMNIORB_BIN_DATASET_NAME)
		);
		if (omniorbApplyPatch) {
			TaskDescriptorHelper.addTaskProperties(td, Pair.pair("patch", "yes"));
		}
		if (simulate) {
			TaskDescriptorHelper.addTaskProperties(td, Pair.pair("simulate", "yes"));
		}
		return td;
	}
	
	/**
	 * Task to build omniORB
	 * @param xamplerRevision version of Xampler to build 
	 * @param omniorbTimestamp version of omniORB to build
	 * @param buildNumber build number of build
	 * @param buildCondition RSL condition for task's host
	 * @param treeParent tree prefix that task should use
	 * @return task's taskdesriptor
	 */
	private TaskDescriptor buildXampler(Long xamplerRevision, String omniorbTimestamp, Integer buildNumber, Condition buildCondition, String treeParent) {
		TaskDescriptor td = createTask("xampler-build-linux", buildCondition, treeParent + "/Xampler build");
		TaskDescriptorHelper.addTaskProperties(
			td,
			Pair.pair("rr.src.analysis", getSourcesAnalysisName()),
			Pair.pair("rr.src.dataset", XAMPLER_SRC_DATASET_NAME),
			Pair.pair("rr.omniorb.analysis", getBinaryAnalysisName()),
			Pair.pair("rr.omniorb.dataset", OMNIORB_BIN_DATASET_NAME),
			Pair.pair("rr.bin.analysis", getBinaryAnalysisName()),
			Pair.pair("rr.bin.dataset", XAMPLER_BIN_DATASET_NAME),
			Pair.pair("xampler.suite.subdir", xamplerSuiteSubdir),
			Pair.pair("omniorb.timestamp", omniorbTimestamp),
			Pair.pair("xampler.revision", xamplerRevision.toString()),
			Pair.pair("build.number", buildNumber.toString())
		);
		if (simulate) {
			TaskDescriptorHelper.addTaskProperties(td, Pair.pair("simulate", "yes"));
		}
		return td;
	}
	/**
	 * Task to deploy Xampler
	 * @param xamplerRevision version of Xampler
	 * @param omniorbTimestamp version of omniORB
	 * @param buildNumber build number 
	 * @param hostCondition condition to which host should be deployed
	 * @param treeParent tree prefix for task
	 * @return task's taskdescriptor
	 */
	private TaskDescriptor deployXampler(Long xamplerRevision, String omniorbTimestamp, Integer buildNumber, Condition hostCondition, String treeParent) { 
		TaskDescriptor td = createTask("xampler-deploy", hostCondition, treeParent + "/Xampler deploy");
		TaskDescriptorHelper.addTaskProperties(
			td,
			Pair.pair("rr.omniorb.analysis", getBinaryAnalysisName()),
			Pair.pair("rr.omniorb.dataset", OMNIORB_BIN_DATASET_NAME),
			Pair.pair("rr.xampler.analysis", getBinaryAnalysisName()),
			Pair.pair("rr.xampler.dataset", XAMPLER_BIN_DATASET_NAME),
			
			Pair.pair("omniorb.timestamp", omniorbTimestamp.toString()),
			Pair.pair("xampler.revision", xamplerRevision.toString()),
			Pair.pair("build.number", buildNumber.toString()),
			
			Pair.pair("omniorb.dir", OMNIORB_DIR),
			Pair.pair("xampler.dir", XAMPLER_DIR)
		);
		if (simulate) {
			TaskDescriptorHelper.addTaskProperties(td, Pair.pair("simulate", "yes"));
		}
		return td;
	}
	
	/**
	 * Task for Xampler run
	 * @param deployTid task ID of corresponding deploy task
	 * @param suitePath path to suite that should be run
	 * @param isServer task's role (null means singlehost mode)
	 * @param treeParent tree prefix for task's tree address
	 * @return task's taskdescriptor
	 */
	private TaskDescriptor executeXampler(String deployTid, String suitePath, Boolean isServer, String treeParent) {
		TaskDescriptor td = createTask("xampler-execute", Analysis.DEFAULT_HOST_RSL, treeParent + ( isServer != null ? (isServer ? "/Xampler server execute" : "/Xampler client execute") : "/Xampler execute"));
		
		String omniorbRoot = "${"+deployTid+":workingDirectory}" + File.separator + OMNIORB_DIR;
		String xamplerRoot = "${"+deployTid+":workingDirectory}" + File.separator + XAMPLER_DIR;
		
		TaskDescriptorHelper.addTaskProperties(
			td,
			Pair.pair("omniorb.root", omniorbRoot),
			Pair.pair("xampler.root", xamplerRoot),
			
			Pair.pair("xampler.suite.path", suitePath)
		);
		//td.setExclusive(TaskExclusivity.EXCLUSIVE);
		if (simulate) {
			TaskDescriptorHelper.addTaskProperties(td, Pair.pair("simulate", "yes"));
		}
		return td;
	}
	
	/**
	 * Task for collecting xampler results
	 * @param clientExecuteTid task ID of client execute task
	 * @param xamplerRevision version of Xampler
	 * @param omniorbTimestamp version of omniORB
	 * @param buildNumber build number
	 * @param runNumber run number
	 * @param suite suite 
	 * @param treeParent tree prefix for task's tree address
	 * @return task's taskdescriptor
	 */
	private TaskDescriptor collectXamplerResults(String clientExecuteTid, Long xamplerRevision, String omniorbTimestamp, Integer buildNumber, Integer runNumber, String suite, String treeParent) {
		TaskDescriptor td = createTask("xampler-collect-results", Analysis.DEFAULT_HOST_RSL, treeParent + "/Xampler collect results");
		
		String resultsDir = "${"+clientExecuteTid+":workingDirectory}";
		
		TaskDescriptorHelper.addTaskProperties(
			td,
			Pair.pair("results.dir", resultsDir),

			Pair.pair("rr.results.analysis", getAnalysis().getName()),
			Pair.pair("rr.results.dataset", XAMPLER_RESULTS_DATASET_NAME),
			Pair.pair("xampler.revision", xamplerRevision.toString()),
			Pair.pair("omniorb.timestamp", omniorbTimestamp.toString()),
			Pair.pair("build.number", buildNumber.toString()),
			Pair.pair("run.number", runNumber.toString()),
			Pair.pair("suite.name", suite)
		);
		if (simulate) {
			TaskDescriptorHelper.addTaskProperties(td, Pair.pair("simulate", "yes"));
		}
		return td;
	}
	
	/**
	 * Task for executing Xampler server 
	 * @param deployTid deploy task's ID
	 * @param suitePath path to suite that will be executed
	 * @param params xampler server parameters 
	 * @param treeParent tree prefix for task's tree address
	 * @return task's taskdesriptor
	 */
	private TaskDescriptor executeXamplerServer(String deployTid, String suitePath, String params, String treeParent) {
		TaskDescriptor td = executeXampler(deployTid, suitePath, true, treeParent);
		TaskDescriptorHelper.addTaskProperties(
			td,
			Pair.pair("xampler.role", "server"),
			Pair.pair("xampler.server.params", params)
		);
		if (simulate) {
			TaskDescriptorHelper.addTaskProperties(td, Pair.pair("simulate", "yes"));
		}
		return td;
	}
	
	/**
	 * Task for executing Xampler client 
	 * @param deployTid deploy task's ID
	 * @param suitePath path to suite that will be executed
	 * @param serverTid execute server task's ID
	 * @param params xampler client parameters 
	 * @param treeParent tree prefix for task's tree address
	 * @return task's taskdesriptor
	 */
	private TaskDescriptor executeXamplerClient(String deployTid, String suitePath, String serverTid, String params, String treeParent) {
		TaskDescriptor td = executeXampler(deployTid, suitePath, false, treeParent);
		TaskDescriptorHelper.addTaskProperties(
			td,
			Pair.pair("xampler.role", "client"),
			Pair.pair("server.tid", serverTid),
			Pair.pair("xampler.client.params", params)
		);
		if (simulate) {
			TaskDescriptorHelper.addTaskProperties(td, Pair.pair("simulate", "yes"));
		}
		return td;
	}
	
	/**
	 * Task for executing both Xampler server and client on a single host 
	 * @param deployTid deploy task's ID
	 * @param suitePath path to suite that will be executed
	 * @param clientParams xampler client parameters 
	 * @param serverParams xampler server parameters
	 * @param treeParent tree prefix for task's tree address
	 * @return task's taskdesriptor
	 */
	private TaskDescriptor executeXamplerClientAndServer(String deployTid, String suitePath, String clientParams, String serverParams, String treeParent) {
		TaskDescriptor td = executeXampler(deployTid, suitePath, null, treeParent);
		TaskDescriptorHelper.addTaskProperties(
			td,
			Pair.pair("xampler.role", "both"),
			Pair.pair("xampler.client.params", clientParams),
			Pair.pair("xampler.server.params", serverParams)
		);
		if (simulate) {
			TaskDescriptorHelper.addTaskProperties(td, Pair.pair("simulate", "yes"));
		}
		return td;
	}
	
	/**
	 * Initializes RR reference.
	 * @throws RemoteException
	 */
	private void initResultsRepositoryReference() throws GeneratorException, RemoteException {
		Object rr = Task.getTaskHandle().getTasksPort().serviceFind(
				ResultsRepositoryService.SERVICE_NAME,
				Service.RMI_MAIN_IFACE );
		if( rr == null ){
			throw new GeneratorException("Results Repository reference cannot be obtained. Maybe it is not running.");
		}
		resultsRepositoryData = (RRDataInterface) rr;
	}
	
	
	/**
	 * For suite key (that can contain both name of suite and subsuite)return appropriate suite directory name
	 * @param suiteKey suite key (identifier) 
	 * @return suite directory name
	 */
	private String getSuiteDirName(String suiteKey) {
		suiteKey = suiteKey.split("/")[0];
		
		Map<String, String> suiteNames = new HashMap<String, String>();
		suiteNames.put("ping", "Ping");
		suiteNames.put("ccc", "Concurrency_Client_Connections");
		suiteNames.put("dsi", "Dispatch_Servant_Instances");
		suiteNames.put("dsl", "Dispatch_Servant_Lifecycle");
		suiteNames.put("id", "Invocation_Dynamic");
		suiteNames.put("ii", "Invocation_Initial");
		suiteNames.put("is", "Invocation_Static");
		suiteNames.put("void", "Void");
		suiteNames.put("mta", "Marshal_Types_Array");
		suiteNames.put("mts", "Marshal_Types_Single");
		suiteNames.put("mos", "Marshal_Octet_Sequence");
		suiteNames.put("mds", "Marshal_Delayed_Sequence");
		suiteNames.put("mda", "Marshal_Delayed_Any");
		
		return suiteNames.get(suiteKey);
	}
	
	/**
	 * For suite key (that can contain both name of suite and subsuite) return appropriate subsuite directory name
	 * @param suiteKey suite key (identifier) 
	 * @return subsuite directory name of null if no subsuite name is present 
	 */
	private String getSubsuiteDirName(String suiteKey) {
		String[] parts = suiteKey.split("/");
		if (parts.length < 2) return null;
		return parts[1];
	}
	
	/**
	 * Parses integer
	 * @param s string to parse
	 * @param fieldName field name that will appear in error message
	 * @param errors error message accumulator
	 * @return number
	 */
	Integer parseInt(String s, String fieldName, List<String> errors) {
		try {
			return Integer.parseInt(s);
		} catch(NumberFormatException e) {
			errors.add("Error parsing field " + fieldName + ": " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Parses float
	 * @param s string to parse
	 * @param fieldName field name that will appear in error message
	 * @param errors error message accumulator
	 * @return number
	 */
	Float parseFloat(String s, String fieldName, List<String> errors) {
		try {
			return Float.parseFloat(s);
		} catch(NumberFormatException e) {
			errors.add("Error parsing field " + fieldName + ": " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Parses long
	 * @param s string to parse
	 * @param fieldName field name that will appear in error message
	 * @param errors error message accumulator
	 * @return number
	 */
	Long parseLong(String s, String fieldName, List<String> errors) {
		try {
			return Long.parseLong(s);
		} catch(NumberFormatException e) {
			errors.add("Error parsing field " + fieldName + ": " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Parses RSL condition 
	 * @param string string to parse
	 * @param fieldName field name that will appear in error message
	 * @param errors error message accumulator
	 * @return RSL condition
	 */
	private Condition parseRSLCondition(String string, String fieldName, List<String> errors, boolean allowEmpty) {
		try {
			if (allowEmpty && string == null) {
				return null;
			}
			string = string.trim();
			if (allowEmpty && string.isEmpty()) {
				return null; 
			} 
			return ParserWrapper.parseString(string);
		} catch(ParseException e) {
			errors.add("Error parsing field " + fieldName + ": " + e.getMessage());
			return null;
		} 
	}
	
	
	private Set<String> prepareSuiteSet(String[] suiteIds, Pattern subsuitePattern) {
		Set<String> result = new TreeSet<String> ();
		Map<String, String[]> subsuiteMap = getSubsuiteMap();
		
		for (String suiteId : suiteIds) {
			String[] subsuites = subsuiteMap.get(suiteId);
			
			if (subsuites == null) {
				/* if there are no subsuites, add suite id to result set */
				result.add(suiteId);
			} else {
				for(String subsuite : subsuites) {
					if (subsuitePattern.matcher(subsuite).matches()) {
						/* if subsuite matches regex, add it to results set */
						String suite = suiteId + "/" + subsuite;
						result.add(suite);
					}
				}
			}
		}
		
		return result;
	}
	
	
	/**
	 * @return map that contains list of subsuites for every suite that has some subsuites
	 */
	private Map<String, String[]> getSubsuiteMap() {
		Map<String, String[]> result = new HashMap<String, String[]>();
		
		result.put("mda", new String[] { "Accessed", "Ignored" });
		
		result.put("mds", new String[] { "Accessed", "Ignored" });
		
		result.put("mos", new String[] { "IN", "INOUT", "OUT", "RETURN"  });
		
		result.put("mta", new String[] { 
				"Boolean_1024_IN",         "LongDouble_64_RETURN",  "ULongLong_1024_OUT",
				"Boolean_1024_INOUT",      "LongLong_1024_IN",      "ULongLong_1024_RETURN",
				"Boolean_1024_OUT",        "LongLong_1024_INOUT",   "ULongLong_128_IN",
				"Boolean_1024_RETURN",     "LongLong_1024_OUT",     "ULongLong_128_INOUT",
				"Double_1024_IN",          "LongLong_1024_RETURN",  "ULongLong_128_OUT",
				"Double_1024_INOUT",       "LongLong_128_IN",       "ULongLong_128_RETURN",
				"Double_1024_OUT",         "LongLong_128_INOUT",    "ULong_1024_IN",
				"Double_1024_RETURN",      "LongLong_128_OUT",      "ULong_1024_INOUT",
				"Double_128_IN",           "LongLong_128_RETURN",   "ULong_1024_OUT",
				"Double_128_INOUT",        "Long_1024_IN",          "ULong_1024_RETURN",
				"Double_128_OUT",          "Long_1024_INOUT",       "ULong_256_IN",
				"Double_128_RETURN",       "Long_1024_OUT",         "ULong_256_INOUT",
				"Float_1024_IN",           "Long_1024_RETURN",      "ULong_256_OUT",
				"Float_1024_INOUT",        "Long_256_IN",           "ULong_256_RETURN",
				"Float_1024_OUT",          "Long_256_INOUT",        "UShort_1024_IN",
				"Float_1024_RETURN",       "Long_256_OUT",          "UShort_1024_INOUT",
				"Float_256_IN",            "Long_256_RETURN",       "UShort_1024_OUT",
				"Float_256_INOUT",         "Octet_1024_IN",         "UShort_1024_RETURN",
				"Float_256_OUT",           "Octet_1024_INOUT",      "UShort_512_IN",
				"Float_256_RETURN",        "Octet_1024_OUT",        "UShort_512_INOUT",
				"Char_1024_IN",            "Octet_1024_RETURN",     "UShort_512_OUT",
				"Char_1024_INOUT",         "Short_1024_IN",         "UShort_512_RETURN",
				"Char_1024_OUT",           "Short_1024_INOUT",      "WChar_1024_IN",
				"Char_1024_RETURN",        "Short_1024_OUT",        "WChar_1024_INOUT",
				"LongDouble_1024_IN",      "Short_1024_RETURN",     "WChar_1024_OUT",
				"LongDouble_1024_INOUT",   "Short_512_IN",          "WChar_1024_RETURN",
				"LongDouble_1024_OUT",     "Short_512_INOUT",       "WChar_512_IN",
				"LongDouble_1024_RETURN",  "Short_512_OUT",         "WChar_512_INOUT",
				"LongDouble_64_IN",        "Short_512_RETURN",      "WChar_512_OUT",
				"LongDouble_64_INOUT",     "ULongLong_1024_IN",     "WChar_512_RETURN",
				"LongDouble_64_OUT",       "ULongLong_1024_INOUT" });
		
		result.put("mts", new String[] { 
				"Boolean_IN",      "Char_INOUT",         "Long_OUT",      "ULongLong_INOUT",
				"Boolean_INOUT",   "Char_OUT",           "Long_RETURN",   "ULongLong_OUT",
				"Boolean_OUT",     "Char_RETURN",        "Octet_IN",      "ULongLong_RETURN",
				"Boolean_RETURN",  "LongDouble_IN",      "Octet_INOUT",   "ULong_OUT",
				"Double_IN",       "LongDouble_INOUT",   "Octet_OUT",     "ULong_RETURN",
				"Double_INOUT",    "LongDouble_OUT",     "Octet_RETURN",  "UShort_IN",
				"Double_OUT",      "LongDouble_RETURN",  "Short_IN",      "UShort_INOUT",
				"Double_RETURN",   "Long_IN",            "Short_INOUT",   "UShort_OUT",
				"Float_IN",        "Long_INOUT",         "Short_OUT",     "UShort_RETURN",
				"Float_INOUT",     "LongLong_IN",        "Short_RETURN",  "WChar_IN",
				"Float_OUT",       "LongLong_INOUT",     "ULong_IN",      "WChar_INOUT",
				"Float_RETURN",    "LongLong_OUT",       "ULong_INOUT",   "WChar_OUT",
				"Char_IN",         "LongLong_RETURN",    "ULongLong_IN",  "WChar_RETURN"});
		
		return result;
	}
	
	private Set<SourceKey> parseVersionBlacklist(String string) throws java.text.ParseException {
		Set<SourceKey> result = new TreeSet<SourceKey>();
		
		String[] lines = string.split("\n");
		
		for (String line : lines) {
			line = line.trim();
			if (!line.isEmpty()) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String version = df.format(df.parse(line));
				result.add(new SourceKey( version ));	
			}
		}
		
		return result;
	}
	
	/**
	 * Reads configuration from given object and validates it.
	 * Returns list of error messages.
	 * Read configuration is stored into this object's fields
	 * @param configuration configuration
	 * @return list of error messages or null if no error encountered
	 */
	private List<String> readConfiguration(Configuration configuration) {
		List<String> errors = new ArrayList<String>();
		
		runBuildRatio = parseFloat(configuration.get("run.build.ratio",0), "run.build.ratio", errors);
		maxNumberOfRuns = parseInt(configuration.get("max.runs",0), "max.runs", errors);
		maxNumberOfBuilds = parseInt(configuration.get("max.builds",0), "max.builds", errors);
		
		workloadUnits = parseInt(configuration.get("workload.units",0), "workload.units", errors);
		downloadCost = parseInt(configuration.get("download.cost",0), "download.cost", errors);
		buildCost = parseInt(configuration.get("build.cost",0), "build.cost", errors);
		runCost = parseInt(configuration.get("run.cost",0), "run.cost", errors);
		
		
		omniorbCvsBranch = configuration.get("omniorb.cvs.branch",0);
		omniorbCvsModule = configuration.get("omniorb.cvs.module",0);
		omniorbCvsPassword = configuration.get("omniorb.cvs.password",0);
		omniorbCvsRepository = configuration.get("omniorb.cvs.repository",0);
		
		xamplerSvnUrl = configuration.get("xampler.svn.url",0);
		xamplerSvnRevision = parseLong(configuration.get("xampler.svn.revision",0), "xampler.svn.revision", errors);
		
		xamplerSuiteSubdir = configuration.get("xampler.suite.subdir",0);
		
		xamplerServerParams = configuration.get("xampler.server.params",0);
		xamplerClientParams = configuration.get("xampler.client.params",0);
		
		
		downloadRsl = parseRSLCondition(configuration.get("downloadrsl",0), "downloadrsl", errors, false);
		clientRsl = parseRSLCondition(configuration.get("clientrsl",0), "clientrsl", errors, false);
		buildRsl = parseRSLCondition(configuration.get("buildrsl",0), "buildrsl", errors, false);
		/* if null, execute will be performed on a single host */
		serverRsl = parseRSLCondition(configuration.get("serverrsl",0), "serverrsl", errors, true);
		
		
		/* simulate */
		if ("simulate".equals(configuration.get("task.run.mode",0))) {
			simulate = true;
		} else {
			simulate = false;
		}
		
		if ("true".equals(configuration.get("omniorb.apply.patch",0))) {
			omniorbApplyPatch = true;
		} else {
			omniorbApplyPatch = false;
		}
		
		
		if ("true".equals(configuration.get("sources.repository.use",0))) {
			useSourcesRepositoryAnalysis = true;
		} else {
			useSourcesRepositoryAnalysis = false;
		}
		sourcesRepositoryAnalysisSafeName = configuration.get("sources.repository.analysis",0);
		
		if ("true".equals(configuration.get("binaries.repository.use",0))) {
			useBinaryRepositoryAnalysis = true;
		} else {
			useBinaryRepositoryAnalysis = false;
		}
		
		binaryRepositoryAnalysisSafeName = configuration.get("binaries.repository.analysis",0);
		
		
		
		/* versions.from.date */
		versionsFromDate = null;
		String versionsFromDateString = configuration.get("versions.from.date", 0);
		if (versionsFromDateString != null && !versionsFromDateString.isEmpty()) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				versionsFromDate = df.parse(versionsFromDateString);
			} catch (java.text.ParseException e) {
				errors.add("Unable to parse date \"" + versionsFromDateString +"\"" + e.getMessage());
			}
		}
		
		/* version blacklist */
		try {
			versionBlacklist = parseVersionBlacklist(configuration.get("version.blacklist", 0));
		} catch (java.text.ParseException e) {
			errors.add("Unable to version blacklist." + e.getMessage());
		}
		
		
		/* subsuite regex */
		 
		String subsuiteRegexString = null;
		try {
			subsuiteRegexString = configuration.get("subsuite.regex", 0);
			subsuiteRegex = Pattern.compile(subsuiteRegexString);
		} catch(PatternSyntaxException e) {
			errors.add("Unable to parse subsuite selection regex \"" + subsuiteRegexString +"\"" + e.getMessage());
		}
		
		/* suite set */
		suites = prepareSuiteSet(configuration.get("suite"), subsuiteRegex);
		
		if (errors.size() == 0) {
			return null;
		} else {
			return errors;
		}
	}

	/**
	 * 
	 * @return safe name of analysis from where to get xampler and omniorb sources
	 */
	private String getSourcesAnalysisName() {
		if (useSourcesRepositoryAnalysis) {
			return sourcesRepositoryAnalysisSafeName; 
		} else {
			return getAnalysis().getName();
		}
	}
	
	/**
	 * 
	 * @return safe name of analysis from where to get xampler and omniorb sources
	 */
	private String getBinaryAnalysisName() {
		if (useBinaryRepositoryAnalysis) {
			return binaryRepositoryAnalysisSafeName; 
		} else {
			return getAnalysis().getName();
		}
	}
	
}
