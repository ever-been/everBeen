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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import cz.cuni.mff.been.benchmarkmanagerng.module.ConfigurationException;
import cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorException;
import cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorPluggableModule;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;

/**
 * 
 *
 *  @author: Jiri Tauber
 */
public class MockGeneratorModule extends GeneratorPluggableModule {

	public MockGeneratorModule(PluggableModuleManager manager) {
		super(manager);
	}

	/**
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorInterface#createDatasets(java.lang.String, cz.cuni.mff.been.benchmarkmanagerng.Configuration)
	 */
	@Override
	public void createDatasets()
			throws ResultsRepositoryException {
		// Nothing to be done
	}

	/**
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorInterface#generate(cz.cuni.mff.been.benchmarkmanagerng.Configuration)
	 */
	@Override
	public Collection<TaskDescriptor> generate() {
		return new LinkedList<TaskDescriptor>();
	}

	/**
	 * @see cz.cuni.mff.been.benchmarkmanagerng.module.ModuleInterface#validateConfiguration(cz.cuni.mff.been.benchmarkmanagerng.Configuration)
	 */
	@Override
	public Collection<String> validateConfiguration(Configuration configuration) {
		Collection<String> result = new LinkedList<String>();
		if( configuration.get("number") == null ){
			result.add("Missing number");
		} else {
			if( !configuration.get("number",0).equals("20") )
				result.add("Wrong number (20 expected, "+configuration.get("number",0)+" given)");
		}

		return result;
	}

	public static Configuration getValidConfig(){
		Configuration result = new Configuration();
		result.set("number", new String[]{"20"});
		return result;
	}
	
	@Override
	protected Map<String, DatasetDescriptor> doCreateDatasets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<String> doValidateConfiguration(
			Configuration configuration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<TaskDescriptor> doGenerate()
			throws ConfigurationException, GeneratorException {
		// TODO Auto-generated method stub
		return null;
	}
}
