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
package cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.simpletest;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import au.com.bytecode.opencsv.CSVReader;

import cz.cuni.mff.been.resultsrepositoryng.RRDataInterface;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.filestore.FileStoreClient;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;
import cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.CommonEvaluatorProperties;
import cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.EvaluatorTask;

/**
 * @author Jiri Tauber
 */
public class SimpleTest extends EvaluatorTask {

	/**
	 * @throws TaskInitializationException
	 */
	public SimpleTest() throws TaskInitializationException {
		super();
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.EvaluatorTask#docheckRequiredProperties()
	 */
	@Override
	protected void doCheckRequiredProperties() throws TaskException {
	}


	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.task.Job#run()
	 */
	@Override
	protected void run() throws TaskException {
		try {
			long highest = doEvaluateRandom();
			notifyDataProcessed(highest);
		} catch(TaskException e){
			if( e.getMessage() != null ) logError(e.getMessage());
			e.printStackTrace();
			exitError();
		}
	}


	/**
	 * Takes results generated by doGenerateRandom
	 * from the RR and evaluates them
	 */
	private long doEvaluateRandom() throws TaskException {
		long highestSerial = 0;
		// Load and check mandatory parameters
		String analysisName = getTaskProperty(CommonEvaluatorProperties.ANALYSIS_NAME);
		String datasetName = getTaskProperty(CommonEvaluatorProperties.DATASET_NAME);
		if( analysisName == null || datasetName == null ){
			throw new TaskException(CommonEvaluatorProperties.ANALYSIS_NAME+" or "+
					CommonEvaluatorProperties.DATASET_NAME+" parameter is missing");
		}

		// Commit the DataHandleTuple to the RRng
		RRDataInterface repository = null;
		FileStoreClient fileClient = null;
		List<DataHandleTuple> result = null;
		try {
			repository = getResultsRepository();
			fileClient = repository.getFileStoreClient();
			result = repository.loadData(analysisName,datasetName, null, null ,null);
		} catch (RemoteException e) {
			throw new TaskException("Couldn't find running instance of ResultsRepository ng to load result",e);
		} catch (ResultsRepositoryException e) {
			throw new TaskException("Couldn't load data from the ResultsRepository ng",e);
		}
		logInfo("Found "+result.size()+" results in the dataset");

		int run = 0;
		long sum = 0;
		long count = 0;
		try {
			for (DataHandleTuple dataHandleTuple : result) {
				if ( highestSerial < dataHandleTuple.getSerial() ) highestSerial = dataHandleTuple.getSerial();
				UUID fileId = dataHandleTuple.get("data").getValue(UUID.class);
				String fileName = getTempDirectory()+File.separator+fileId.toString();
				fileClient.downloadFile(fileId, new File(fileName));

				CSVReader reader = new CSVReader(new FileReader(fileName), ' ');
				String[] data;
				while( (data = reader.readNext()) != null ){
					for (String s : data) {
						if( s.isEmpty() ) continue;
						sum+= Integer.decode(s);
						count++;
					}
				}
				reader.close();
				run++;
			}
		} catch (DataHandleException e) {
			throw new TaskException("Couldn't load data from the result", e);
		} catch (IOException e) {
			throw new TaskException("Couldn't save the result file", e);
		}
		logInfo("Evaluator found "+count+" numbers in "+run+" runs. Mean value is "+(sum/count));

		return highestSerial;
	}

}
