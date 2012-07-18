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
package cz.cuni.mff.been.resultsrepositoryng.implementation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.condition.Condition;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.transaction.RRTransaction;

/**
 * Implementation of RR Transaction 
 * @author Jan Tattermusch
 *
 */
class RRTransactionImplementation extends UnicastRemoteObject implements RRTransaction {
	
    /**
     * results repository instance
     */
	private ResultsRepositoryImplementation resultsRepository;
	
	/**
	 * name of associated analysis 
	 */
	private String analysisName;
	
	/**
	 * name of associated dataset
	 */
	private String datasetName;
	
	/**
	 * associated session
	 */
	private Session session;
	
	/**
	 * associated transaction
	 */
	private Transaction transaction;
	
	/**
	 * Creates new instance of RRTransactionImplementation 
	 * @param resultsRepository results repository instance
	 * @param analysisName analysis name
	 * @param datasetName dataset name
	 * @param session hibernate session
	 * @param transaction hibernate transaction
	 * @throws RemoteException
	 */
	protected RRTransactionImplementation(ResultsRepositoryImplementation resultsRepository, String analysisName, String datasetName, Session session, Transaction transaction) throws RemoteException {
		super();

		this.resultsRepository = resultsRepository;
		this.analysisName = analysisName;
		this.datasetName = datasetName;
		this.session = session;
		this.transaction = transaction;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6662160471977190941L;

	
	@Override
	public synchronized void commit() throws RemoteException, ResultsRepositoryException {
		transaction.commit();
		session.close();
		
	}

	@Override
	public synchronized List<DataHandleTuple> loadData(Condition condition)
			throws RemoteException, ResultsRepositoryException {
		
		return resultsRepository.loadData(analysisName, datasetName, condition, (Long) null, (Long) null, session);
	}

	@Override
	public synchronized void rollback() throws RemoteException, ResultsRepositoryException {
		transaction.rollback();
		session.close();
	}

	@Override
	public synchronized void saveData(DataHandleTuple data) throws RemoteException,
			ResultsRepositoryException {
		
		resultsRepository.saveData(analysisName, datasetName, data, session);
		
	}

}
