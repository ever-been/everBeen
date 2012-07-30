/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
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
package cz.cuni.mff.been.benchmarkmanagerng.module.evaluator.simpletest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.been.benchmarkmanagerng.Analysis;
import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorPluggableModule;
import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.RRTrigger;
import cz.cuni.mff.been.resultsrepositoryng.condition.Restrictions;
import cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.CommonEvaluatorProperties;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper;

/**
 * @author Jiri Tauber
 */
public class SimpleTestEvaluator extends EvaluatorPluggableModule {

	public static final String CONFIG_DATASET_NAME = "datasetName";

	/**
	 * @param manager
	 */
	public SimpleTestEvaluator(PluggableModuleManager manager) {
		super(manager);
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorPluggableModule#doCreateDatasets(cz.cuni.mff.been.benchmarkmanagerng.Configuration)
	 */
	@Override
	protected Map<String, DatasetDescriptor> doCreateDatasets(Configuration configuration) {
		// no datasets
		return null;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorPluggableModule#doCreateTriggers()
	 */
	@Override
	protected List<RRTrigger> doCreateTriggers() {
		List<RRTrigger> triggers = new LinkedList<RRTrigger>();

		String analysisName = getAnalysis().getName();
		String datasetName = getConfiguration().get(CONFIG_DATASET_NAME, 0);

		TaskDescriptor descriptor = TaskDescriptorHelper.createTask(
				"SimpleEvaluator",
				"simpletest-evaluator-task",
				getAnalysis().getEvaluatorContext(),
				Analysis.DEFAULT_HOST_RSL,
				getTaskTreePrefix()+"/simpletest"
			);
		TaskDescriptorHelper.addTaskProperties(
			descriptor,
			Pair.pair(CommonEvaluatorProperties.ANALYSIS_NAME, analysisName),
			Pair.pair(CommonEvaluatorProperties.DATASET_NAME, datasetName)
		);		
		RRTrigger trigger = new RRTrigger(
				analysisName,
				datasetName,
				"SimpleTest",
				Restrictions.alwaysTrue(),
				descriptor
			);
		triggers.add(trigger);

		return triggers;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorPluggableModule#doValidateConfiguration(cz.cuni.mff.been.benchmarkmanagerng.Configuration)
	 */
	@Override
	protected Collection<String> doValidateConfiguration(Configuration configuration) {
		ArrayList<String> errors = new ArrayList<String>();

		if( configuration.get(CONFIG_DATASET_NAME,0) == null ){
			errors.add("Configuration parameter '"+CONFIG_DATASET_NAME+"' is missing.");
		}

		return errors;
	}

}
