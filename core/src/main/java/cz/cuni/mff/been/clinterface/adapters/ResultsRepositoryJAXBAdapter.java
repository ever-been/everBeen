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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.clinterface.ref.TaskManagerReference;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.jaxb.dataset.Dataset;
import cz.cuni.mff.been.jaxb.td.Condition;
import cz.cuni.mff.been.jaxb.td.Trigger;
import cz.cuni.mff.been.jaxb.tuplit.Row;
import cz.cuni.mff.been.jaxb.tuplit.TupLit;
import cz.cuni.mff.been.resultsrepositoryng.RRDataInterface;
import cz.cuni.mff.been.resultsrepositoryng.RRTrigger;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;

/**
 * A class that simplicifes ResultsRepository calls so that CLI can use them.
 * 
 * @author Andrej Podzimek
 */
public final class ResultsRepositoryJAXBAdapter extends ResultsRepositoryAbstractAdapter {

	/**
	 * Initializes the internal JAXB Object Factory and stores a reference to the Results
	 * Repository using the abstract ancestor.
	 * 
	 * @param taskManagerReference A reference to the Task Manager.
	 */
	public ResultsRepositoryJAXBAdapter( TaskManagerReference taskManagerReference ) {
		super( taskManagerReference );
	}
	
	/**
	 * Dataset JAXB structure getter.
	 * 
	 * @param analysis Name of the analysis.
	 * @param dataset Name of the dataset.
	 * @return A JAXB-annotated data structure representing the dataset.
	 * @throws ResultsRepositoryException When something bad happens in the Results Repository.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 */
	public Dataset getDataset( String analysis, String dataset )
	throws ResultsRepositoryException, RemoteException, ComponentInitializationException {
		return DatasetConvertor.getDataset(
			rrManagerReference.get().getDatasetDescriptor( analysis, dataset ),
			analysis,
			dataset
		);
	}
	
	/**
	 * TupLit JAXB structure getter. Extracts data tuples from the dataset and constructs their
	 * JAXB-based representation.
	 * 
	 * @param analysis Name of the analysis.
	 * @param dataset Name of the dataset.
	 * @param condition The condition the returned data must meet (null for none).
	 * @param from First serial number (null for no limit).
	 * @param to Last serial number (null for no limit).
	 * @return A JAXB-annotated data structure representing the dataset's content.
	 * @throws ResultsRepositoryException When something bad happens in the Results Repository.
	 * @throws ModuleSpecificException On XML integrity errors or (de)serialization problems.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 */
	public TupLit getDatasetData(
		String analysis,
		String dataset,
		Condition condition,
		Long from,
		Long to
	) throws
		ResultsRepositoryException,
		ModuleSpecificException,
		RemoteException,
		ComponentInitializationException
	{
		List< DataHandleTuple > tuples;
	
		tuples = rrDataReference.get().loadData(
			analysis,
			dataset,
			ConditionConvertor.conditionToCondition( condition ),
			from,
			to
		);
		return TupleConvertor.tuplesToTupLit( tuples );
	}
	
	/**
	 * DataHandleTuple structures getter. Extracts data tuples from the dataset.
	 * 
	 * @param analysis Name of the analysis.
	 * @param dataset Name of the dataset.
	 * @param condition The condition the returned data must meet (null for none).
	 * @param from First serial number (null for no limit).
	 * @param to Last serial number (null for no limit).
	 * @return A list of DataHandleTuple structures.
	 * @throws ResultsRepositoryException When something bad happens in the Results Repository.
	 * @throws ModuleSpecificException On XML integrity errors or (de)serialization problems.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 */
	public Iterable< DataHandleTuple > getDatasetRawData(
		String analysis,
		String dataset,
		Condition condition,
		Long from,
		Long to
	) throws
		ResultsRepositoryException,
		ModuleSpecificException,
		RemoteException,
		ComponentInitializationException
	{
		return rrDataReference.get().loadData(
			analysis,
			dataset,
			ConditionConvertor.conditionToCondition( condition ),
			from,
			to
		);
	}
	
	/**
	 * Registers a new dataset in the Results Repository's database.
	 * 
	 * @param dataset The JAXB-based representation of the dataset metadata.
	 * @throws ResultsRepositoryException When something bad happens in the Results Repository.
	 * @throws ModuleSpecificException On XML integrity errors.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 */
	public void createDataset( Dataset dataset ) throws
		ResultsRepositoryException,
		ModuleSpecificException,
		RemoteException,
		ComponentInitializationException
	{
		rrManagerReference.get().createDataset(
			dataset.getAnalysis(),
			dataset.getName(),
			DatasetConvertor.datasetToDescriptor( dataset )
		);
	}	

	/**
	 * Stores a row into the Results Repository.
	 * 
	 * @param analysis The analysis where the data belongs.
	 * @param dataset The dataset to write to.
	 * @param tupLit The data itself, in the JAXB-based form.
	 * @throws ResultsRepositoryException When something bad happens in the Results Repository.
	 * @throws ModuleSpecificException On XML integrity errors or (de)serialization problems.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 */
	public Iterable< Long > saveData( String analysis, String dataset, TupLit tupLit ) throws
		ResultsRepositoryException,
		ModuleSpecificException,
		RemoteException,
		ComponentInitializationException
	{
		RRDataInterface rrdi;
		ArrayDeque< Long > result;
		
		result = new ArrayDeque< Long >();
		rrdi = rrDataReference.get();
		if ( tupLit.isSetItems() ) {
			result.add(
				rrdi.saveData(
					analysis,
					dataset,
					TupleConvertor.itemsToTuple( tupLit.getItems() )
				)
			);
		} else {
			for ( Row row : tupLit.getRow() ) {
				result.add(
					rrdi.saveData(
						analysis,
						dataset,
						TupleConvertor.itemsToTuple( row.getItems() )
					)
				);
			}
		}
		return result;
	}
	
	/**
	 * Retrieves one trigger by its UUID.
	 * 
	 * @param triggerID UUID of the trigger to find.
	 * @param binary Whether to use base64 serialization or toString() for object output.
	 * @return An instance representing the trigger.
	 * @throws ResultsRepositoryException When something bad happens in the Results Repository.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 * @throws NoSuchElementException When no such trigger is found.
	 * @throws ModuleSpecificException On XML integrity errors or (de)serialization problems.
	 * @throws RemoteException When it rains.
	 */
	public Trigger getTrigger( UUID triggerID, boolean binary ) throws
		ModuleSpecificException,
		ResultsRepositoryException,
		RemoteException,
		NoSuchElementException,
		ComponentInitializationException
	{
		return TriggerConvertor.rrTriggerToTrigger(
			rrManagerReference.get().getTrigger( triggerID ),
			binary
		);
	}
	
	/**
	 * Creates a new trigger in the Results Repository.
	 * 
	 * @param trigger An XML representation of the trigger to create.
	 * @throws ResultsRepositoryException When something bad happens in the Results Repository.
	 * @throws ModuleSpecificException On XML integrity errors and (de)serialization problems.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 */
	public UUID createTrigger( Trigger trigger ) throws
		ResultsRepositoryException,
		ModuleSpecificException,
		RemoteException,
		ComponentInitializationException
	{
		RRTrigger rrTrigger;
		
		rrTrigger = TriggerConvertor.triggerToRRTrigger( trigger );
		rrManagerReference.get().createTrigger( rrTrigger );
		return rrTrigger.getId();
	}
}
