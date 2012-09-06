/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jaroslav Urban
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
package cz.cuni.mff.been.task.test.testworker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorException;
import cz.cuni.mff.been.resultsrepositoryng.RRDataInterface;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryService;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.data.FileDataHandle;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;

/**
 * <p>This task can be set to do various example work. It's used for
 * testing purposes. You have to choose which actions should it perform.
 * It's designed to cooperate with other running <tt>TestWorker</tt> tasks.</p>
 * 
 * <b>Task properties:</b><br>
 * <ul>
 * <li>{@value #DO_CHECKPOINT_VALUE}: <br>
 * 		- if set to "true" or "yes", it retrieves a value of a checkpoint. 
 * 		The value must be of the String[2]
 * 		type. The name of the checkpoint must be specified in the 
 * 		{@value #CHECKPOINT_NAME} task property. Task ID of a task that sets the 
 * 		checkpoint must be specified in the {@value #CHECKPOINT_TASK}
 * 		task property.<br>
 * <li>{@value #DO_CHECKPOINT_SET}: <br>
 * 		- if set to "true" or "yes", it sets a checkpoint with a value. 
 * 		The value will be {"BEEN", "DSRG"}. 
 * 		The name of the checkpoint must be specified in the 
 * 		{@value #CHECKPOINT_NAME} task property.<br>
 * <li>{@value #DO_CHECKPOINT_BLOCK}: <br>
 * 		- if set to "true" or "yes", it retrieves a value of a checkpoint in a 
 * 		similar way as when the {@value #DO_CHECKPOINT_VALUE} task property is 
 * 		set to "true" or "yes", but the query for the checkpoint value is 
 * 		blocking. The {@value #CHECKPOINT_NAME} and {@value #CHECKPOINT_TASK}
 * 		task properties must be also set.<br>
 * <li>{@value #DO_PROPERTY_VALUE}: <br>
 * 		- if set to "true" or "yes", it retrieves the value of a task property. 
 * 		The value must be of the String[2] type. Name of the task property 
 * 		must be specified in the {@value #PROPERTY_NAME} task property.<br>
 * <li>{@value #DO_WAIT}: <br>
 * 		- if set to "true" or "yes", the task sleeps for the ammount of seconds 
 * 		set in the {@value #WAIT_TIME} task property.<br>
 * <li>{@value #WAIT_TIME}: <br>
 * 		- number of seconds to sleep. <br>
 * 		- mandatory if {@value #DO_WAIT} is "true" or "yes".  
 * <li>{@value #DO_GENERATE_RANDOM}: <br>
 * 		- if set to "true" or "yes", it generates a series of random numbers. 
 * 		The {@value #RANDOM_COUNT} task property (default 1000) says how many
 * 		numbers are going to be generated. The {@value #RANDOM_MAX} task
 * 		property (default 100) says what is the maximal value for generated
 * 		numbers. Task generates integer values. The parameters are optional.<br>
 * <li>{@value #ANALYSIS_NAME}: <br>
 * 		- Name of the analysis - used when saving data to the Results Repository.
 * 		Property is mandatory when {@value #DO_GENERATE_RANDOM} is true.<br>
 * <li>{@value #DATASET_NAME}: <br>
 * 		- Name of the dataset - used when saving data to the Results Repository.
 * 		Property is mandatory when {@value #DO_GENERATE_RANDOM} is true.<br>
 * </ul>
 * 
 * @author Jaroslav Urban
 * @author Jiri Tauber
 */
public class TestWorker extends Job {
	/** 
	 * Task property name for a property indicating that getting a checkpoint
	 * value should be done.
	 */
	public static final String DO_CHECKPOINT_VALUE = "do.checkpoint.value";
	/** 
	 * Task property name for a property indicating that setting a checkpoint
	 * value should be done.
	 */
	public static final String DO_CHECKPOINT_SET = "do.checkpoint.set";
	/** 
	 * Task property name for a property indicating that a blocking wait for 
	 * a checkpoint value should be done.
	 */
	public static final String DO_CHECKPOINT_BLOCK = "do.checkpoint.block";
	/**
	 * Name of the checkpoint that should be set/queried.
	 */
	public static final String CHECKPOINT_NAME = "checkpoint.name";
	/**
	 * Task ID of the task that set a checkpoint that will be queried.
	 */
	public static final String CHECKPOINT_TASK = "checkpoint.task";
	/** 
	 * Task property name for a property indicating that getting a task property
	 * value should be done.
	 */
	public static final String DO_PROPERTY_VALUE = "do.property.value";
	/**
	 * Name of the task property which value should be gotten.
	 */
	public static final String PROPERTY_NAME = "property.name";
	/** 
	 * Task property name for a property indicating that sleeping should be done.
	 */
	public static final String DO_WAIT = "do.wait";
	/**
	 * Name of the task property for the sleep time (in seconds).
	 */
	public static final String WAIT_TIME = "wait.time";
	
