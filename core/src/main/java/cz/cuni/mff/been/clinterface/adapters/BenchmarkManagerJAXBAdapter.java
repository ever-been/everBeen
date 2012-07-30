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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import cz.cuni.mff.been.benchmarkmanagerng.Analysis;
import cz.cuni.mff.been.benchmarkmanagerng.AnalysisException;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerException;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface;
import cz.cuni.mff.been.benchmarkmanagerng.Configuration;
import cz.cuni.mff.been.benchmarkmanagerng.module.BMEvaluator;
import cz.cuni.mff.been.benchmarkmanagerng.module.BMGenerator;
import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.clinterface.modules.BenchmarksModule.Errors;
import cz.cuni.mff.been.clinterface.ref.TaskManagerReference;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.jaxb.Factory;
import cz.cuni.mff.been.jaxb.benchmark.Benchmark;
import cz.cuni.mff.been.jaxb.benchmark.Desc;
import cz.cuni.mff.been.jaxb.benchmark.Evaluators;
import cz.cuni.mff.been.jaxb.benchmark.GenEval;
import cz.cuni.mff.been.jaxb.benchmark.Generator;
import cz.cuni.mff.been.jaxb.benchmark.Period;
import cz.cuni.mff.been.jaxb.benchmark.RSL;
import cz.cuni.mff.been.jaxb.benchmark.Values;
import cz.cuni.mff.been.jaxb.config.Config;

/**
 * A class that simplifies BenchmarkManagerInterface calls so that CLI can use them.
 * 
 * @author Andrej Podzimek
 */
public final class BenchmarkManagerJAXBAdapter extends BenchmarkManagerAbstractAdapter {
	
	/**
	 * Initializes the internal JAXB Object Factory and stores a reference to the Benchmark
	 * Manager using the abstract ancestor.
	 * 
	 * @param taskManagerReference A reference to the Task Manager.
	 */
	public BenchmarkManagerJAXBAdapter( TaskManagerReference taskManagerReference ) {
		super( taskManagerReference );
	}
	
	/**
	 * Creates a new analysis in the Bencmark Manager.
	 *
	 * @param benchmark The XML-based representation of a benchmark descriptor.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 * @throws ModuleSpecificException For known errors that should be reported. 
	 */
	public void createAnalysis( Benchmark benchmark ) throws
		BenchmarkManagerException,
		RemoteException,
		ComponentInitializationException,
		ModuleSpecificException
	{
		BenchmarkManagerInterface bmi;
		Analysis analysis;
		ArrayDeque< String > messages;
		
		bmi = bmiReference.get();
		messages = new ArrayDeque< String >();
		analysis = new Analysis(
			benchmark.getName(),																	// Name
			benchmark.isSetDesc() ? benchmark.getDesc().getValue() : null,							// Description
			prepareGenerator( bmi, benchmark.getGenerator(), messages )								// Generator
		);
		
		analysis.setGeneratorHostRSL( benchmark.getRSL().getValue() );								// Generator Host RSL
		if ( benchmark.isSetPeriod() ) {
			long longPeriod = benchmark.getPeriod().getValue();
			
			if ( longPeriod <= Integer.MAX_VALUE ) {
				analysis.setRunPeriod( (int) longPeriod );											// Run Period
			} else {
				throw new ModuleSpecificException( Errors.MALF_PERIOD, " (" + longPeriod + ")" );
			}
		}
		if ( benchmark.isSetEvaluators() ) {
			for (
				BMEvaluator evaluator :
				prepareEvaluators( bmi, benchmark.getEvaluators(), messages ) 
			) {
				analysis.addEvaluator( evaluator );													// Evaluators
			}
		}
		
		if ( messages.isEmpty() ) {
			bmi.createAnalysis( analysis );															// FIRE!
		} else {
			throw new ModuleSpecificException( Errors.INVD_CONFIG, getLines( messages ) );			// Make a detailed report.
		}
	}
	
