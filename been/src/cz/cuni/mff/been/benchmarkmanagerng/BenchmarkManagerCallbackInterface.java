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

/**
 * The Benchmark Manager callback interface used by various services
 * to report back to the Benchmark Manager.
 *
 *  @author Jiri Tauber
 */
public interface BenchmarkManagerCallbackInterface extends Remote {

	/**
	 * Function used by GeneratorRunner to report successful run of the
	 * analysis generator. This function must be called after TaskManager
	 * accepts all the tasks for the analysis.
	 * 
	 * @param analysisName name of the analysis that triggered the generator.
	 * @param contextId name of the context where generator is running.
	 * @param generatorTid task ID of the generator that is reporting the success.
	 * @throws RemoteException when remote error occurs in this or any subsequent call 
	 * @throws BenchmarkManagerException when there is an error on the BenchmarkManager side
	 */
	public void reportGeneratorSuccess(String analysisName, String contextId, String generatorTid) throws RemoteException, BenchmarkManagerException;

	/**
	 * Function used by ContextMonitor to report that analysis has finished work
	 * in a particular context. Analysis is unlocked and can be ran even without
	 * the force parameter if that was the only context with running generator. 
	 * 
	 * @param contextId Context that has just finished
	 * @throws RemoteException
	 * @throws BenchmarkManagerException
	 */
	public void reportAnalysisFinish(String contextId) throws RemoteException, BenchmarkManagerException;
}
