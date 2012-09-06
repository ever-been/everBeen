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
package cz.cuni.mff.been.task.benchmarkmanagerng.evaluator;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import cz.cuni.mff.been.resultsrepositoryng.RRDataInterface;
import cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryService;
import cz.cuni.mff.been.resultsrepositoryng.condition.AlwaysTrueCondition;
import cz.cuni.mff.been.resultsrepositoryng.condition.Condition;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;
import cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.CommonEvaluatorProperties;

/**
 * @author Jiri Tauber
 *
 */
public abstract class EvaluatorTask extends Job {
	
	private UUID triggerId;
	
	private Long lastSerialProcessed;
	
	private String analysisName;
	
	private String datasetName;

	/** RMI reference to results repository */ 
	private RRDataInterface resultsRepository;

	/**
	 * @throws cz.cuni.mff.been.task.TaskInitializationException
	 */
	public EvaluatorTask() throws TaskInitializationException {
		super();
		try {
			resultsRepository = (RRDataInterface)getTasksPort().serviceFind(ResultsRepositoryService.SERVICE_NAME, ResultsRepositoryService.RMI_MAIN_IFACE);
		} catch (RemoteException e) {
			throw new TaskInitializationException(e);
		}
		if( resultsRepository == null ){
			throw new TaskInitializationException("Results Repository reference cannot be obtained.");
		}

		if( resultsRepository == null ){
			throw new TaskInitializationException("Couldn't find ResultsRepository reference");
		}
	}


	/**
	 * Does the job's work, the task ends when this method returns.<br>
	 * Evaluator task has to report success to the Results Repository just before
	 * leaving this method because the trigger that fired it is blocked until
	 * then.<br>
	 * Consider overriding {@link #doRun()} instead of this function.
	 * 
	 * @see cz.cuni.mff.been.task.Job#run()
	 */
	@Override
	protected void run() throws TaskException{
		try{
			doRun();
		} catch (Exception e){
			// just unblock the evaluator trigger
			notifyDataProcessed(lastSerialProcessed);
			logFatal((e.getMessage() != null ? e.getMessage() : "")+" (trigger unblocked)");
			e.printStackTrace();
			exitError();
		}
	}


	/**
	 * Override this method instead of {@link #run()} if you want a failsafe
	 * environment.<br>
	 * Normally when run() method ends with exception, it means that evaluator
	 * didn't have the chance to report any success to the Results Repository.
	 * When using doRun(), Evaluator will always unblock the trigger that fired
	 * it before any exception leaves run().
	 * @throws cz.cuni.mff.been.task.TaskException When something goes wrong.
	 * @see #run()
	 */
	protected void doRun() throws TaskException {}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.Job#checkRequiredProperties()
	 */
	@Override
	protected void checkRequiredProperties() throws TaskException {
		StringBuilder errors = new StringBuilder();

		// Trigger UUID -> not required
		String property = getTaskProperty(CommonEvaluatorProperties.TRIGGER_ID);
		if( property != null ){
			try {
				triggerId = UUID.fromString(property);
			} catch( IllegalArgumentException e ){
				errors.append(CommonEvaluatorProperties.TRIGGER_ID+" is not valid UUID;");
			}
		}

		// Last processed serial number (Long)
		property = getTaskProperty(CommonEvaluatorProperties.LAST_SERIAL_PROCESSED);
		if( property == null ){
			errors.append(CommonEvaluatorProperties.LAST_SERIAL_PROCESSED+" is null;");
		} else {
			try {
				Long number = Long.decode(property);
				lastSerialProcessed = number;
				if( number < 0 ){
					errors.append(CommonEvaluatorProperties.LAST_SERIAL_PROCESSED+" is not valid Long;");
				}
			} catch( NumberFormatException e ){
				errors.append(CommonEvaluatorProperties.LAST_SERIAL_PROCESSED+" is not valid Long;");
			}
		}
		
		analysisName = getTaskProperty(CommonEvaluatorProperties.ANALYSIS_NAME);
		if( analysisName == null ){
			errors.append(CommonEvaluatorProperties.ANALYSIS_NAME+" is null;");
		}
		
		datasetName = getTaskProperty(CommonEvaluatorProperties.DATASET_NAME);
		if( datasetName == null ){
			errors.append(CommonEvaluatorProperties.DATASET_NAME+" is null;");
		}

		if(errors.length() > 0){
			throw new TaskException(errors.toString());
		}

		doCheckRequiredProperties();
	}


	/**
	 * Method called from <code>checkRequiredProperties</code> to check
	 * the task-specific properties.
	 * 
	 * @see cz.cuni.mff.been.task.Job#checkRequiredProperties()
	 */
	protected abstract void doCheckRequiredProperties() throws TaskException;



	//***** Utility methods **************************************************//
	/**
	 * @return Results repository reference
	 */
	protected final RRDataInterface getResultsRepository(){
		return resultsRepository;
	}


	/**
	 * Shortcut method for loadData(condition, last.serial.processed, null)
	 * @see #loadData(Condition, Long, Long)
	 * @param condition Conditon to filter the new data
	 */
	protected List<DataHandleTuple> loadNewData(Condition condition) {
		return loadData(condition, lastSerialProcessed+1, null);
	}


	/**
	 * Shortcut method for loadData(AlwaysTrueCondition(), 0, null)
	 * @see #loadData(Condition, Long, Long)
	 */
	protected List<DataHandleTuple> loadAllData() {
		return loadData(new AlwaysTrueCondition(), null, null);
	}


	/**
	 * Loads data from dataset that is named in {@code CommonEvaluatorProperties.DATASET_NAME} property.
	 * 
	 * @see RRDataInterface#loadData(String, String, Condition, Long, Long)
	 * 
	 * @param condition condition for the loaded data
	 * @param serialFrom which serial to start
	 * @param serialTo which serial to end the data - {@code Long.MAX_VALUE} usually
	 * @return The loaded data or null on error
	 */
	protected List<DataHandleTuple> loadData(Condition condition, Long serialFrom, Long serialTo){
		RRDataInterface repository = getResultsRepository();
		List<DataHandleTuple> result = null;
		try {
			result = repository.loadData(analysisName, datasetName, condition, serialFrom, serialTo);
		} catch (RemoteException e) {
			logError("Couldn't load data from Results Repository");
			e.printStackTrace();
			exitError();
		} catch (ResultsRepositoryException e) {
			logError("Couldn't load data from Results Repository");
			e.printStackTrace();
			exitError();
		}
		return result;
	}


	/**
	 * Notifies the Results Repository about processed data.
	 * Results Repository will not fire the trigger again
	 * until it receives this notification.<br>
	 * This method should be called after all data are loaded and processed.
	 * 
	 * @param highestId The highest processed serial.
	 */
	protected void notifyDataProcessed(long highestId){
		if( triggerId == null ){
			logWarning("Couldn't notify the ResultsRepository about processed data"+" because no trigger ID was given");
		} else try {
			((RRManagerInterface)resultsRepository).notifyDataProcessed(triggerId, highestId);
		} catch (ResultsRepositoryException e) {
			logFatal("Couldn't notify the ResultsRepository about processed data");
			e.printStackTrace();
			exitError();
		} catch (RemoteException e) {
			logFatal("Couldn't notify the ResultsRepository about processed data");
			e.printStackTrace();
			exitError();
		}
	}


	protected UUID getTriggerId() {
		return triggerId;
	}


	protected long getLastSerialProcessed() {
		return lastSerialProcessed;
	}


	protected String getAnalysisName() {
		return analysisName;
	}


	protected String getDatasetName() {
		return datasetName;
	}

}
