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
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import javax.xml.bind.JAXBElement;

import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.clinterface.modules.ResultsModule.Errors;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.jaxb.Factory;
import cz.cuni.mff.been.jaxb.dataset.Dataset;
import cz.cuni.mff.been.jaxb.dataset.Item;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle.DataType;

/**
 * A bunch of static methods for data structure conversion. 
 * 
 * @author Andrej Podzimek
 */
final class DatasetConvertor {

	/**
	 * A callback interface for logical operators.
	 * 
	 * @author Andrej Podzimek
	 */
	private static interface DatasetInterface {
		
		/**
		 * Translates XML-based tuple literal item to its DatasetDescriptor counterpart.
		 * 
		 * @param item The item obtained from XML to translate.
		 * @param descriptor The output descriptor to which item data will be stored.
		 * @throws ModuleSpecificException When a severe XML integrity error is encountered.
		 */
		void convert( Item item, DatasetDescriptor descriptor )
		throws ModuleSpecificException;
	}
	
	/** Map of item convertors. */
	private static final TreeMap< String, DatasetInterface > itemMap;
	
	static {
		itemMap = new TreeMap< String, DatasetInterface >();
		itemMap.put(
			"int",
			new DatasetInterface() {
				
				@Override
				public void convert( Item item, DatasetDescriptor descriptor )
				throws ModuleSpecificException {
					descriptor.put( item.getName(), DataType.INT, item.isKey() );
				}
			}
		);
		itemMap.put(
			"long",
			new DatasetInterface() {
				
				@Override
				public void convert( Item item, DatasetDescriptor descriptor )
				throws ModuleSpecificException {
					descriptor.put( item.getName(), DataType.LONG, item.isKey() );
				}
			}
		);
		itemMap.put(
			"float",
			new DatasetInterface() {
				
				@Override
				public void convert( Item item, DatasetDescriptor descriptor )
				throws ModuleSpecificException {
					descriptor.put( item.getName(), DataType.FLOAT, item.isKey() );
				}
			}
		);
		itemMap.put(
			"double",
			new DatasetInterface() {
				
				@Override
				public void convert( Item item, DatasetDescriptor descriptor )
				throws ModuleSpecificException {
					descriptor.put( item.getName(), DataType.DOUBLE, item.isKey() );
				}
			}
		);
		itemMap.put(
			"string",
			new DatasetInterface() {
				
				@Override
				public void convert( Item item, DatasetDescriptor descriptor )
				throws ModuleSpecificException {
					descriptor.put( item.getName(), DataType.STRING, item.isKey() );
				}
			}
		);
		itemMap.put(
			"uuid",
			new DatasetInterface() {
				
				@Override
				public void convert( Item item, DatasetDescriptor descriptor )
				throws ModuleSpecificException {
					descriptor.put( item.getName(), DataType.UUID, item.isKey() );
				}
			}
		);
		itemMap.put(
			"file",
			new DatasetInterface() {
				
				@Override
				public void convert( Item item, DatasetDescriptor descriptor )
				throws ModuleSpecificException {
					descriptor.put( item.getName(), DataType.FILE, item.isKey() );
				}
			}
		);
		itemMap.put(
			"binary",
			new DatasetInterface() {
				
				@Override
				public void convert( Item item, DatasetDescriptor descriptor )
				throws ModuleSpecificException {
					descriptor.put( item.getName(), DataType.SMALL_BINARY, item.isKey() );
				}
			}
		);
		itemMap.put(
			"serializable",
			new DatasetInterface() {
				
				@Override
				public void convert( Item item, DatasetDescriptor descriptor )
				throws ModuleSpecificException {
					descriptor.put( item.getName(), DataType.SERIALIZABLE, item.isKey() );
				}
			}
		);
	}
	
	/**
	 * Don't do this.
	 */
	private DatasetConvertor() {
	}
	
	/**
	 * Converts a JAXB-based Dataset descriptor to a real DatasetDescriptor.
	 * 
	 * @param dataset The Dataset to convert. (This is in fact a database table definition.)
	 * @return A DatasetDescriptor representing the data from the Dataset.
	 * @throws ModuleSpecificException When an XML integrity error is encountered. 
	 */
	static DatasetDescriptor datasetToDescriptor( Dataset dataset )
	throws ModuleSpecificException {
		DatasetDescriptor result;
		DatasetInterface convertor;
		
		result = new DatasetDescriptor();
		for ( JAXBElement< Item > element : dataset.getItems() ) {
			convertor = itemMap.get( element.getName().getLocalPart() );
			if ( null == convertor ) {
				throw new ModuleSpecificException(
					Errors.INTG_TUPLIT,
					" (" + element.getName().toString() + ')'
				);
			}
			convertor.convert( element.getValue(), result );
		}
		return result;
	}
	
	/**
	 * Dataset JAXB structure getter.
	 * 
	 * @param descriptor The dataset descriptor to read from.
	 * @param analysis Name of the analysis. (Not contained in the descriptor.)
	 * @param dataset Name of the dataset. (Not contained in the descriptor.)
	 * @return A JAXB-annotated data structure representing the dataset.
	 * @throws ResultsRepositoryException When something bad happens in the Results Repository.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 */
	static Dataset getDataset( DatasetDescriptor descriptor, String analysis, String dataset )
	throws ResultsRepositoryException, RemoteException, ComponentInitializationException {
		Dataset datasetInstance;
		Collection< String > keys;
		List< JAXBElement< Item > > items;
		JAXBElement< Item > itemElement;
		Item item;
		
		keys = descriptor.idTags();
		datasetInstance = Factory.DATASET.createDataset();
		datasetInstance.setName( dataset );
		datasetInstance.setAnalysis( analysis );
		items = datasetInstance.getItems();
		for ( String tag : descriptor.tags() ) {
			item = Factory.DATASET.createItem();
			item.setName( tag );
			item.setKey( keys.contains( tag ) );
			switch ( descriptor.get( tag ) ) {
				case DOUBLE:
					itemElement = Factory.DATASET.createDouble( item );
					break;
				case FILE:
					itemElement = Factory.DATASET.createFile( item );
					break;
				case FLOAT:
					itemElement = Factory.DATASET.createFloat( item );
					break;
				case INT:
					itemElement = Factory.DATASET.createInt( item );
					break;
				case LONG:
					itemElement = Factory.DATASET.createLong( item );
					break;
				case SERIALIZABLE:
					itemElement = Factory.DATASET.createSerializable( item );
					break;
				case SMALL_BINARY:
					itemElement = Factory.DATASET.createBinary( item );
					break;
				case STRING:
					itemElement = Factory.DATASET.createString( item );
					break;
				case UUID:
					itemElement = Factory.DATASET.createUuid( item );
					break;
				default:
					itemElement = null;																// This should never happen.
			}
			items.add( itemElement );
		}
		return datasetInstance;
	}
}
