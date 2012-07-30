/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
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
package cz.cuni.mff.been.clinterface.adapters;

import java.rmi.RemoteException;
import java.util.ArrayDeque;
import java.util.regex.Pattern;

import cz.cuni.mff.been.benchmarkmanagerng.Analysis;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerException;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerService;
import cz.cuni.mff.been.benchmarkmanagerng.module.BMEvaluator;
import cz.cuni.mff.been.benchmarkmanagerng.module.BMGenerator;
import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.clinterface.modules.BenchmarksModule.Errors;
import cz.cuni.mff.been.clinterface.ref.ServiceReference;
import cz.cuni.mff.been.clinterface.ref.TaskManagerReference;
import cz.cuni.mff.been.common.ComponentInitializationException;

/**
 * Abstract ancestor of all Benchmark Manager adapters, with some common utility methods.
 * 
 * @author Andrej Podzimek
 */
public abstract class BenchmarkManagerAbstractAdapter {

	/**
	 * A helper class containing Benchmark Manager modules information.
	 * 
	 * @author Andrej Podzimek
	 */
	public static final class ModuleInfo {
		
		/** Name of the module. */
		private final String name;
		
		/** Version of the module. */
		private final String version;
		
		/** Name of the package used to instantiate the module. */
		private final String packageName;

		/**
		 * Creates a ModuleInfo susing the supplied information.
		 * 
		 * @param name Name of the module.
		 * @param version Version of the module.
		 * @param packageName Name of the package used to instantiate the module
		 */
		ModuleInfo( String name, String version, String packageName ) {
			this.name = name;
			this.version = version;
			this.packageName = packageName;
		}

		/**
		 * Module name getter.
		 * 
		 * @return Name of the module.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Version getter.
		 * 
		 * @return Version of the module.
		 */
		public String getVersion() {
			return version;
		}

		/**
		 * Package name getter.
		 * 
		 * @return the Name of the package used to instantiate the module.
		 */
		public String getPackageName() {
			return packageName;
		}
	}

	/** Reference to a running instance of the Benchmark Manager. */
	protected final ServiceReference< BenchmarkManagerInterface > bmiReference;

	/**
	 * Initializes a reference to the Benchmark Manager using the supplied Task Manager reference.
	 * 
	 * @param taskManagerReference A reference to query the Task Manager.
	 */
	protected BenchmarkManagerAbstractAdapter( TaskManagerReference taskManagerReference ) {
		this.bmiReference = new ServiceReference< BenchmarkManagerInterface >(
			taskManagerReference,
			BenchmarkManagerService.SERVICE_NAME,
			BenchmarkManagerService.RMI_MAIN_IFACE,
			BenchmarkManagerService.SERVICE_HUMAN_NAME
		);
	}
	
	/**
	 * Obtains a list of generators from the BM.
	 * 
	 * @return An Iterable to a collection of ModuleInfo structures.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 */
	public Iterable< ModuleInfo > getGenerators()
	throws BenchmarkManagerException, RemoteException, ComponentInitializationException {
		ArrayDeque< ModuleInfo > result;
		
		result = new ArrayDeque< ModuleInfo >();
		for ( BMGenerator generator : bmiReference.get().getGenerators() ) {
			result.add(
				new ModuleInfo(
					generator.getName(),
					generator.getVersion(),
					generator.getPackageName()
				)
			);
		}
		return result;
	}
	
	/**
	 * Obtains a filtered list of generators from the BM.
	 * 
	 * @param pattern The regular expression generator names must match.
	 * @return An Iterable to a collection of ModuleInfo structures.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 */
	public Iterable< ModuleInfo > getGenerators( Pattern pattern )
	throws BenchmarkManagerException, RemoteException, ComponentInitializationException {
		ArrayDeque< ModuleInfo > result;
		
		result = new ArrayDeque< ModuleInfo >();
		for ( BMGenerator generator : bmiReference.get().getGenerators() ) {
			if ( pattern.matcher( generator.getName() ).matches() ) {
				result.add(
					new ModuleInfo(
						generator.getName(),
						generator.getVersion(),
						generator.getPackageName()
					)
				);
			}
		}
		return result;
	}
	