	/**
	 * Task property name for a property indicating that random generating should be done.
	 */
	public static final String DO_GENERATE_RANDOM = "do.generate.random";
	/**
	 * Name of the task property for the random number count
	 */
	public static final String RANDOM_COUNT = "random.count";
	/**
	 * Name of the task property for the random number max value
	 */
	public static final String RANDOM_MAX = "random.max";
	/**
	 * Name of the task property for the analysis name used for saving data to the RR.
	 */
	public static final String ANALYSIS_NAME = "analysis.name";
	/**
	 * Name of the task property for the dataset name used for saving data to the RR.
	 */
	public static final String DATASET_NAME = "dataset.name";

	/**
	 * 
	 * Allocates a new <code>TestWorker</code> object.
	 *
	 * @throws cz.cuni.mff.been.task.TaskInitializationException
	 */
	public TestWorker() throws TaskInitializationException {
		super();
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.Job#checkRequiredProperties()
	 */
	@Override
	protected void checkRequiredProperties() throws TaskException {
	}


	/**
	 * Gets the value of a checkpoint.
	 * 
	 * @throws cz.cuni.mff.been.task.TaskException
	 */
	private void doCheckpointValue() throws TaskException {
		if (!getBooleanTaskProperty(DO_CHECKPOINT_VALUE)) {
			return;
		}
		logInfo("Getting checkpoint value");

		String name = getTaskProperty(CHECKPOINT_NAME);
		if (name == null) {
			throw new TaskException(CHECKPOINT_NAME + " task property not set");
		}
		logInfo("Checkpoint name: " + name);
		
		String taskID = getTaskProperty(CHECKPOINT_TASK);
		if (taskID == null) {
			throw new TaskException(CHECKPOINT_TASK + " task property not set");
		}
		logInfo("Task ID: " + taskID);
		
		String[] value = (String[]) checkPointWait(null, taskID, name, 0);
		logInfo("Checkpoint value: [" + value[0] + ", " + value[1] + "]");
	}

	/**
	 * Sets the value of a checkpoint.
	 * 
	 * @throws cz.cuni.mff.been.task.TaskException
	 */
	private void doCheckpointSet() throws TaskException {
		if (!getBooleanTaskProperty(DO_CHECKPOINT_SET)) {
			return;
		}
		logInfo("Setting a checkpoint");
		
		String name = getTaskProperty(CHECKPOINT_NAME);
		if (name == null) {
			throw new TaskException(CHECKPOINT_NAME + " task property not set");
		}
		logInfo("Checkpoint name: " + name);
		
		String[] value = {"BEEN", "DSRG"};
		checkPointReached(name, value);
		logInfo("Set checkpoint with value: [" + value[0] + ", " + value[1] + "]");
	}

	/**
	 * Does a blocking wait for a checkpoint.
	 * 
	 * @throws cz.cuni.mff.been.task.TaskException
	 */
	private void doCheckpointBlock() throws TaskException {
		if (!getBooleanTaskProperty(DO_CHECKPOINT_BLOCK)) {
			return;
		}
		
		logInfo("Blocking on waiting for checkpoint value");

		String name = getTaskProperty(CHECKPOINT_NAME);
		if (name == null) {
			throw new TaskException(CHECKPOINT_NAME + " task property not set");
		}
		logInfo("Checkpoint name: " + name);
		
		String taskID = getTaskProperty(CHECKPOINT_TASK);
		if (taskID == null) {
			throw new TaskException(CHECKPOINT_TASK + " task property not set");
		}
		logInfo("Task ID: " + taskID);

		String[] value = (String[]) checkPointWait(null, taskID, name, 
			TaskManagerInterface.INFINITE_TIME);
		logInfo("Checkpoint value: [" + value[0] + ", " + value[1] + "]");
	}
	
	/**
	 * Gets the value of a task property.
	 * 
	 * @throws cz.cuni.mff.been.task.TaskException
	 */
	private void doPropertyValue() throws TaskException {
		if (!getBooleanTaskProperty(DO_PROPERTY_VALUE)) {
			return;
		}
		logInfo("Getting task property object value");

		String name = getTaskProperty(PROPERTY_NAME);
		if (name == null) {
			throw new TaskException(PROPERTY_NAME + " task property not set");
		}
		logInfo("Task property name: " + name);

		
		String[] value = (String[]) getTaskPropertyObject(name);
		if (value == null) {
			throw new TaskException(name + " task property not set");
		}
		if (value.length < 2  || value[0] == null || value[1] == null) {
			throw new TaskException(name + " task property is not array of 2 strings");
		}
		logInfo("Property value: [" + value[0] + ", " + value[1] + "]");
	}

	/**
	 * Sleeps for some time.
	 * 
	 * @throws cz.cuni.mff.been.task.TaskException
	 */
	private void doWait() throws TaskException {
		if (!getBooleanTaskProperty(DO_WAIT)) {
			return;
		}
		
		String time = getTaskProperty(WAIT_TIME);
		if (time == null) {
			throw new TaskException(WAIT_TIME + " task property not set");
		}
		logInfo("Waiting for " + time + " seconds");
		
		try {
			Thread.sleep(Long.valueOf(time) * 1000);
		}
		catch (InterruptedException e) {
			logWarning("Sleep interrupted");
			Thread.currentThread().interrupt();
		}
		
		logInfo("Woke up");
	}

	/**
	 * Generates random numbers and commits them to the ResultsRepositoryng
	 * 
	 * @throws cz.cuni.mff.been.task.TaskException
	 */
	private void doGenerateRandom() throws TaskException {
		if (!getBooleanTaskProperty(DO_GENERATE_RANDOM)) {
			return;
		}

		// Load and check mandatory parameters
		String analysisName = getTaskProperty(ANALYSIS_NAME);
		String datasetName = getTaskProperty(DATASET_NAME);
		if( analysisName == null || datasetName == null ){
			logError(ANALYSIS_NAME+" or "+DATASET_NAME+" parameter is missing.");
			exitError();
		}

		// Load and check optional parameters
		String countProp = getTaskProperty(RANDOM_COUNT, "1000");
		String maxProp = getTaskProperty(RANDOM_MAX, "100");
		int count = Integer.decode(countProp);
		int max = Integer.decode(maxProp);

		// Generate the result
		String fileName = getTempDirectory()+File.separator+"output.tmp";
		logInfo("Generating "+count+" numbers between 0 and "+max+" into file "+fileName);
		FileWriter writer;
		try {
			writer = new FileWriter(fileName);
			for(int i = 0; i < count; i++){
				Integer rnd = (int)Math.round(Math.random()*max);
				writer.write(rnd.toString());
				writer.write(' ');
				System.out.print(rnd);
				System.out.print(' ');
			}
			writer.close();
		} catch (IOException e) {
			throw new TaskException("Couldn't create the output file",e);
		}

		// Prepare tags 
		String dateTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());

		// upload results to RR
		DataHandleTuple tuple = new DataHandleTuple();
		try {
			RRDataInterface repository = (RRDataInterface) getTasksPort().
					serviceFind(ResultsRepositoryService.SERVICE_NAME, Service.RMI_MAIN_IFACE);
			if( repository == null ){
				throw new TaskException("Results Repository reference cannot be obtained. Maybe it is not running.");
			}

			UUID fileId = repository.getFileStoreClient().uploadFile(new File(fileName));
			logInfo("Saved file id: "+fileId.toString());

			tuple.set("dateTime", dateTime);
			tuple.set("data", new FileDataHandle(fileId));

			repository.saveData(analysisName, datasetName, tuple);
		} catch (IOException e) {
			throw new TaskException("Error uploading results file",e);
		} catch (ResultsRepositoryException e) {
			throw new TaskException("Error uploading results to the Results Repository");
		}

		logInfo("Data ("+dateTime+") successfuly saved to the Results Repository");
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.Job#run()
	 */
	@Override
	protected void run() throws TaskException {
		try {
			doCheckpointSet();
			doCheckpointValue();
			doCheckpointBlock();
			doPropertyValue();
			doWait();
			doGenerateRandom();
		} catch(TaskException e){
			if( e.getMessage() != null ) logError(e.getMessage());
			e.printStackTrace();
			exitError();
		}
	}

}