	/**
	 * Runs an anaysis by name.
	 * 
	 * @param name Name of the analysis to run.
	 * @param force Force the command even when another instance is still running.
	 * @throws ComponentInitializationException When the Service Reference repors a failure.
	 * @throws RemoteException  When it rains.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws ModuleSpecificException For known errors that should be reported.
	 */
	public void runAnalysis( String name, boolean force ) throws
		BenchmarkManagerException,
		RemoteException,
		ComponentInitializationException,
		ModuleSpecificException
	{
		try {
			bmiReference.get().runAnalysis( name, force );											// FIRE!
		} catch ( AnalysisException exception ) {
			throw new ModuleSpecificException( Errors.INVD_ANAL, " (" + name + ')', exception );	// Re-report incorrect name...
		}
	}
	
	/**
	 * Updates an analysis that already exists in the Benchmark Manager.
	 * 
	 * @param benchmark The XML-based representation of a benchmark descriptor.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 * @throws ModuleSpecificException For known errors that should be reported.
	 */
	public void updateAnalysis( Benchmark benchmark ) throws
		BenchmarkManagerException,
		RemoteException,
		ComponentInitializationException,
		ModuleSpecificException
	{
		BenchmarkManagerInterface bmi;
		Analysis analysis;
		ArrayDeque< String > messages;
		
		bmi = bmiReference.get();
		messages = new ArrayDeque< String >();
		analysis = getAnalysis( bmi, benchmark.getName() );											// Name
		
		// Description
		if ( benchmark.isSetDesc() ) {
			if ( benchmark.getDesc().isChanged() ) {
				analysis.setDescription( benchmark.getDesc().getValue() );							// Description
			}
		} else {
			analysis.setDescription( null );														// Description <- null
		}
		
		// RSL
		if ( benchmark.getRSL().isChanged() ) {														// This is always set.
			analysis.setGeneratorHostRSL( benchmark.getRSL().getValue() );							// Generator Host RSL
		}
		
		// Run period
		if ( benchmark.isSetPeriod() ) {
			if ( benchmark.getPeriod().isChanged() ) {
				long longPeriod = benchmark.getPeriod().getValue();

				if ( longPeriod <= Integer.MAX_VALUE ) {
					analysis.setRunPeriod( (int) longPeriod );										// Run Period
				} else {
					throw new ModuleSpecificException(
						Errors.MALF_PERIOD,
						" (" + longPeriod + ")"
					);
				}
			}
		} else {
			analysis.setRunPeriod( null );															// Run Period <- null
		}
		
		// Generator
		if ( benchmark.getGenerator().isChanged() ) {												// This is always set.
			analysis.setGenerator( prepareGenerator( bmi, benchmark.getGenerator(), messages ) );	// Generator
		}
		
		// Evaluators
		if ( benchmark.isSetEvaluators() ) {
			if ( benchmark.getEvaluators().isChanged() ) {
				analysis.removeEvaluators();
				for (
					BMEvaluator evaluator :
					prepareEvaluators( bmi, benchmark.getEvaluators(), messages )
				) {
					analysis.addEvaluator( evaluator );												// Evaluators
				}
			}
		} else {
			analysis.removeEvaluators();															// Evaluators <- null
		}
		
		if ( messages.isEmpty() ) {
			bmi.updateAnalysis( analysis );															// FIRE!
		} else {
			throw new ModuleSpecificException( Errors.INVD_CONFIG, getLines( messages ) );			// Make a detailed report. 
		}
	}
	
	/**
	 * Deletes an analysis that already exists in the Benchmark Manager.
	 * 
	 * @param name Name of the analysis to delete.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws ModuleSpecificException For known errors that should be reported.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failue.
	 */
	public void deleteAnalysis( String name ) throws
		BenchmarkManagerException,
		ModuleSpecificException,
		RemoteException,
		ComponentInitializationException
	{
		BenchmarkManagerInterface bmi;
		
		bmi = bmiReference.get();
		bmi.deleteAnalysis( name );
	}
	
