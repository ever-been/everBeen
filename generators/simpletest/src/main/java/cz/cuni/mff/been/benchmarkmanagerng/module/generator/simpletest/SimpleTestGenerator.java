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
package cz.cuni.mff.been.benchmarkmanagerng.module.generator.simpletest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.been.benchmarkmanagerng.Analysis;
import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorException;
import cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorPluggableModule;
import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper;

/**
 * Simple generator pluggable module for BEEN benchmark manager.
 * Curently can generate tasks for two types of analysis - basic and extended
 *
 * @see cz.cuni.mff.been.benchmarkmanagerng.module.generator.simpletest.SimpleTestGenerator#generateBasic() generateBasic()
 * @see cz.cuni.mff.been.benchmarkmanagerng.module.generator.simpletest.SimpleTestGenerator#generateExtended() generateExtended()
 * @author Jiří Täuber
 */
public final class SimpleTestGenerator extends GeneratorPluggableModule {

	
	
	public static final String TYPE_BASIC = "basic";
	public static final String TYPE_EXTENDED = "extended";

	public static final String CONFIG_TYPE = "type";
	public static final String CONFIG_WAIT_TIME = "waitTime";

	public static final String CONFIG_DATASET_NAME = "datasetName";
	public static final String CONFIG_RANDOM_COUNT = "randomCount";
	public static final String CONFIG_RANDOM_MAX = "randomMax";
	/**
	 * @param manager
	 */
	public SimpleTestGenerator(PluggableModuleManager manager) {
		super(manager);
	}


	@Override
	protected Collection<String> doValidateConfiguration(Configuration configuration) {
		ArrayList<String> errors = new ArrayList<String>();

		// Analysis type - basic or extended
		if( configuration.get(CONFIG_TYPE,0) == null ){
			errors.add("Configuration parameter '"+CONFIG_TYPE+"' is missing");
		} 

		// Basic test parameters - no Repository, only waiting jobs
		else if( TYPE_BASIC.equals(configuration.get(CONFIG_TYPE,0)) ){
			// time to wait
			try {
				Integer waitTime = Integer.decode(configuration.get(CONFIG_WAIT_TIME,0));
				if( waitTime < 0 ){
					errors.add("Configuration parameter '"+CONFIG_WAIT_TIME+"' can't be negative");
				}
			} catch (NumberFormatException e) {
				errors.add("Configuration parameter '"+CONFIG_WAIT_TIME+"' is not an integer");
			}
		}

		// Extended test parameters - no waiting, only RR work
		else if( TYPE_EXTENDED.equals(configuration.get(CONFIG_TYPE,0)) ){
			if( configuration.get(CONFIG_DATASET_NAME,0) == null ){
				errors.add("Configuration parameter '"+CONFIG_DATASET_NAME+"' is missing.");
			}
			String err = null;

			// Optional components (check for null first)
			String field = CONFIG_RANDOM_COUNT;
			String value = configuration.get(field,0);
			if( value != null ){
				err = checkIntegerValue(field, value, 1, Integer.MAX_VALUE);
				if( err != null ) errors.add(err);
			}

			field = CONFIG_RANDOM_MAX;
			value = configuration.get(field,0);
			if( value != null ){
				err = checkIntegerValue(field, value, 1, Integer.MAX_VALUE);
				if( err != null ) errors.add(err);
			}
		}

		// none of the above
		else {
			errors.add("Configuration parameter '"+CONFIG_TYPE+"' is invalid ("+configuration.get(CONFIG_TYPE,0)+")");
		}

		return errors;
	}


	@Override
	protected Map<String, DatasetDescriptor> doCreateDatasets(){
		Map<String, DatasetDescriptor> descriptors = new HashMap<String, DatasetDescriptor>();


		if( !TYPE_BASIC.equals(getConfiguration().get(CONFIG_TYPE,0)) ){
			DatasetDescriptor mainDataset = new DatasetDescriptor();
			mainDataset.put("dateTime", DataHandle.DataType.STRING, true);
			mainDataset.put("data", DataHandle.DataType.FILE, false);
			descriptors.put(getConfiguration().get(CONFIG_DATASET_NAME,0),mainDataset);
		}

		return descriptors;
	}


