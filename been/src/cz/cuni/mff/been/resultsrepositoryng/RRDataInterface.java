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
package cz.cuni.mff.been.resultsrepositoryng;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import cz.cuni.mff.been.resultsrepositoryng.condition.Condition;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.filestore.FileStoreClient;
import cz.cuni.mff.been.resultsrepositoryng.transaction.RRTransaction;

/**
 * Interface for storing and retrieving data to and from the RR. 
 *
 * @author Jan Tattermusch
 */
public interface RRDataInterface extends Remote {

	/** 
	 * Saves a new data tuple to dataset of given name 
	 * @param analysisName name of analysis to which dataset belongs to
	 * @param datasetName name of dataset to save to
	 * @param data data to save
	 * 
	 * @return serial number assigned to new record

	 * @throws RemoteException
	 * @throws ResultsRepositoryException 
	 */
    long saveData( String analysisName, String datasetName, DataHandleTuple data) throws RemoteException, ResultsRepositoryException;
	
    
    /**
     * Loads all data from a dataset satisfying given condition and
     * having its serial number in given interval (when inserting
     * a new record to results repository, each records gets a
     * serial number from growing sequence).
     * 
     * @param analysisName name of analysis to which dataset belongs to
     * @param datasetName name of dataset to load from
     * @param condition condition that returned entries must meet (if null this constraint is not applied)
     * @param fromSerial record's serial number lower bound (if null this constraint is not applied)
     * @return all data from dataset which meet given condition.
     * @throws RemoteException
     * @throws ResultsRepositoryException 
     */
	List<DataHandleTuple> loadData( String analysisName, String datasetName, Condition condition, Long fromSerial, Long toSerial) throws RemoteException, ResultsRepositoryException;

	
	/**
	 * 
	 * @return client object for working with RR's file store
	 */
	FileStoreClient getFileStoreClient() throws RemoteException;
	
	
	/**
	 * Creates new transaction on given dataset.
	 * Dataset's type has to allow performing transactions (TRANSACTION_ENABLED)
	 * 
	 * @param analysisName	name of analysis
	 * @param datasetName name of dataset
	 * @return new RR transaction associated with given dataset 
	 * @throws RemoteException
	 * @throws ResultsRepositoryException
	 */
	RRTransaction getTransaction(String analysisName, String datasetName) throws RemoteException, ResultsRepositoryException;
}
