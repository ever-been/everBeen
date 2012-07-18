/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2010 Distributed Systems Research Group,
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
package cz.seznam.been.module.hintserver.generator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.benchmarkmanagerng.module.ConfigurationException;
import cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorException;
import cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorPluggableModule;
import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.common.rsl.Condition;
import cz.cuni.mff.been.common.rsl.ParseException;
import cz.cuni.mff.been.common.rsl.ParserWrapper;
import cz.cuni.mff.been.common.scripting.ScriptEnvironment;
import cz.cuni.mff.been.common.scripting.ScriptException;
import cz.cuni.mff.been.common.scripting.ScriptLauncher;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.jaxb.td.TaskExclusivity;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.RRDataInterface;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryService;
import cz.cuni.mff.been.resultsrepositoryng.condition.AlwaysTrueCondition;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper;

/**
 * Generator for measuring requests per second of Hintserver created by
 * Seznam.cz.
 * 
 * @author Jiri Tauber
 */
public class HintserverGenerator extends GeneratorPluggableModule {

	//------------------------------------------------------------------------//
	// Datasets and their tags
	/** The name of the dateset where compiled binaries are stored */
	public static final String DATASET_BINARY = "binary";   // copy in BinUpload & LogUpload task
	/** A binary dataset tag */
	public static final String TAG_BINARY_BINARY = "bin_file"; // copy in BinUpload & LogUpload task
	/** A binary dataset tag */
	public static final String TAG_BINARY_VERSION = "version"; // copy in BinUpload task
	/** A binary dataset tag */
	public static final String TAG_BINARY_BUILDTIME = "build_time"; // copy in BinUpload task
	/** A binary dataset tag */
	public static final String TAG_BINARY_RUN_COUNT = "run_count"; // copy in BinUpload & LogUpload task

	/** The name of the dataset where logs are stored */
	public static final String DATASET_LOG = "server_log";  // copy in evaluator & LogUpload
	/** A log dataset tag */
	public static final String TAG_LOG_LOGFILE = "log_file";  // copy in evaluator task & LogUpload
	/** A log dataset tag */
	public static final String TAG_LOG_BINARY = "binary_id";  // copy in LogUpload
	/** A log dataset tag */
	public static final String TAG_LOG_RPS_SEQUENCE = "rps_sequence";  // copy in LogUpload
	/** A log dataset tag */
	public static final String TAG_LOG_CLIENT_COUNT = "client_count";  // copy in LogUpload
	/** A log dataset tag */
	public static final String TAG_LOG_SERVER = "server";  // copy in LogUpload
	/** A log dataset tag */
	public static final String TAG_LOG_DATABASE = "database";  // copy in LogUpload

	//------------------------------------------------------------------------//
	// Configuration
	/** Configuration parameter that holds source repository URL */
	private static final String CONF_REPOSITORY = "repository.url";
	/** Configuration parameter that holds RLS of hosts which will be used for compilation */
	private static final String CONF_BUILD_RSL = "build.rsl";

	/** Configuration parameter for the RSL of Hintserver deployment */
	private static final String CONF_SERVER_RSL = "server.rsl";
	/** Configuration parameter that holds the number of hosts used */
	private static final String CONF_CLIENT_COUNT = "client.count";
	/** Configuration parameter that holds the RSL for client hosts */
	private static final String CONF_CLIENT_RSL = "client.rsl";

	/** Configuration parameter that says the maximal number of builds per run */
	private static final String CONF_MAX_BUILDS = "max.builds";
	/** Configuration parameter that says the maximal number of runs per build */
	private static final String CONF_RUN_RATIO = "run.ratio";
	/** Configuration parameter for planner points */
	private static final String CONF_PLANNER_POINTS = "planner.points";
	/** Configuration parameter for number of planner points used for build */
	private static final String CONF_BUILD_PRICE = "build.price";
	/** Configuration parameter for number of planner points used for run */
	private static final String CONF_RUN_PRICE = "run.price";