	/**
	 * Obtains a list of evaluators from the BM.
	 * 
	 * @return An Iterable to a collection of ModuleInfo structures.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 */
	public Iterable< ModuleInfo > getEvaluators()
	throws BenchmarkManagerException, RemoteException, ComponentInitializationException {
		ArrayDeque< ModuleInfo > result;
		
		result = new ArrayDeque< ModuleInfo >();
		for ( BMEvaluator evaluator : bmiReference.get().getEvaluators() ) {
			result.add(
				new ModuleInfo(
					evaluator.getName(),
					evaluator.getVersion(),
					evaluator.getPackageName()
				)
			);
		}
		return result;
	}
	
	/**
	 * Obtains a filtered list of evaluators from the BM.
	 * 
	 * @param pattern The regular expression evaluator names must match.
	 * @return An Iterable to a collection of ModuleInfo structures.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 */
	public Iterable< ModuleInfo > getEvaluators( Pattern pattern )
	throws BenchmarkManagerException, RemoteException, ComponentInitializationException {
		ArrayDeque< ModuleInfo > result;
		
		result = new ArrayDeque< ModuleInfo >();
		for ( BMEvaluator evaluator : bmiReference.get().getEvaluators() ) {
			if ( pattern.matcher( evaluator.getName() ).matches() ) {
				result.add(
					new ModuleInfo(
						evaluator.getName(),
						evaluator.getVersion(),
						evaluator.getPackageName()
					)
				);
			}
		}
		return result;
	}
	
	/**
	 * Raw analyses getter for simple listing.
	 * 
	 * @return An Iterable to the collection of analyses in the BM.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws RemoteException when it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 */
	public Iterable< Analysis > getRawAnalyses()
	throws BenchmarkManagerException, RemoteException, ComponentInitializationException {
		return bmiReference.get().getAnalyses();
	}
	
	/**
	 * Filtered raw analyses getter for simple listing.
	 * 
	 * @param pattern The regular expression to match.
	 * @return An Iterable to the collection of analyses in the BM.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws RemoteException when it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 */
	public Iterable< Analysis > getAnalyses( Pattern pattern )
	throws BenchmarkManagerException, RemoteException, ComponentInitializationException {
		ArrayDeque< Analysis > result;
		
		result = new ArrayDeque< Analysis >();
		for ( Analysis analysis : bmiReference.get().getAnalyses() ) {
			if ( pattern.matcher( analysis.getName() ).matches() ) {
				result.add( analysis );
			}
		}
		return result;
	}
	
	/**
	 * Retrieves an analysis of the requested name.
	 * 
	 * @param bmi An instance of Benchmark Manager's remote interface.
	 * @param name Name of the analysis.
	 * @return An analysis instance of the requested name.
	 * @throws ModuleSpecificException When no analysis of the requested name is found.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws RemoteException When it rains.
	 */
	protected static Analysis getAnalysis( BenchmarkManagerInterface bmi, String name )
	throws ModuleSpecificException, BenchmarkManagerException, RemoteException {
		for ( Analysis analysis : bmi.getAnalyses() ) {
			if ( analysis.getName().equals( name ) ) {
				return analysis;
			}
		}
		throw new ModuleSpecificException( Errors.INVD_ANAL, " (" + name + ')' );
	}
	
	/**
	 * Retrieves the latest available version of a generator.
	 * 
	 * @param bmi An instance of Benchmark Manager's remote interface.
	 * @param name Name of the generator.
	 * @return A generator instance of the requested name and the latest available version.
	 * @throws ModuleSpecificException When no generator is found.
	 * @throws BenchmarkManagerException When something bad happens inside Benchmark Manager.
	 * @throws RemoteException When it rains.
	 */
	protected static BMGenerator getGenerator( BenchmarkManagerInterface bmi, String name )
	throws ModuleSpecificException, BenchmarkManagerException, RemoteException {
		BMGenerator result = null;
		String version = "";
		
		for ( BMGenerator generator : bmi.getGenerators() ) {
			if ( generator.getName().equals( name ) ) {
				if ( version.compareTo( generator.getVersion() ) < 0 ) {
					result = generator;
					version = generator.getVersion();
				}
			}
		}
		if ( null == result ) {
			throw new ModuleSpecificException( Errors.INVD_GEN, " (" + name + ')' );
		}
		return result;
	}
	
