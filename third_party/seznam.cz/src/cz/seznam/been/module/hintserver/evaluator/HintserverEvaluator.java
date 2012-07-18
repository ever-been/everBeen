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
package cz.seznam.been.module.hintserver.evaluator;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorPluggableModule;
import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.common.rsl.Condition;
import cz.cuni.mff.been.common.rsl.ParseException;
import cz.cuni.mff.been.common.rsl.ParserWrapper;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.RRTrigger;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.condition.Restrictions;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.CommonEvaluatorProperties;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper;

/**
 * Evaluator pluggable module for evaluating measured request-per-second data
 * on hint server in Seznam.cz
 *
 * @author Jiri Tauber
 */
public class HintserverEvaluator extends EvaluatorPluggableModule {

	/** Reasonably unique evaluator identification for use in triggers */
	private static final String EVALUATOR_NAME = "Hintserver";

	/** Dataset where the generated data whould be found */
	private static final String INPUT_DATASET = "server_log";  // the same as in generator

	/** Mid-point dataset where the preparsed data of each run are stored */
	private static final String OUTPUT_DATASET = "parsed_rps";
	/** A tag name in the evaluator dataset */
	private static final String OUTPUT_TAG_RUN = "run_id";
	/** A tag name in the evaluator dataset */
	private static final String OUTPUT_TAG_REQUESTS = "rps_requested";
	/** A tag name in the evaluator dataset */
	private static final String OUTPUT_TAG_RPS_SERVED = "rps_served";

	/** Name of the parser task */
	private static final String TASK_LOGPARSER = "szn-hintserver-logparser";
	/** Name of the aggregation task */
	private static final String TASK_RPS_GRAPHER = "szn-hintserver-rpsgrapher";

	/** Both Parser task and Grapher task property name */
	private static final String TASKPROP_GRAPH_DESTINATION = "graph.destination";  // copy in LogParser and RpsGrapher

	/** Configuration parameter - where to run evaluators */
	private static final String CONF_HOST = "host.name";
	/** Configuration parameter - where to save graphs */
	private static final String CONF_WEB_ROOT = "web.root";

	//------------------------------------------------------------------------//
	public HintserverEvaluator(PluggableModuleManager manager) {
		super(manager);
	}


	//------------------------------------------------------------------------//
	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorPluggableModule#doValidateConfiguration(cz.cuni.mff.been.benchmarkmanagerng.Configuration)
	 */
	@Override
	protected Collection<String> doValidateConfiguration(Configuration configuration) {
		ArrayList<String> errors = new ArrayList<String>();

		String propName = CONF_HOST;
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

		return errors;
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorPluggableModule#doCreateDatasets(cz.cuni.mff.been.benchmarkmanagerng.Configuration)
	 */
	@Override
	protected Map<String, DatasetDescriptor> doCreateDatasets(Configuration configuration)
	throws RemoteException, ResultsRepositoryException {
		Map<String, DatasetDescriptor> descriptors = new HashMap<String, DatasetDescriptor>();
		DatasetDescriptor dd;

		dd = new DatasetDescriptor();
		dd.put(OUTPUT_TAG_RUN, DataHandle.DataType.INT, true);
		dd.put(OUTPUT_TAG_REQUESTS, DataHandle.DataType.INT, true);
		dd.put(OUTPUT_TAG_RPS_SERVED, DataHandle.DataType.INT, false);
		descriptors.put(OUTPUT_DATASET, dd);

		return descriptors;
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorPluggableModule#doCreateTriggers()
	 */
	@Override
	protected List<RRTrigger> doCreateTriggers() throws RemoteException, ResultsRepositoryException {
		List<RRTrigger> triggers = new LinkedList<RRTrigger>();

		String analysisName = getAnalysis().getName();
		Condition hostRSL = null;
		try {
			hostRSL = ParserWrapper.parseString(getConfiguration().get(CONF_HOST, 0));
		} catch (ParseException e) {
			assert false : CONF_HOST+" value is not a valid RSL"; // was checked before
		}
		String propWebroot = getConfiguration().get(CONF_WEB_ROOT, 0);

		// the log parser trigger
		TaskDescriptor descriptor = TaskDescriptorHelper.createTask(
				"logParser-%s",
				TASK_LOGPARSER,
				getAnalysis().getEvaluatorContext(),
				hostRSL,
				getTaskTreePrefix()+"/logparser-%s"
			);
		TaskDescriptorHelper.addTaskProperties(
			descriptor,
			Pair.pair(CommonEvaluatorProperties.ANALYSIS_NAME, analysisName),
			Pair.pair(CommonEvaluatorProperties.DATASET_NAME, INPUT_DATASET),
			Pair.pair(TASKPROP_GRAPH_DESTINATION, propWebroot)
		);		
		RRTrigger trigger = new RRTrigger(
				analysisName,
				INPUT_DATASET,
				EVALUATOR_NAME,
				Restrictions.alwaysTrue(),
				descriptor
			);
		triggers.add(trigger);

		// the results dataset trigger - draws the final graph
		descriptor = TaskDescriptorHelper.createTask(
				"results-%s",
				TASK_RPS_GRAPHER,
				getAnalysis().getEvaluatorContext(),
				hostRSL,
				getTaskTreePrefix()+"/results-%s"
			);
		TaskDescriptorHelper.addTaskProperties(
			descriptor,
			Pair.pair(CommonEvaluatorProperties.ANALYSIS_NAME, analysisName),
			Pair.pair(CommonEvaluatorProperties.DATASET_NAME, INPUT_DATASET),
			Pair.pair(TASKPROP_GRAPH_DESTINATION, propWebroot)
		);		
		trigger = new RRTrigger(
				analysisName,
				OUTPUT_DATASET,
				EVALUATOR_NAME,
				Restrictions.alwaysTrue(),
				descriptor
			);
		triggers.add(trigger);

		return triggers;
	}

}
