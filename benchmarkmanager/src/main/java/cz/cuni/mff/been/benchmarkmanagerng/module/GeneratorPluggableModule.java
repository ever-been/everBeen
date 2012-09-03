/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Jiri Tauber
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package cz.cuni.mff.been.benchmarkmanagerng.module;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import cz.cuni.mff.been.benchmarkmanagerng.Analysis;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerImplementation;
import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.common.rsl.Condition;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryService;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;

/**
 * Base class for all generator pluggable modules. This class contains basic
 * helper methods that most generators will use
 * 
 * @author Jiri Tauber
 */
public abstract class GeneratorPluggableModule extends PluggableModule
		implements GeneratorInterface {

	private Analysis analysis = null;

	/** Place for storing valid configuration information */
	private Configuration configuration = null;

	private String treePrefix = "/analyses/";
	private String contextID = "system";

	// ------------------------------------------------------------//
	public GeneratorPluggableModule(PluggableModuleManager manager) {
		super(manager);
	}

	public Analysis getAnalysis() {
		return analysis;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorInterface#configure
	 * (cz.cuni.mff.been.benchmarkmanagerng.Analysis)
	 */
	@Override
	public void configure(Analysis analysis) throws ConfigurationException {
		if (analysis == null) {
			throw new ConfigurationException("Cannot configure null analysis");
		}
		this.analysis = analysis;
		this.configuration = analysis.getGenerator().getConfiguration();
		Collection<String> errors = validateConfiguration(this.configuration);
		if (errors.size() > 0) {
			this.configuration = null;
			throw new ConfigurationException(
					"There is an error in configuration: "
							+ errors.iterator().next());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorInterface#createDatasets
	 * ()
	 */
	@Override
	public void createDatasets() throws ResultsRepositoryException,
			GeneratorException, ConfigurationException {
		if (configuration == null || analysis == null) {
			throw new ConfigurationException(
					"Invalid configuration - call configure() first");
		}

		Map<String, DatasetDescriptor> datasets = doCreateDatasets();
		if (datasets == null || datasets.isEmpty()) {
			return;
		}

		String name = null;
		try {
			RRManagerInterface rr = getRRManagerInterface();
			if (rr == null) {
				throw new GeneratorException(
						"Results Repository reference cannot be obtained. Maybe it is not running.");
			}
			for (String datasetName : datasets.keySet()) {
				name = datasetName;
				rr.createDataset(
						analysis.getName(),
						datasetName,
						datasets.get(datasetName));
				CurrentTaskSingleton.getTaskHandle().logInfo(
						"created dataset '" + datasetName + "'");
			}
		} catch (RemoteException e) {
			throw new ResultsRepositoryException(
					"Error connecting to results repository.",
					e);
		} catch (ResultsRepositoryException e) {
			CurrentTaskSingleton.getTaskHandle().logError(
					"Error creating dataset '" + name + "'");
			throw e;
		}
	}

	/**
	 * Simple generator method with some basic preparations. It guesses the
	 * treePathPrefix and sets it to some reasonable value. New context is
	 * created in the TaskManager. After this, doGenerate() is called to return
	 * the task descriptors. You should override either this method or
	 * <code>doGenerate()</code> in your own generator.
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorInterface#generate()
	 *      GeneratorInterface.generate()
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorPluggableModule#doGenerate()
	 *      doGenerate()
	 */
	@Override
	public Collection<TaskDescriptor> generate() throws ConfigurationException,
			GeneratorException {
		if (getConfiguration() == null || getAnalysis() == null) {
			throw new ConfigurationException(
					"Invalid configuration - call configure() first");
		}

		// Set the context to the current one:
		setContextID(CurrentTaskSingleton.getTaskHandle().getTaskDescriptor()
				.getContextId());

		// Set the tree path prefix:
		StringBuilder path = new StringBuilder();
		path.append(BenchmarkManagerImplementation.ANALYSES_TREEPATH_PREFIX);
		path.append("/");
		path.append(getAnalysis().getName());
		path.append("/");
		path.append(CurrentTaskSingleton.getTaskHandle().getTaskDescriptor()
				.getContextId());
		path.append("/");
		setTreePrefix(path.toString());

		// Return the task list
		Collection<TaskDescriptor> tasks = doGenerate();
		if (tasks == null) {
			tasks = new LinkedList<TaskDescriptor>();
		}
		return tasks;
	}

	/**
	 * Validates the common parts of generator configuration and calls
	 * <code>doValidateConfiguration()</code> for the rest. The common part is
	 * currently only <b>treePathPrefix</b>.
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.ModuleInterface#validateConfiguration(cz.cuni.mff.been.benchmarkmanagerng.Configuration)
	 *      ModuleInterface.validateConfiguration(Configuration)
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorPluggableModule#doValidateConfiguration(cz.cuni.mff.been.benchmarkmanagerng.Configuration)
	 *      doValidateConfiguration(Configuration)
	 */
	@Override
	public Collection<String>
			validateConfiguration(Configuration configuration) {
		ArrayList<String> errors = new ArrayList<String>();

		Collection<String> other = doValidateConfiguration(configuration);
		if (other != null) {
			errors.addAll(other);
		}

		return errors;
	}

	// ----- protected methods -------------------------------------------//
	/**
	 * Validates the generator-specific part of configuration.
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.ModuleInterface#validateConfiguration(cz.cuni.mff.been.benchmarkmanagerng.Configuration)
	 *      validateConfiguration(Configuration)
	 * @param configuration
	 * @return List of errors found in the configuration (both null and empty
	 *         collection are valid empty results)
	 */
	protected abstract Collection<String> doValidateConfiguration(
			Configuration configuration);

	/**
	 * Actually creates the dataset descriptors for the analysis.
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorInterface#createDatasets()
	 *      createDatasets()
	 * @return Mappings of used datasetName - datasetDescriptor (both empty map
	 *         and null are valid empty results)
	 * @throws PluggableModuleException
	 *             when something goes wrong
	 */
	protected abstract Map<String, DatasetDescriptor> doCreateDatasets()
			throws ConfigurationException, GeneratorException;

	/**
	 * Does the simple job of just creating task descriptors inside the
	 * <code>generate()</code> method. You should override this or
	 * <code>generate()</code> in your generator.
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorPluggableModule#generate()
	 *      GeneratorPluggableModule.generate()
	 * @return list of tasks (can be <code>null</code>)
	 * @throws PluggableModuleException
	 */
	protected abstract Collection<TaskDescriptor> doGenerate()
			throws ConfigurationException, GeneratorException;

	/**
	 * Sets the tree prefix for generated tasks. Usually you don't need to worry
	 * about this because it's set in <code>configure</code> function.
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorPluggableModule#createTask(String,
	 *      Condition, String) createTask(String, Condition, String).
	 * @param treePrefix
	 */
	protected void setTreePrefix(String treePrefix) {
		this.treePrefix = treePrefix;
	}

	protected String getTreePrefix() {
		return treePrefix;
	}

	/**
	 * Sets the context for generated tasks. Creates the context in task manager
	 * if the task manager doesn't know it already. Usually you don't need to
	 * worry about this because it's set in <code>configure</code> function.
	 * 
	 * @param contextID
	 * @throws PluggableModuleException
	 *             when anything goes wrong (
	 */
	protected void setContextID(String contextID) throws GeneratorException {
		try {
			TaskManagerInterface manager = CurrentTaskSingleton.getTaskHandle()
					.getTasksPort().getTaskManager();
			if (!manager.isContextRegistered(contextID)) {
				String description = "Context generated for analysis "
						+ analysis.getName() + " during run #"
						+ analysis.getRunCount();
				manager.newContext(contextID, contextID, description, contextID);
			}
		} catch (RemoteException e) {
			throw new GeneratorException("Couldn't change context to: "
					+ contextID, e);
		} catch (LogStorageException e) {
			throw new GeneratorException("Couldn't change context to: "
					+ contextID, e);
		} catch (IllegalArgumentException e) {
			throw new GeneratorException("Couldn't change context to: "
					+ contextID, e);
		} catch (NullPointerException e) {
			throw new GeneratorException("Couldn't change context to: "
					+ contextID, e);
		}
		this.contextID = contextID;
	}

	protected String getContextID() {
		return contextID;
	}

	/**
	 * Creates a new task descriptor. <code>ContextID</code> and
	 * <code>treePrefix</code> should be set before call to this function
	 * because their values are used when creating the new task descriptor.
	 * 
	 * @param taskName
	 *            Name of the new task
	 * @param hostRSL
	 *            Restriction on host for the task
	 * @param treeSuffix
	 *            Suffix for the task tree is appended behind treePrefix
	 * @return The new task descriptor
	 */
	protected TaskDescriptor createTask(
			String taskName,
			Condition hostRSL,
			String treeSuffix) {
		String tid;
		try {
			tid = CurrentTaskSingleton.getTaskHandle().getTasksPort()
					.getTaskManager().getUniqueTaskID();
		} catch (RemoteException e) {
			CurrentTaskSingleton.getTaskHandle().logWarning(
					"Couldn't get UTID from Task manager, using UUID instead");
			tid = UUID.randomUUID().toString();
		}
		return TaskDescriptorHelper.createTask(
				tid,
				taskName,
				contextID,
				hostRSL,
				treePrefix + treeSuffix + "/" + tid);
	}

	/**
	 * Shortcut for creating success dependency checkpoint in a task
	 * 
	 * @param taskDescriptor
	 *            Task descriptor to modify.
	 * @param taskId
	 *            task ID of the task which is the dependency.
	 */
	protected static void success(TaskDescriptor taskDescriptor, String taskId) {
		try {
			TaskDescriptorHelper.addDependencyCheckpoint(
					taskDescriptor,
					taskId,
					Task.CHECKPOINT_NAME_FINISHED,
					Task.EXIT_CODE_SUCCESS);
		} catch (IOException e) {
			throw new IllegalStateException(
					"Serialization of Integer failed.",
					e); // This must not happen.
		}
	}

	/**
	 * Shortcut for creating failure dependency checkpoint in a task
	 * 
	 * @param taskDescriptor
	 *            Task descriptor to modify.
	 * @param taskId
	 *            task ID of the task which is the dependency.
	 */
	protected static void error(TaskDescriptor taskDescriptor, String taskId) {
		try {
			TaskDescriptorHelper.addDependencyCheckpoint(
					taskDescriptor,
					taskId,
					Task.CHECKPOINT_NAME_FINISHED,
					Task.EXIT_CODE_ERROR);
		} catch (IOException e) {
			throw new IllegalStateException(
					"Serialization of Integer failed.",
					e); // This must not happen.
		}
	}

	/**
	 * Retrieves the RMI reference to the Results repository .
	 * 
	 * @return Software Repository Interface
	 */
	protected RRManagerInterface getRRManagerInterface() throws RemoteException {
		RRManagerInterface rr = (RRManagerInterface) CurrentTaskSingleton
				.getTaskHandle()
				.getTasksPort()
				.serviceFind(
						ResultsRepositoryService.SERVICE_NAME,
						ResultsRepositoryService.RMI_MAIN_IFACE);
		return rr;
	}

	// ------------------------------------------------------------//
	/**
	 * Checks integer configuration property.
	 * 
	 * @param fieldName
	 *            Name of the configuration field used in reported errors
	 * @param fieldValue
	 *            Typically value of configuration.get(fieldName) - the value to
	 *            be checked
	 * @param min
	 *            Minimal allowed value of the checked field (not checked if
	 *            null)
	 * @param max
	 *            Maximal allowed value of the checked field (not checked if
	 *            null)
	 * @return error message
	 */
	protected String checkIntegerValue(
			String fieldName,
			String fieldValue,
			int min,
			int max) {
		if (fieldValue == null) {
			return "Configuration parameter '" + fieldName + "' is missing";
		}
		int value = 0;
		try {
			value = Integer.decode(fieldValue);
		} catch (NumberFormatException e) {
			return "Configuration parameter '" + fieldName
					+ "' is not an integer";
		}
		if (value < min) {
			return "Configuration parameter '" + fieldName
					+ "' can't be less than " + min;
		}
		if (value > max) {
			return "Configuration parameter '" + fieldName
					+ "' can't be more than " + max;
		}
		return null;
	}

}