	@Override
	protected Collection<TaskDescriptor> doGenerate() throws GeneratorException {
		if( TYPE_BASIC.equals(getConfiguration().get(CONFIG_TYPE,0)) ){
			try {
				return generateBasic();
			} catch (IOException e) {
				throw new GeneratorException(e);
			}
		} else {
			return generateExtended();
		}
	}


	//----- private methods ---------------------------------------------//
	/**
	 * Called from <code>generate()</code> when <i>extended</i> type of analysis is chosen.
	 * Creates a task that sends random data to the RR.
	 * Dataset name is part of configuration in this case.
	 * 
	 * @throws PluggableModuleException
	 */

	private Collection<TaskDescriptor> generateExtended() {
		List<TaskDescriptor> tasks = new ArrayList<TaskDescriptor>();
		
		TaskDescriptor task1 = createTask("testworker", Analysis.DEFAULT_HOST_RSL, "TestTask");
		TaskDescriptorHelper.addTaskProperties(
			task1,
			Pair.pair("do.generate.random", "true"),
			Pair.pair("dataset.name", getConfiguration().get(CONFIG_DATASET_NAME,0)),
			Pair.pair("analysis.name", getAnalysis().getName()),
			// optional parameters (set to default values):
			Pair.pair("random.max", "100"),
			Pair.pair("random.count", "1000")
		);
		tasks.add(task1);
		
		return tasks;
	}


	/**
	 * Called from <code>generate()</code> when <i>basic</i> type of analysis is chosen.
	 * Creates 4 tasks that wait for each other.
	 * It doesn't use RR for storing results as the test doesn't
	 * generate any results.
	 * 
	 * @throws java.io.IOException
	 */

	private Collection<TaskDescriptor> generateBasic() throws IOException {
		List<TaskDescriptor> tasks = new ArrayList<TaskDescriptor>();

		TaskDescriptor task1 = createTask("testworker", Analysis.DEFAULT_HOST_RSL, "TestTask1");
		TaskDescriptorHelper.addTaskProperties(
			task1,
			Pair.pair("do.property.value", "true"),
			Pair.pair("property.name", "test.property"),
			Pair.pair("do.checkpoint.set", "true"),
			Pair.pair("checkpoint.name", "data")
		);
		TaskDescriptorHelper.addTaskPropertyObjects(
				task1,
				Pair.pair("test.property", new String[] {"Hello", "World"})
		);
		tasks.add(task1);

		TaskDescriptor taskSleeper = createTask("testworker", Analysis.DEFAULT_HOST_RSL, "TestSleeper");
		TaskDescriptorHelper.addTaskProperties(
			taskSleeper,
			Pair.pair("do.wait", "true"),
			Pair.pair("wait.time", getConfiguration().get("waitTime",0)),
			Pair.pair("do.checkpoint.value", "true"),
			Pair.pair("checkpoint.name", "data"),
			Pair.pair("checkpoint.task", task1.getTaskId())
		);
		success(taskSleeper, task1.getTaskId());
		tasks.add(taskSleeper);

		TaskDescriptor task3 = createTask("testworker", Analysis.DEFAULT_HOST_RSL, "TestTask3");
		TaskDescriptorHelper.addTaskProperties(
			task3,
			Pair.pair("do.checkpoint.set", "true"),
			Pair.pair("checkpoint.name", "go")
		);
		success(task3, taskSleeper.getTaskId());
		tasks.add(task3);

		TaskDescriptor taskWaiter = createTask("testworker", Analysis.DEFAULT_HOST_RSL, "TestWaiter");
		TaskDescriptorHelper.addTaskProperties(
			taskWaiter,
			Pair.pair("do.checkpoint.block", "true"),
			Pair.pair("checkpoint.name", "go"),
			Pair.pair("checkpoint.task", task3.getTaskId())
		);
		tasks.add(taskWaiter);
		
		return tasks;

	}

}
