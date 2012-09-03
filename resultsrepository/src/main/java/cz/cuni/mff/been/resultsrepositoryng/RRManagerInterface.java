/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiří Täuber
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
import java.util.NoSuchElementException;
import java.util.UUID;


/**
 * Interface for managing the RR. 
 *
 * @author Jan Tattermusch
 */
public interface RRManagerInterface extends Remote {
	
	/**
	 * Creates a new dataset in the Results Repository
	 * 
	 * @param analysis name of analysis to which dataset will belong to
	 * @param name Dataset name - must be unique
	 * @param descriptor Dataset Descriptor
	 * @throws ResultsRepositoryException when something goes wrong
	 */
	void createDataset( String analysis, String name, DatasetDescriptor descriptor )
		throws ResultsRepositoryException, RemoteException;
	/**
	 * Deletes a dataset and all the triggers that are associated with it
	 * 
	 * @param analysis name of analysis to which dataset belongs to
	 * @param dataset name of the dataset to delete
	 * @throws ResultsRepositoryException 
	 */
	void deleteDataset( String analysis, String dataset ) throws RemoteException, ResultsRepositoryException;
	
	/**
	 * @param analysis analysis to which datasets should belong
	 * @return List of dataset names belonging to given analysis
	 */
	List<String> getDatasets(String analysis) throws ResultsRepositoryException, RemoteException;
	
	/**
	 * 
	 * @return List of all analyses referenced by RR
	 */
	List<String> getAnalyses() throws ResultsRepositoryException, RemoteException;
	
	/**
	 * @param analysis analysis to which dataset belongs to
	 * @param dataset name of the dataset
	 * @return descriptor of the given dataset
	 */
	DatasetDescriptor getDatasetDescriptor( String analysis, String dataset ) throws ResultsRepositoryException, RemoteException;

	/**
	 * Saves new trigger in the RR if it's not there yet.
	 * This function does not overwrite any existing triggers.
	 * 
	 * Newly created trigger will take into count data inserted
	 * after its creation.
	 * 
	 * Trying to insert trigger with the same Id that is already assigned
	 * to another trigger results in error.
	 * 
	 * @param trigger
	 * @throws ResultsRepositoryException
	 */
	void createTrigger( RRTrigger trigger )
		throws ResultsRepositoryException, RemoteException;
	
	/**
	 * Deletes triggers on a specific dataset that belong to a specific evaluator
	 * 
	 * @param analysis name of analysis to which dataset belongs to
	 * @param dataset name of the dataset (if null, constraint not applied and all triggers with given evaluator will be deleted)
	 * @param evaluator name of the evaluator (if null, constraint not applied and all triggers of given dataset will be deleted)
	 * @throws ResultsRepositoryException 
	 */
	void deleteTriggers( String analysis, String dataset, String evaluator ) throws RemoteException, ResultsRepositoryException;
	
	/**
	 * Deletes a trigger by UUID.
	 * 
	 * @param triggerId UUID of the trigger to delete.
	 * @throws ResultsRepositoryException When a hibernate-related failure occurs.
	 * @throws RemoteException When RMI fails.
	 * @throws NoSuchElementException When no such trigger is found.
	 */
	void deleteTrigger( UUID triggerId )
	throws ResultsRepositoryException, RemoteException, NoSuchElementException;
	
	/**
	 * @param analysis name of analysis to which dataset belongs to
	 * @param dataset name of the dataset
	 * @return Iterable set of triggers associated with the dataset
	 */
	List<RRTrigger> getTriggers( String analysis, String dataset ) throws ResultsRepositoryException, RemoteException;
	
	/**
	 * Gets a trigger by UUID.
	 * 
	 * @param triggerId UUID of the trigger.
	 * @return A trigger instance.
	 * @throws ResultsRepositoryException When a hibernate-related failure occurs.
	 * @throws RemoteException When RMI fails.
	 * @throws NoSuchElementException When no such trigger is found.
	 */
	RRTrigger getTrigger( UUID triggerId )
	throws ResultsRepositoryException, RemoteException, NoSuchElementException;
	
	/**
	 * Notifies that data with serial number less or equal to 
	 * argument lastProcessedSerial have been successfully processed by evaluator.
	 * 
	 * @param triggerId trigger's identifiers
	 * @param lastProcessedSerial last serial number that has been processed
	 * @throws ResultsRepositoryException
	 * @throws RemoteException
	 */
	void notifyDataProcessed(UUID triggerId, long lastProcessedSerial) throws ResultsRepositoryException, RemoteException;
	
}