	/**
	 * Retrieves the latest available version of an evaluator.
	 * 
	 * @param bmi An instance of Benchmark Manager's remote interface.
	 * @param name Name of the evaluator.
	 * @return An evaluator instance of the requested name and the latest available version.
	 * @throws ModuleSpecificException When no evaluator is found.
	 * @throws BenchmarkManagerException When something bad happens inside Benchmark Manager.
	 * @throws RemoteException When it rains.
	 */
	protected static BMEvaluator getEvaluator( BenchmarkManagerInterface bmi, String name )
	throws ModuleSpecificException, BenchmarkManagerException, RemoteException {
		BMEvaluator result = null;
		String version = "";
		
		for ( BMEvaluator evaluator : bmi.getEvaluators() ) {
			if ( evaluator.getName().equals( name ) ) {
				if ( version.compareTo( evaluator.getVersion() ) < 0 ) {
					result = evaluator;
					version = evaluator.getVersion();
				}
			}
		}
		if ( null == result ) {
			throw new ModuleSpecificException( Errors.INVD_EVAL, " (" + name + ')' );
		}
		return result;
	}
	
	/**
	 * Retrieves a generator instance of the requested name and version.
	 * 
	 * @param bmi An instance of Benchmark Manager's remote interface.
	 * @param name Name of the generator.
	 * @param version Version of the generator.
	 * @return A generator instance of the requested name and version.
	 * @throws ModuleSpecificException When no generator is found or no such version is available.
	 * @throws BenchmarkManagerException When something bad happens inside Bechmark Manager.
	 * @throws RemoteException When it rains.
	 */
	protected static BMGenerator getGenerator(
		BenchmarkManagerInterface bmi,
		String name,
		String version
	) throws ModuleSpecificException, BenchmarkManagerException, RemoteException {
		boolean nameExists = false;
		
		for ( BMGenerator generator : bmi.getGenerators() ) {
			if ( generator.getName().equals( name ) ) {
				nameExists = true;
				if ( generator.getVersion().equals( version ) ) {
					return generator;
				}
			}
		}
		throw new ModuleSpecificException(
			nameExists ? Errors.INVD_GEN_VER : Errors.INVD_GEN,
			" (" + name + ' ' + version + ')'
		);
	}
	
	/**
	 * Retrieves an evaluator instance of the requested name and version.
	 * 
	 * @param bmi An instance of Benchmark Manager's remote interface.
	 * @param name Name of the evaluator.
	 * @param version Version of the evaluator.
	 * @return An evaluator instance of the requested name and version.
	 * @throws ModuleSpecificException When no evaluator is found or no such version is available.
	 * @throws BenchmarkManagerException When something bad happens inside Benchmark Manager.
	 * @throws RemoteException When it rains.
	 */
	protected static BMEvaluator getEvaluator(
		BenchmarkManagerInterface bmi,
		String name,
		String version
	) throws ModuleSpecificException, BenchmarkManagerException, RemoteException {
		boolean nameExists = false;
		
		for ( BMEvaluator evaluator : bmi.getEvaluators() ) {
			if ( evaluator.getName().equals( name ) ) {
				nameExists = true;
				if ( evaluator.getVersion().equals( version ) ) {
					return evaluator;
				}
			}
		}
		throw new ModuleSpecificException(
			nameExists ? Errors.INVD_EVAL_VER : Errors.INVD_EVAL,
			" (" + name + ' ' + version + ')'
		);
	}
	
	/**
	 * Drops the underlying Benchmark Manager reference.
	 */
	public void drop() {
		bmiReference.drop();
	}
}
