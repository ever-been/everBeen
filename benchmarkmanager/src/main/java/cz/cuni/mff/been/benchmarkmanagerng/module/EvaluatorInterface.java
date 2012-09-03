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
package cz.cuni.mff.been.benchmarkmanagerng.module;

import java.rmi.RemoteException;

import cz.cuni.mff.been.benchmarkmanagerng.Analysis;
import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;

/**
 * Interface for pluggable module of type evaluator.
 * 
 * @author Jiri Tauber
 */
public interface EvaluatorInterface extends ModuleInterface {

	/**
	 * Attaches the evaluator to the analysis. At this point the evaluator
	 * creates its datasets and triggers in the analysis.
	 * 
	 * @param anaysis The analysis which contains this evaluator
	 * @param configuration The evaluator configuration
	 * @throws RemoteException When a remote error occurred (calls to the Results Repository)
	 * @throws ResultsRepositoryException When the Results Repository throws an unexpected exception
	 * @throws ConfigurationException When there is an error in configuration
	 */
	void attachToAnalysis( Analysis anaysis, Configuration configuration ) throws RemoteException, ResultsRepositoryException, ConfigurationException;

	/**
	 * Detaches the evaluator from the analysis. At this point the evaluator
	 * deletes its triggers. It should keep any datasets it created in the analysis.
	 *  
	 * @param analysis The host analysis
	 * @param configuration The evaluator configuration used when attaching it to the analysis 
	 * @throws RemoteException When a remote error occurred (calls to the Results Repository)
	 * @throws ResultsRepositoryException When the Results Repository throws an unexpected exception
	 * @throws ConfigurationException When there is an error in configuration
	 */
	void detachFromAnalysis( Analysis analysis, Configuration configuration ) throws RemoteException, ResultsRepositoryException, ConfigurationException;

}
