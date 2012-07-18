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
package cz.cuni.mff.been.resultsrepositoryng.transaction;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.condition.Condition;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;

/**
 * Represents results repository transaction associated with a dataset (dataset has to be of type TRANSACTION_ENABLED).
 * Transaction is started when this object is requested from results repository.
 * Once transaction is committed or rolled back, object is expired and should
 * not be used anymore.
 * 
 * @author Jan Tattermusch
 *
 */
public interface RRTransaction extends Remote {
	
	
	/** 
	 * Saves a new data tuple to associated dataset
	 *  
	 * @param data data to save

	 * @throws RemoteException
	 * @throws ResultsRepositoryException 
	 */
    void saveData(DataHandleTuple data) throws RemoteException, ResultsRepositoryException;
	
    /**
     * Loads data satisfying given condition 
     * from associated dataset. 
     * 
     * @param condition condition that returned entries must meet (if null this constraint is not applied)
     * @throws RemoteException
     * @throws ResultsRepositoryException 
     */
	List<DataHandleTuple> loadData(Condition condition) throws RemoteException, ResultsRepositoryException;	
	
	/**
	 * Commits transaction.
	 * After commit, this object is closed and should not be used anymore.
	 * 
	 * @throws RemoteException
	 * @throws ResultsRepositoryException
	 */
	void commit() throws RemoteException, ResultsRepositoryException; 
	
	/**
	 * Rollbacks transaction.
	 * After rollbact, this object is closed and should not be used anymore.
	 * 
	 * @throws RemoteException
	 * @throws ResultsRepositoryException
	 */
	void rollback() throws RemoteException, ResultsRepositoryException;
	
}