	/**
	 * Obtains a Benchmark object of an analysis.
	 * 
	 * @param name Name of the analysis to fetch.
	 * @return A Benchmark JAXB-based object representing the requested analysis.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 * @throws ModuleSpecificException For known errors that should be repoted.
	 */
	public Benchmark getAnalysis( String name ) throws
		BenchmarkManagerException,
		RemoteException,
		ComponentInitializationException,
		ModuleSpecificException
	{
		return analysisToJAXB( getAnalysis( bmiReference.get(), name ) );
	}
	
	/**
	 * Retrieves the configuration description of a generator.
	 * 
	 * @param name Name of the generator to find.
	 * @param version Version of the generator to find.
	 * @return A JAXB-based object representing the generator's configuration metadata.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws ModuleSpecificException For known errors that should be reported.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failue.
	 */
	public Config getGeneratorConfig( String name, String version ) throws
		BenchmarkManagerException,
		ModuleSpecificException,
		RemoteException,
		ComponentInitializationException
	{
		BenchmarkManagerInterface bmi;
		
		bmi = bmiReference.get();
		return bmi.getConfigurationDescription( getGenerator( bmi, name, version ) );
	}
	
	/**
	 * Retrieves the configuration description of an evaluator.
	 * 
	 * @param name Name of the evaluator to find.
	 * @param version Version of the evaluator to find.
	 * @return A JAXB-based object representing the evaluator's configuration metadata.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws ModuleSpecificException For known errors that should be reported.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failue.
	 */
	public Config getEvaluatorConfig( String name, String version ) throws
		BenchmarkManagerException,
		ModuleSpecificException,
		RemoteException,
		ComponentInitializationException
	{
		BenchmarkManagerInterface bmi;
		
		bmi = bmiReference.get();
		return bmi.getConfigurationDescription( getEvaluator( bmi, name, version ) );
	}
	
	/**
	 * Translates a whole Analysis to its JAXB representation.
	 * 
	 * @param analysis A complete and correct Analysis instance from the Benchmark Manager.
	 * @return A XML-based representation of the Analysis and its whole configuration.
	 */
	private static Benchmark analysisToJAXB( Analysis analysis ) {
		Benchmark benchmark;
		Desc desc;
		RSL rsl;
		Period period;
		Generator generator;
		GenEval evaluator;
		Evaluators evaluators;
		BMGenerator generatorInst;
		List< GenEval > evaluatorList;

		benchmark = Factory.BENCHMARK.createBenchmark();
		benchmark.setName( analysis.getName() );													// Name
			
		if ( null != analysis.getDescription() ) {
			desc = Factory.BENCHMARK.createDesc();
			desc.setValue( analysis.getDescription() );
			benchmark.setDesc( desc );																// Description
		}
			
		rsl = Factory.BENCHMARK.createRSL();
		rsl.setValue( analysis.getGeneratorHostRSL() );												// Generator Host RSL.
		benchmark.setRSL( rsl );
		
		if ( null != analysis.getRunPeriod() ) {
			period = Factory.BENCHMARK.createPeriod();
			period.setValue( analysis.getRunPeriod() );
			benchmark.setPeriod( period );
		}
			
		generatorInst = analysis.getGenerator();
		generator = Factory.BENCHMARK.createGenerator();
		generator.setName( generatorInst.getName() );
		generator.setVersion( generatorInst.getVersion() );
		fillValuesList( generatorInst.getConfiguration(), generator.getValues() );
		benchmark.setGenerator( generator );
			
		evaluators = Factory.BENCHMARK.createEvaluators();
		evaluatorList = evaluators.getEvaluator();
		for ( BMEvaluator evaluatorInst : analysis.getEvaluators() ) {
			evaluator = Factory.BENCHMARK.createGenEval();
			evaluator.setName( evaluatorInst.getName() );
			evaluator.setVersion( evaluatorInst.getVersion() );
			fillValuesList( evaluatorInst.getConfiguration(), evaluator.getValues() );
			evaluatorList.add( evaluator );
		}
		benchmark.setEvaluators( evaluators );
			
		return benchmark;
	}
	