	/** Configuration parameter for maximal value of requests per second */
	private static final String CONF_RPS_MAX = "rps.max";
	/** Configuration parameter for the RPS step */
	private static final String CONF_RPS_STEP = "rps.step";
	/** Configuration parameter for delay between each request burst */
	private static final String CONF_BURST_DELAY = "burst.delay";
	/** Configuration parameter for each request burst time */
	private static final String CONF_BURST_TIME = "burst.time";

	//------------------------------------------------------------------------//

	private static final int BURST_START_DELAY = 30; // seconds

	//------------------------------------------------------------------------//

	public HintserverGenerator(PluggableModuleManager manager) {
		super(manager);
	}

	//------------------------------------------------------------------------//
	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorPluggableModule#doValidateConfiguration(cz.cuni.mff.been.benchmarkmanagerng.Configuration)
	 */
	@Override
	protected Collection<String> doValidateConfiguration(Configuration configuration) {
		ArrayList<String> errors = new ArrayList<String>();
		String propName;
		String propValue;

		checkIsInt(configuration, CONF_PLANNER_POINTS, errors);
		checkIsInt(configuration, CONF_BUILD_PRICE, errors);
		checkIsInt(configuration, CONF_RUN_PRICE, errors);

		checkIsInt(configuration, CONF_MAX_BUILDS, errors);
		checkIsInt(configuration, CONF_RUN_RATIO, errors);
		checkIsInt(configuration, CONF_CLIENT_COUNT, errors);

		checkIsRSL(configuration, CONF_BUILD_RSL, errors);
		checkIsRSL(configuration, CONF_SERVER_RSL, errors);
		checkIsRSL(configuration, CONF_CLIENT_RSL, errors);

		checkIsInt(configuration, CONF_RPS_MAX, errors);
		checkIsInt(configuration, CONF_RPS_STEP, errors);
		checkIsInt(configuration, CONF_BURST_DELAY, errors);
		checkIsInt(configuration, CONF_BURST_TIME, errors);

		propName = CONF_REPOSITORY;
		propValue = configuration.get(propName, 0);
		if (propValue == null || propValue.isEmpty()){
			errors.add("Configuration parameter '"+propName+"' is missing");
		} 

		return errors;
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorPluggableModule#doCreateDatasets()
	 */
	@Override
	protected Map<String, DatasetDescriptor> doCreateDatasets() throws ConfigurationException, GeneratorException {
		Map<String, DatasetDescriptor> descriptors = new HashMap<String, DatasetDescriptor>();
		DatasetDescriptor dd;

		dd = new DatasetDescriptor();
		dd.put(TAG_BINARY_VERSION, DataHandle.DataType.INT, true);
		dd.put(TAG_BINARY_RUN_COUNT, DataHandle.DataType.INT, false);
		dd.put(TAG_BINARY_BUILDTIME, DataHandle.DataType.STRING, false);
		dd.put(TAG_BINARY_BINARY, DataHandle.DataType.FILE, false);
		descriptors.put(DATASET_BINARY, dd);

		dd = new DatasetDescriptor();
		dd.put(TAG_LOG_BINARY, DataHandle.DataType.UUID, true);  // links to DATASET_BINARY->TAG_BINARY_BINARY
		dd.put(TAG_LOG_CLIENT_COUNT, DataHandle.DataType.INT, false);
		dd.put(TAG_LOG_RPS_SEQUENCE, DataHandle.DataType.STRING, false);
		dd.put(TAG_LOG_SERVER, DataHandle.DataType.STRING, false);
		dd.put(TAG_LOG_DATABASE, DataHandle.DataType.STRING, false);
		dd.put(TAG_LOG_LOGFILE, DataHandle.DataType.FILE, false);
		descriptors.put(DATASET_LOG, dd);

		return descriptors;
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorPluggableModule#doGenerate()
	 */
	@Override
	protected Collection<TaskDescriptor> doGenerate() throws ConfigurationException, GeneratorException {

		logInfo("Running SVN to determine current HEAD revision number");
		int headVersion = getHeadVersion();
		logInfo("Current HEAD revision number is "+headVersion);

		logInfo("Loading information about compiled binaries from the RR");
		BinaryData[] binaryData = loadBinaryData();
		int runRatio = Integer.decode(getConfiguration().get(CONF_RUN_RATIO, 0));

		logInfo("Initializing planner");
		Planner planner = new Planner(
				Integer.decode(getConfiguration().get(CONF_PLANNER_POINTS, 0)),
				Integer.decode(getConfiguration().get(CONF_BUILD_PRICE, 0)),
				Integer.decode(getConfiguration().get(CONF_RUN_PRICE, 0)),
				runRatio,
				Integer.decode(getConfiguration().get(CONF_MAX_BUILDS, 0)),
				binaryData,
				headVersion);

		List<TaskDescriptor> tasks = new ArrayList<TaskDescriptor>();

		Integer buildVersion;
		UUID runBinary;
		do {
			buildVersion = planner.planBuild();
			if (buildVersion != null){
				logInfo("Generating build of version "+buildVersion);
				generateBuild(buildVersion, tasks);
			} else {
				logInfo("No versions to build");
			}

			int runs = 0;
			do {
				runBinary = planner.planRun();
				if (runBinary != null) {
					logInfo("Generating run for binary number "+runBinary);
					generateRun(runBinary, tasks);
				} else {
					logInfo("No binaries to run");
					break;
				}
			} while (runs++ <= runRatio); // do runRatio+1 builds per build
		} while (buildVersion != null && runBinary != null);

		return tasks;
	}

	//------------------------------------------------------------------------//

	/**
	 * Contacts the SVN repository to retrieve the number for HEAD revision.
	 * 
	 * @return the HEAD revision number
	 * @throws GeneratorException
	 */
	private int getHeadVersion() throws GeneratorException {
		String repository = getConfiguration().get(CONF_REPOSITORY, 0);
		String[] svnCommand = new String[]{
				"svn log -q -r HEAD " + repository + " > svn.log"};
		ScriptLauncher launcher = new ScriptLauncher();
		int svnRetval;
		try {
			svnRetval = launcher.runShellScript(svnCommand, new ScriptEnvironment());
		} catch (ScriptException e) {
			throw new GeneratorException("Error executing SVN command", e);
		}
		if (svnRetval != 0){
			throw new GeneratorException("Error executing SVN command");
		}
		int version = 0;
		try {
			FileReader reader = new FileReader(new File(
					launcher.getTempDirectory()+File.separator+"svn.log"));
			/* file format:
			 * ------------------------------------------------------------------------
			 * r578 | user | date & time
			 * ------------------------------------------------------------------------
			 */
			int i;
			while ((i = reader.read()) != -1){
				if (i == 'r') break;
			}
			StringBuilder str = new StringBuilder();
			while ((i = reader.read()) != -1){
				str.append((char)i);
				if (i < '0' || i > '9') break;
				version = 10*version + (i - '0');
			}
			reader.close();
		} catch (IOException e) {
			throw new GeneratorException("Couldn't get the number of HEAD revision", e);
		}

		return version;
	}


	/**
	 * Loads the data about created binaries from the ResultsRepository.
	 * 
	 * @return The build data
	 * @throws GeneratorException
	 */
	private BinaryData[] loadBinaryData() throws GeneratorException {
		// Load the build information from the RR
		RRDataInterface rr;
		List<DataHandleTuple> rrData;
		try {
			rr = (RRDataInterface)Task.getTaskHandle().getTasksPort().
					serviceFind(ResultsRepositoryService.SERVICE_NAME,
								ResultsRepositoryService.RMI_MAIN_IFACE);
			rrData = rr.loadData(getAnalysis().getName(), DATASET_BINARY,
					new AlwaysTrueCondition(),
					0L, Long.MAX_VALUE);
		} catch (RemoteException e) {
			throw new GeneratorException(e);
		} catch (ResultsRepositoryException e) {
			throw new GeneratorException(e);
		}

		ArrayList<BinaryData> result = new ArrayList<BinaryData>();
		for (DataHandleTuple tuple : rrData) {
			try {
				result.add(new BinaryData(tuple));
			} catch (DataHandleException e) {
				throw new GeneratorException(e);
			}
		}
		return result.toArray(new BinaryData[0]);
	}

	/**
	 * Generates download and build work tasks.
	 * Plans tasks that download and build the server binary. The binary is then
	 * saved to the results repository.
	 * 
	 * @param version Which version should be downloaded and built
	 * @param tasks The tasks sequence where the tasks will be appended
	 */
	private void generateBuild(int version, List<TaskDescriptor> tasks) {
		Condition buildHost = null;
		try {
			buildHost = ParserWrapper.parseString(getConfiguration().get(CONF_BUILD_RSL,0));
		} catch (ParseException e) {
			assert false;  // checked when validating configuration
		}

		TaskDescriptor buildTask = createTask(
				"szn-hintserver-downloadbuild",
				buildHost,
				"build");
		TaskDescriptorHelper.addTaskProperties(
				buildTask,
				Pair.pair("repository", getConfiguration().get(CONF_REPOSITORY,0)),
				Pair.pair("REVISION", version)
		);
		tasks.add(buildTask);

		TaskDescriptor uploadTask = createTask(
				"szn-hintserver-binupload",
				null,  // Set later on
				"binUpload");
		TaskDescriptorHelper.addTaskProperties(
				uploadTask,
				Pair.pair("bin.filename", "/tmp/szn-novinky-hintserversearch-"+version), // TODO check the binary filename
				Pair.pair("version", version),
				Pair.pair("analysis", getAnalysis().getName())
		);
		uploadTask.getHostRuntimes().setAsTask(buildTask.getTaskId());  // I just hope this works
		TaskDescriptorHelper.addDependencyCheckpoint(uploadTask, buildTask.getTaskId(), Task.CHECKPOINT_NAME_FINISHED);
		tasks.add(uploadTask);

	}

	/**
	 * Generates tasks for single run of the benchmark.
	 * Plans tasks to deploy and run server, run given number of clients,
	 * collect results and then clean up the deployed server. Clients are bash
	 * scripts so they don't need cleaning up.
	 *  
	 * @param binId Identification of the binary which will be ran
	 * @param tasks The tasks sequence where the tasks will be appended
	 */
	private void generateRun(UUID binId, List<TaskDescriptor> tasks) {

		// setup the RPS sequence parameters
		int rpsMax  = Integer.decode(getConfiguration().get(CONF_RPS_MAX, 0));
		int rpsStep = Integer.decode(getConfiguration().get(CONF_RPS_STEP, 0));
		int burstCount = (int) Math.ceil(rpsMax / (2 * rpsStep))+1; // start right bellow rpsMax/2
		int burstDelay = Integer.decode(getConfiguration().get(CONF_BURST_DELAY, 0));
		int burstTime =  Integer.decode(getConfiguration().get(CONF_BURST_TIME, 0));
		int server_run_time = burstCount*(burstTime+burstDelay)+2*BURST_START_DELAY;

		// TODO: Create randomized test sequence
		StringBuilder testSequence = new StringBuilder();
		for (int i = burstCount-1; i >= 0; i--) {
			testSequence.append(rpsMax-(i*rpsStep));
			testSequence.append(' ');
		}

		// server host
		Condition serverHost = null;
		try {
			serverHost = ParserWrapper.parseString(getConfiguration().get(CONF_SERVER_RSL, 0));
		} catch (ParseException e) {
			assert false : "Server host is not a RSL expression"; // checked when validating configuration
		}

		// Deploy server task
		TaskDescriptor deployTask = createTask(
				"szn-hintserver-deploy",
				serverHost,
				"deployServer");
		TaskDescriptorHelper.addTaskProperties(
				deployTask,
				Pair.pair("binary", binId)
		);
		deployTask.setExclusive(TaskExclusivity.EXCLUSIVE);
		tasks.add(deployTask);

		// Server stopper task
		TaskDescriptor stopServerTask = createTask(
				"szn-hintserver-stop",
				serverHost,
				"stopServer");
		TaskDescriptorHelper.addTaskProperties(
				stopServerTask,
				Pair.pair("TIME", server_run_time)  // It will terminate the server after 20 minutes
		);
		TaskDescriptorHelper.addDependencyCheckpoint(
				stopServerTask,
				deployTask.getTaskId(),
				Task.CHECKPOINT_NAME_FINISHED); // server stopper will start after the deploy task finishes
		stopServerTask.setExclusive(TaskExclusivity.EXCLUSIVE);
		tasks.add(stopServerTask);

		// Client hosts
		Condition clientHost = null;
		try {
			clientHost = ParserWrapper.parseString(getConfiguration().get(CONF_CLIENT_RSL, 0));
		} catch (ParseException e) {
			assert false : "Client host is not a RSL expression"; // checked when validating configuration
		}
		// Client tasks
		int clientCount = Integer.decode(getConfiguration().get(CONF_CLIENT_COUNT, 0));
		for (int i = 0; i < clientCount; i++) {
			TaskDescriptor clientTask = createTask(
					"szn-hintserver-client",
					clientHost,
					"client"+i);
			// TODO: Check the properties of this task
			TaskDescriptorHelper.addTaskProperties(
					clientTask,
					Pair.pair("period", burstTime),
					Pair.pair("delay", burstDelay),
					Pair.pair("test.sequence", testSequence)
			);
			TaskDescriptorHelper.addDependencyCheckpoint(
					clientTask,
					stopServerTask.getTaskId(),
					Task.CHECKPOINT_NAME_STARTED); // clients will start after the stopper task starts
			clientTask.setExclusive(TaskExclusivity.EXCLUSIVE);
			tasks.add(clientTask);
		}

		// Collect results task
		TaskDescriptor resultsTask = createTask(
				"szn-hintserver-logupload",
				serverHost,
				"logUpload");
		TaskDescriptorHelper.addTaskProperties(
				resultsTask,
				Pair.pair("log.filename",
					"/www/novinky/hintserversearch/log/hintserversearch-dbg_log"),
				Pair.pair("analysis", getAnalysis().getName()),
				Pair.pair("binary", binId),
				Pair.pair("client.count", getConfiguration().get(CONF_CLIENT_COUNT,0)),
				Pair.pair("rps.sequence", testSequence)
		);
		TaskDescriptorHelper.addDependencyCheckpoint(
				resultsTask,
				stopServerTask.getTaskId(),
				Task.CHECKPOINT_NAME_FINISHED); // results will be collected after the stopper finishes
		tasks.add(resultsTask);

		// cleanup task
		TaskDescriptor cleanupTask = createTask(
				"szn-hintserver-cleanup",
				serverHost,
				"cleanup");
		TaskDescriptorHelper.addDependencyCheckpoint(
				cleanupTask,
				resultsTask.getTaskId(),
				Task.CHECKPOINT_NAME_FINISHED);  // cleanup will be performed after the log file is saved
		tasks.add(cleanupTask);
	}


	/**
	 * Checks whether given configuration parameter is set and has integer value.
	 * 
	 * @param configuration The configuration object
	 * @param propName The property name
	 * @param errors Error message storage
	 */
	private void checkIsInt(Configuration configuration, String propName, Collection<String> errors) {
		String propValue = configuration.get(propName, 0);
		if (propValue == null || propValue.isEmpty()) {
			errors.add("Configuration parameter '"+propName+"' is missing");
		} else if (!propValue.matches("^\\d+$")) {
			errors.add("Configuration parameter '"+propName+"' is not a number");
		} 
	}


	/**
	 * Checks whether given configuration parameter is set and is a valid RSL condition.
	 * 
	 * @param configuration The configuration object
	 * @param propName The property name
	 * @param errors Error message storage
	 */
	private void checkIsRSL(Configuration configuration, String propName, ArrayList<String> errors) {
		String propValue = configuration.get(propName, 0);
		if (propValue == null || propValue.isEmpty()) {
			errors.add("Configuration parameter '"+propName+"' is missing");
		} else {
			try {
				ParserWrapper.parseString(propValue);
			} catch (ParseException e) {
				errors.add("Configuration parameter '"+propName+"' is not a valid RSL expression");
			}
		} 
	}

	private void logInfo(String message){
		Task task = Task.getTaskHandle();
		if (task != null) {
			task.logInfo(message);
		}
	}
}
