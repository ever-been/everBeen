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
package cz.cuni.mff.been.benchmarkmanagerng;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

import cz.cuni.mff.been.benchmarkmanagerng.module.BMEvaluator;
import cz.cuni.mff.been.benchmarkmanagerng.module.BMGenerator;
import cz.cuni.mff.been.benchmarkmanagerng.module.BMModule;
import cz.cuni.mff.been.jaxb.config.Config;

/**
 * Benchmark manager Interface used by web module and command line interface.
 * It provides access to available generators, evaluators and analyses.
 *
 *  @author Jiri Tauber
 */
public interface BenchmarkManagerInterface extends Remote {

	/**
	 * @return list of available generator pluggable modules
	 * @throws RemoteException when remote error occurs in this or any subsequent call 
	 * @throws BenchmarkManagerException when there is an error on the BenchmarkManager side
	 */
	Collection<BMGenerator> getGenerators() throws RemoteException, BenchmarkManagerException;

	/**
	 * @return list of available evaluator pluggable modules
	 * @throws RemoteException when remote error occurs in this or any subsequent call 
	 * @throws BenchmarkManagerException when there is an error on the BenchmarkManager side
	 */
	Collection<BMEvaluator> getEvaluators() throws RemoteException, BenchmarkManagerException;


	/**
	 * Saves the Analysis information to the RR, creates the
	 * data structures in the RR, attaches all listed evaluators
	 * and finally runs the generator
	 *
	 * @param analysis Analysis information
	 * @throws RemoteException when remote error occurs in this or any subsequent call 
	 * @throws BenchmarkManagerException when there is an error on the BenchmarkManager side
	 * @throws AnalysisException when there is error in the analysis itself (invalid configuration, etc.)
	 */
	void createAnalysis(Analysis analysis) throws RemoteException, BenchmarkManagerException, AnalysisException;

	/**
	 * Launches GeneratorRunner with specified analysis as parameter.
	 * That effectively means that the generator associated with the
	 * analysis will do its job creating tasks. The created tasks are
	 * immediately submitted to the task manager.<br>
	 * When {@code force} parameter is set to false then analysis will not be
	 * scheduled unless it's either idle or unknown. When set to true, analysis
	 * will be scheduled unless it is generating.
	 * 
	 * @param name the name of the analysis
	 * @param force whether to force new run even when the old one hasn't reported back
	 * @throws RemoteException when remote error occurs in this or any subsequent call 
	 * @throws BenchmarkManagerException when there is an error on the BenchmarkManager side
	 * @throws AnalysisException when the analysis name is invalid
	 */
	void runAnalysis(String name, boolean force) throws RemoteException, BenchmarkManagerException, AnalysisException;

	/**
	 * Updates <code>Analysis</code> object previously retrieved by <code>getAnalyses()</code>.
	 * The analysis is identified by inaccessible id property.
	 * 
	 * @param analysis The changed analysis information to submit
	 * @throws RemoteException when remote error occurs in this or any subsequent call 
	 * @throws BenchmarkManagerException when there is an error on the BenchmarkManager side
	 * @throws AnalysisException when there is error in the analysis itself (invalid configuration, etc.)
	 */
	void updateAnalysis(Analysis analysis) throws RemoteException, BenchmarkManagerException, AnalysisException;

	/**
	 * Deletes analysis from Benchmark Manager database and from the results repository
	 * 
	 * @throws RemoteException when remote error occurs in this or any subsequent call 
	 * @throws BenchmarkManagerException when there is an error on the BenchmarkManager side
	 */
	void deleteAnalysis(String name) throws RemoteException, BenchmarkManagerException;

	/**
	 * Returns list of contexts and their states registered for certain analysis.
	 * Context names are keys and their respective states are values in returned map.
	 * 
	 * @param analysisName the analysis we query
	 * @return the name - state mapping of analysis contexts registered in the BM
	 * @throws RemoteException when remote error occurs in this call 
	 */
	Map<String, AnalysisState> getActiveContexts(String analysisName) throws RemoteException;

	/**
	 * @param name Name of the analysis to return
	 * @return the analysis object
	 * @throws RemoteException
	 * @throws BenchmarkManagerException When error occurred during loading
	 */
	Analysis getAnalysis(String name) throws RemoteException, BenchmarkManagerException;

	/**
	 * @return list of analyses registered by the Benchmark Manager ordered by name
	 * @throws RemoteException when remote error occurs in this or any subsequent call 
	 * @throws BenchmarkManagerException when there is an error on the BenchmarkManager side
	 */
	Collection<Analysis> getAnalyses() throws RemoteException, BenchmarkManagerException;
	
	/**
	 * Configuration schema getter.
	 * 
	 * @param module config description of which module should be returned ?
	 * @return The parsed configuration description
	 * @throws RemoteException when remote error occurs in this or any subsequent call 
	 * @throws BenchmarkManagerException when there is an error on the BenchmarkManager side
	 */
	Config getConfigurationDescription(BMModule module) throws RemoteException, BenchmarkManagerException;

	/**
	 * Makes the module validate its configuration.
	 * Returns collection of error messages that the module reported.
	 * If the config is valid then the collection will be empty.
	 *
	 * @param module module information
	 * @return errors in the configuration
	 * @throws RemoteException when remote error occurs in this or any subsequent call 
	 * @throws BenchmarkManagerException when there is an error on the BenchmarkManager side
	 */
	Collection<String> validateModuleConfiguration(BMModule module) throws RemoteException;

	/**
	 * Stops the analyses scheduler.
	 * Use this function when you need to stop automatic analysis runs but you
	 * want other BenchmarkManager features accessible. 
	 * 
	 * @throws RemoteException when remote error occurs in this or any subsequent call 
	 */
	void stopScheduler() throws RemoteException;

	/**
	 * @return {@code true} when sheduler is running, {@code false} otherwise
	 * @throws RemoteException when remote error occurs in this or any subsequent call 
	 */
	boolean isSchedulerRunning() throws RemoteException;

}
