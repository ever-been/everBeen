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

import java.util.Collection;

import cz.cuni.mff.been.benchmarkmanagerng.Analysis;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;

/**
 * Interface for generator pluggable modules for the benchmark manager
 *
 * @author Jiri Tauber
 */
public interface GeneratorInterface extends ModuleInterface {

	/**
	 * Configure the module before calling any other method.
	 * It validates the generator configuration saved in the Analysis.
	 * 
	 * @param analysis the analysis that is configuring this generator
	 * @throws ConfigurationException when some configuration error occurs
	 */
	public void configure(Analysis analysis) throws ConfigurationException;

	/**
	 * Creates datasets in the results repository.
	 * You must call <code>configure()</code> before this method
	 * so the module knows the analysis name and configuration.  
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorInterface#configure(cz.cuni.mff.been.benchmarkmanagerng.Analysis) configure(Analysis)
	 * @throws ResultsRepositoryException
	 * @throws ConfigurationException
	 * @throws GeneratorException
	 */
	public void createDatasets()
		throws ConfigurationException, GeneratorException, ResultsRepositoryException;

	/**
	 * Creates the list of tasks for one experiment.
	 * The module must be properly initialized before this call.
	 * Call <code>configure()</code> to ensure that.
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorInterface#configure(cz.cuni.mff.been.benchmarkmanagerng.Analysis) configure(Analysis)
	 * @return The list of task descriptors
	 * @throws ConfigurationException when missing configuration information
	 * @throws GeneratorException
	 */
	public Collection<TaskDescriptor> generate()
		throws ConfigurationException, GeneratorException;

}