	/**
	 * Translates a Configuration instance to its corresponding XML representation.
	 * 
	 * @param configuration Configuration from a BM module (input).
	 * @param valuesList A list of XML based Values instances (name=values[] pairs) (output).
	 */
	private static void fillValuesList( Configuration configuration, List< Values > valuesList ) {
		Values values;
		List< String > valueList;
		
		for ( Entry< String, String[] > entry : configuration.entrySet() ) {
			values = Factory.BENCHMARK.createValues();
			values.setName( entry.getKey() );
			valueList = values.getValue();
			for ( String val : entry.getValue() ) {
				valueList.add( val );
			}
			valuesList.add( values );
		}
	}
	
	/**
	 * Joins multiple strings into one, prefixing each of them with a new line.
	 * 
	 * @param strings Just about any collection of strings to join. 
	 * @return A concatenation of all the strings, each of them prefixed with '\n'.
	 */
	private static String getLines( Iterable< String > strings ) {
		StringBuilder builder;
		
		builder = new StringBuilder();
		for ( String string : strings ) {
			builder.append( '\n' ).append( string );
		}
		return builder.toString();
	}

	/**
	 * Prepares a generator descriptor based on the supplied XML-based descriptor.
	 * 
	 * @param bmi An instance of Benchmark Manager's remote interface.
	 * @param generator The XML-based generator descriptor.
	 * @return A generator descriptor suitable for the Benchmark Manager.
	 * @throws ModuleSpecificException For known errors that should be reported.
	 * @throws BenchmarkManagerException When something bad happens in the Benchmark Manager.
	 * @throws RemoteException When it rains.
	 */
	private static BMGenerator prepareGenerator(
		BenchmarkManagerInterface bmi,
		GenEval generator,
		Collection< String > messages
	) throws ModuleSpecificException, BenchmarkManagerException, RemoteException {
		BMGenerator result;
		
		result = generator.isSetVersion() ?
			getGenerator( bmi, generator.getName(), generator.getVersion() ) :
			getGenerator( bmi, generator.getName() );
		result.setConfiguration( prepareConfiguration( generator ) );
		messages.addAll( bmi.validateModuleConfiguration( result ) );
		return result;
	}
	
	/**
	 * Prepares a list of evaluator descriptors based on the supplied XML-based descriptor.
	 * 
	 * @param bmi An instance of Benchmark Manager's remote interface.
	 * @param evaluators The XML-based descriptor of a list of evaluators.
	 * @return A list of evaluator descriptors suitable for the Benchmark Manager.
	 * @throws ModuleSpecificException
	 * @throws BenchmarkManagerException
	 * @throws RemoteException
	 */
	private static ArrayList< BMEvaluator > prepareEvaluators(
		BenchmarkManagerInterface bmi,
		Evaluators evaluators,
		Collection< String > messages
	) throws ModuleSpecificException, BenchmarkManagerException, RemoteException {
		ArrayList< BMEvaluator > result;
		BMEvaluator evalInst;
		
		result = new ArrayList< BMEvaluator >();
		for ( GenEval evalDesc : evaluators.getEvaluator() ) {
			evalInst = evalDesc.isSetVersion() ?
				getEvaluator( bmi, evalDesc.getName(), evalDesc.getVersion() ) :
				getEvaluator( bmi, evalDesc.getName() );
			evalInst.setConfiguration( prepareConfiguration( evalDesc ) );
			messages.addAll( bmi.validateModuleConfiguration( evalInst ) );
			result.add( evalInst );
		}
		return result;
	}
	
	/**
	 * Prepares a Configuration object based on the supplied XML-based descriptor.
	 * 
	 * @param module The XML-based descriptor of a list of name=value pairs.
	 * @return A Configuration object that can be handled by the Benchmark Manager.
	 */
	private static Configuration prepareConfiguration( GenEval module ) {
		Configuration configuration;
		List< String > items;
		
		configuration = new Configuration();
		for ( Values values : module.getValues() ) {
			items = values.getValue();
			configuration.set( values.getName(), items.toArray( new String[ items.size() ] ) );
		}
		return configuration;
	}
}
