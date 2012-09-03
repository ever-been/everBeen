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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cz.cuni.mff.been.benchmarkmanagerng.Analysis;
import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface;
import cz.cuni.mff.been.resultsrepositoryng.RRTrigger;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryService;
import cz.cuni.mff.been.task.CurrentTaskSingleton;

/**
 * @author Jiri Tauber
 */
public abstract class EvaluatorPluggableModule extends PluggableModule
		implements EvaluatorInterface {

	public static String REGEXP_JAVA_IDENTIFIER = "[a-zA-Z_][a-zA-Z0-9_]*";

	private Analysis analysis;
	private Configuration configuration;

	// ------------------------------------------------------------//
	public EvaluatorPluggableModule(PluggableModuleManager manager) {
		super(manager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorInterface#
	 * attachToAnalysis(java.lang.String,
	 * cz.cuni.mff.been.benchmarkmanagerng.Configuration)
	 */
	@Override
	public void
			attachToAnalysis(Analysis analysis, Configuration configuration)
					throws RemoteException, ResultsRepositoryException {
		this.analysis = analysis;
		this.configuration = configuration;

		RRManagerInterface rr = getRRManagerInterface();

		Map<String, DatasetDescriptor> datasets = doCreateDatasets(configuration);
		if (datasets == null) {
			datasets = new TreeMap<String, DatasetDescriptor>();
		}
		if (!datasets.isEmpty()) {
			List<String> createdDatasets = new ArrayList<String>();
			try {
				for (String datasetName : datasets.keySet()) {
					rr.createDataset(
							analysis.getName(),
							datasetName,
							datasets.get(datasetName));
					createdDatasets.add(datasetName);
				}
			} catch (ResultsRepositoryException e) {
				deleteDatasets(analysis.getName(), datasets, createdDatasets);
				throw e;
			} catch (RemoteException e) {
				deleteDatasets(analysis.getName(), datasets, createdDatasets);
				throw e;
			}
		}

		List<RRTrigger> triggers = doCreateTriggers();
		if (triggers != null && !triggers.isEmpty()) {
			List<RRTrigger> createdTriggers = new ArrayList<RRTrigger>();
			try {
				for (RRTrigger trigger : triggers) {
					rr.createTrigger(trigger);
					createdTriggers.add(trigger);
				}
			} catch (ResultsRepositoryException e) {
				deleteTriggers(createdTriggers);
				deleteDatasets(analysis.getName(), datasets, datasets.keySet());
				throw e;
			} catch (RemoteException e) {
				deleteTriggers(createdTriggers);
				deleteDatasets(analysis.getName(), datasets, datasets.keySet());
				throw e;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorInterface#
	 * detachFromAnalysis(java.lang.String)
	 */
	@Override
	public void detachFromAnalysis(
			Analysis analysis,
			Configuration configuration) throws RemoteException,
			ResultsRepositoryException {
		this.analysis = analysis;
		this.configuration = configuration;

		Map<String, DatasetDescriptor> datasets = doCreateDatasets(configuration);
		if (datasets != null) {
			deleteDatasets(analysis.getName(), datasets, datasets.keySet());
		}

		List<RRTrigger> triggers = doCreateTriggers();
		if (triggers != null) {
			deleteTriggers(triggers);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.ModuleInterface#
	 * validateConfiguration(cz.cuni.mff.been.benchmarkmanagerng.Configuration)
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

	// ----- Abstract Methods ------------------------------------------------//
	/**
	 * 
	 */
	protected abstract Collection<String> doValidateConfiguration(
			Configuration configuration);

	/**
	 * 
	 */
	protected abstract Map<String, DatasetDescriptor> doCreateDatasets(
			Configuration configuration) throws RemoteException,
			ResultsRepositoryException;

	/**
	 * 
	 */
	protected abstract List<RRTrigger> doCreateTriggers()
			throws RemoteException, ResultsRepositoryException;

	// ----- Final Methods ---------------------------------------------------//

	protected final Analysis getAnalysis() {
		return analysis;
	}

	protected final Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Retrieves the RMI reference to the Results repository .
	 * 
	 * @return Software Repository Interface
	 */
	protected final RRManagerInterface getRRManagerInterface()
			throws RemoteException {
		RRManagerInterface rr = (RRManagerInterface) CurrentTaskSingleton
				.getTaskHandle()
				.getTasksPort()
				.serviceFind(
						ResultsRepositoryService.SERVICE_NAME,
						ResultsRepositoryService.RMI_MAIN_IFACE);
		return rr;
	}

	/**
	 * @param datasets
	 * @param createdDatasets
	 * @throws RemoteException
	 * @throws ResultsRepositoryException
	 */
	protected final void deleteDatasets(
			String analysis,
			Map<String, DatasetDescriptor> datasets,
			Collection<String> createdDatasets) throws RemoteException,
			ResultsRepositoryException {
		if (datasets == null || datasets.size() == 0) {
			return;
		}
		RRManagerInterface rr = getRRManagerInterface();
		for (String dataset : createdDatasets) {
			rr.deleteDataset(analysis, dataset);
		}
	}

	/**
	 * @param triggers
	 * @throws ResultsRepositoryException
	 * @throws RemoteException
	 */
	protected final void deleteTriggers(Collection<RRTrigger> triggers)
			throws RemoteException, ResultsRepositoryException {
		RRManagerInterface rr = getRRManagerInterface();
		for (RRTrigger trigger : triggers) {
			rr.deleteTriggers(
					trigger.getAnalysis(),
					trigger.getDataset(),
					trigger.getEvaluator());
		}
	}

	protected String getTaskTreePrefix() {
		return "/evaluator/" + getAnalysis().getName();
	}
}
