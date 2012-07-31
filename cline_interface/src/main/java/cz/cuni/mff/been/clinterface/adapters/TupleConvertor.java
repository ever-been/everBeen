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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;

import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.clinterface.modules.ResultsModule.Errors;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.jaxb.AbstractSerializable;
import cz.cuni.mff.been.jaxb.Factory;
import cz.cuni.mff.been.jaxb.tuplit.NamedBinary;
import cz.cuni.mff.been.jaxb.tuplit.NamedDouble;
import cz.cuni.mff.been.jaxb.tuplit.NamedFloat;
import cz.cuni.mff.been.jaxb.tuplit.NamedInt;
import cz.cuni.mff.been.jaxb.tuplit.NamedLong;
import cz.cuni.mff.been.jaxb.tuplit.NamedString;
import cz.cuni.mff.been.jaxb.tuplit.NamedUUID;
import cz.cuni.mff.been.jaxb.tuplit.Row;
import cz.cuni.mff.been.jaxb.tuplit.TupLit;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.data.ByteArrayDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.data.DoubleDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.FileDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.FloatDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.IntegerDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.LongDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.SerializableDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.StringDataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.UUIDDataHandle;

/**
 * A bunch of static methods for data structure conversion. 
 * 
 * @author Andrej Podzimek
 */
final class TupleConvertor {

	/**
	 * A callback interface for logical operators.
	 * 
	 * @author Andrej Podzimek
	 */
	private static interface TupleInterface {
		
		/**
		 * Translates XML-based tuple literal item to its DataHandleTuple counterpart.
		 * 
		 * @param item The item obtained from XML to translate.
		 * @param tuple The output tuple to which item data will be stored.
		 * @throws ModuleSpecificException When a severe XML integrity error is encountered.
		 */
		void convert( JAXBElement< ? extends AbstractSerializable > item, DataHandleTuple tuple )
		throws ModuleSpecificException;
	}
	
	/** Map of item convertors. */
	private static final TreeMap< String, TupleInterface > itemMap;
	
	static {
		itemMap = new TreeMap< String, TupleInterface >();
		
		itemMap.put(
			"int",
			new TupleInterface() {

				@Override
				public void convert(
					JAXBElement< ? extends AbstractSerializable > item,
					DataHandleTuple tuple
				) throws ModuleSpecificException {
					NamedInt value;
					
					value = (NamedInt) item.getValue();
					if ( item.isNil() ) {
						tuple.set( value.getName(), new IntegerDataHandle( null ) );
					} else {
						tuple.set( value.getName(), value.getValue() );
					}
				}
			}
		);
		itemMap.put(
			"long",
			new TupleInterface() {

				@Override
				public void convert(
					JAXBElement< ? extends AbstractSerializable > item,
					DataHandleTuple tuple
				) throws ModuleSpecificException {
					NamedLong value;
					
					value = (NamedLong) item.getValue();
					if ( item.isNil() ) {
						tuple.set( value.getName(), new LongDataHandle( null ) );
					} else {
						tuple.set( value.getName(), value.getValue() );
					}
				}
			}
		);
		itemMap.put(
			"float",
			new TupleInterface() {

				@Override
				public void convert(
					JAXBElement< ? extends AbstractSerializable > item,
					DataHandleTuple tuple
				) throws ModuleSpecificException {
					NamedFloat value;
					
					value = (NamedFloat) item.getValue();
					if ( item.isNil() ) {
						tuple.set( value.getName(), new FloatDataHandle( null ) );
					} else {
						tuple.set( value.getName(), value.getValue() );
					}
				}
			}
		);
		itemMap.put(
			"double",
			new TupleInterface() {

				@Override
				public void convert(
					JAXBElement< ? extends AbstractSerializable > item,
					DataHandleTuple tuple
				) throws ModuleSpecificException {
					NamedDouble value;
					
					value = (NamedDouble) item.getValue();
					if ( item.isNil() ) {
						tuple.set( value.getName(), new DoubleDataHandle( null ) );
					} else {
						tuple.set( value.getName(), value.getValue() );
					}
				}
			}
		);
		itemMap.put(
			"string",
			new TupleInterface() {

				@Override
				public void convert(
					JAXBElement< ? extends AbstractSerializable > item,
					DataHandleTuple tuple
				) throws ModuleSpecificException {
					NamedString value;
					
					value = (NamedString) item.getValue();
					if ( item.isNil() ) {
						tuple.set( value.getName(), new StringDataHandle( null ) );
					} else {
						tuple.set( value.getName(), value.getValue() );
					}
				}
			}
		);
		itemMap.put(
			"uuid",
			new TupleInterface() {

				@Override
				public void convert(
					JAXBElement< ? extends AbstractSerializable > item,
					DataHandleTuple tuple
				) throws ModuleSpecificException {
					NamedUUID value;
					
					value = (NamedUUID) item.getValue();
					if ( item.isNil() ) {
						tuple.set( value.getName(), new UUIDDataHandle( null ) );
					} else {
						tuple.set(
							value.getName(),
							new UUIDDataHandle( value.getValue() )
						);
					}
				}
			}
		);
		itemMap.put(
			"file",
			new TupleInterface() {

				@Override
				public void convert(
					JAXBElement< ? extends AbstractSerializable > item,
					DataHandleTuple tuple
				) throws ModuleSpecificException {
					NamedUUID value;
					
					value = (NamedUUID) item.getValue();
					if ( item.isNil() ) {
						tuple.set( value.getName(), new FileDataHandle( null ) );
					} else {
						tuple.set(
							value.getName(),
							new FileDataHandle( value.getValue() )
						);
					}
				}
			}
		);
		itemMap.put(
			"binary",
			new TupleInterface() {

				@Override
				public void convert(
					JAXBElement< ? extends AbstractSerializable > item,
					DataHandleTuple tuple
				) throws ModuleSpecificException {
					NamedBinary value;
					
					value = (NamedBinary) item.getValue();
					if ( item.isNil() ) {
						tuple.set( value.getName(), new ByteArrayDataHandle( null ) );
					} else {
						tuple.set( value.getName(), value.getValue() );
					}
				}
			}
		);
		itemMap.put(
			"serializable",
			new TupleInterface() {

				@Override
				public void convert(
					JAXBElement< ? extends AbstractSerializable > item,
					DataHandleTuple tuple
				) throws ModuleSpecificException {
					NamedBinary value;
					
					value = (NamedBinary) item.getValue();
					if ( item.isNil() ) {
						tuple.set( value.getName(), new SerializableDataHandle( null ) );
					} else {
						try {
							tuple.set(
								value.getName(),
								new SerializableDataHandle(
									(Serializable) new ObjectInputStream(
										new ByteArrayInputStream( value.getValue() )
									).readObject()
								)
							);
						} catch ( IOException exception ) {
							throw new ModuleSpecificException(
								Errors.FAIL_DESER,
								" (" + value.getName() + ')',
								exception
							);
						} catch ( ClassNotFoundException exception ) {
							throw new ModuleSpecificException(
								Errors.UNKN_CLASS,
								" (" + value.getName() + ')',
								exception
							);
						}
					}
				}
			}
		);
	}
	
	/**
	 * Don't do this.
	 */
	private TupleConvertor() {
	}
	
	/* From XML to internal representation. */
	
	/**
	 * Converts a list of JAXB-based data handle tuple items to a real DataHandleTuple.
	 * 
	 * @param items The items to convert. (This is in fact a database table definition.)
	 * @return A DataHandleTuple representing the data from the items.
	 * @throws ModuleSpecificException When an XML integrity error is encountered. 
	 */
	static DataHandleTuple itemsToTuple(
		Iterable< JAXBElement< ? extends AbstractSerializable > > items
	) throws ModuleSpecificException {
		DataHandleTuple result;
		TupleInterface convertor;
		
		result = new DataHandleTuple();
		for ( JAXBElement< ? extends AbstractSerializable > item : items ) {
			convertor = itemMap.get( item.getName().getLocalPart() );
			if ( null == convertor ) {
				throw new ModuleSpecificException(
					Errors.INTG_TUPLIT,
					" (" + item.getName().toString() + ')'
				);
			}
			convertor.convert( item, result );
		}
		return result;
	}
	
	/* From internal representation to XML. */
	
	/**
	 * TupLit JAXB structure getter. Transforms data tuples into their JAXB-based representation.
	 * 
	 * @param tuples A list of the tuples to transform.
	 * @return A JAXB-annotated data structure representing the dataset's content.
	 * @throws ResultsRepositoryException When something bad happens in the Results Repository.
	 * @throws ModuleSpecificException On XML integrity errors or (de)serialization problems.
	 * @throws RemoteException When it rains.
	 * @throws ComponentInitializationException When the Service Reference reports a failure.
	 */
	static TupLit tuplesToTupLit( List< DataHandleTuple > tuples ) throws
		ResultsRepositoryException,
		ModuleSpecificException,
		RemoteException,
		ComponentInitializationException
	{
		TupLit result;
		List< Row > rows;
		
		result = Factory.TUPLIT.createTupLit();
		switch ( tuples.size() ) {
			case 0:
				break;
			default:
				rows = result.getRow();
				for ( DataHandleTuple tuple : tuples ) {
					rows.add( tupleToRow( tuple ) );
				}
				break;
		}
		return result;
	}
	
	/**
	 * Converts a data handle tuple to its JAXB-based representation. This method creates a Row
	 * instance that can be included in a tuple literal.
	 * 
	 * @param tuple The data handle tuple to convert. It must not be empty.
	 * @return A JAXB-annotated data structure representing named contents of the tuple.
	 * @throws ModuleSpecificException On XML integrity errors or (de)serialization problems.
	 */
	private static Row tupleToRow( DataHandleTuple tuple ) throws ModuleSpecificException {
		Row result;
		
		result = Factory.TUPLIT.createRow();
		result.setSerial( tuple.getSerial() );
		tupleToItems( tuple, result.getItems() );
		return result;
	}
	
	/**
	 * Converts a data handle tuple into a JAXB based list of data column definitions.
	 * 
	 * @param tuple The data handle tuple to convert. It must not be empty.
	 * @param items The (output) list of items obtained either from a TupLit or from a Row.
	 * @throws ModuleSpecificException On XML integrity errors or (de)serialization problems.
	 */
	private static void tupleToItems(
		DataHandleTuple tuple,
		List< JAXBElement< ? extends AbstractSerializable > > items
	) throws ModuleSpecificException {
		DataHandle handle;
		
		for ( Entry< String, DataHandle > entry : tuple.getEntries() ) {
			handle = entry.getValue();
			switch ( handle.getType() ) {
				case DOUBLE: {
					JAXBElement< NamedDouble > element;
					NamedDouble named;
					Double raw;
					
					named = Factory.TUPLIT.createNamedDouble();
					named.setName( entry.getKey() );
					try {
						raw = handle.getValue( Double.class );
						element = Factory.TUPLIT.createDouble( named );
						if ( null == raw ) {
							element.setNil( true );
						} else {
							named.setValue( raw );
						}
						items.add( element );
					} catch ( DataHandleException exception ) {
						throw new ModuleSpecificException(
							Errors.INTG_TYPE,
							" (" + entry.getKey() + ')',
							exception
						);
					}
					break;
				}
				case FILE: {
					JAXBElement< NamedUUID > element;
					NamedUUID named;
					UUID raw;
					
					named = Factory.TUPLIT.createNamedUUID();
					named.setName( entry.getKey() );
					try {
						raw = handle.getValue( UUID.class );
						element = Factory.TUPLIT.createFile( named );
						if ( null == raw ) {
							element.setNil( true );
						} else {
							named.setValue( raw );
						}
						items.add( element );
					} catch ( DataHandleException exception ) {
						throw new ModuleSpecificException(
							Errors.INTG_TYPE,
							" (" + entry.getKey() + ')',
							exception
						);
					}
					break;
				}
				case FLOAT: {
					JAXBElement< NamedFloat > element;
					NamedFloat named;
					Float raw;
					
					named = Factory.TUPLIT.createNamedFloat();
					named.setName( entry.getKey() );
					try {
						raw = handle.getValue( Float.class );
						element = Factory.TUPLIT.createFloat( named );
						if ( null == raw ) {
							element.setNil( true );
						} else {
							named.setValue( raw );
						}
						items.add( element );
					} catch ( DataHandleException exception ) {
						throw new ModuleSpecificException(
							Errors.INTG_TYPE,
							" (" + entry.getKey() + ')',
							exception
						);
					}
					break;
				}
				case INT: {
					JAXBElement< NamedInt > element;
					NamedInt named;
					Integer raw;
					
					named = Factory.TUPLIT.createNamedInt();
					named.setName( entry.getKey() );
					try {
						raw = handle.getValue( Integer.class );
						element = Factory.TUPLIT.createInt( named );
						if ( null == raw ) {
							element.setNil( true );
						} else {
							named.setValue( raw );
						}
						items.add( element );
					} catch ( DataHandleException exception ) {
						throw new ModuleSpecificException(
							Errors.INTG_TYPE,
							" (" + entry.getKey() + ')',
							exception
						);
					}
					break;
				}
				case LONG: {
					JAXBElement< NamedLong > element;
					NamedLong named;
					Long raw;
					
					named = Factory.TUPLIT.createNamedLong();
					named.setName( entry.getKey() );
					try {
						raw = handle.getValue( Long.class );
						element = Factory.TUPLIT.createLong( named );
						if ( null == raw ) {
							element.setNil( true );
						} else {
							named.setValue( raw );
						}
						items.add( element );
					} catch ( DataHandleException exception ) {
						throw new ModuleSpecificException(
							Errors.INTG_TYPE,
							" (" + entry.getKey() + ')',
							exception
						);
					}
					break;
				}
				case SERIALIZABLE: {
					JAXBElement< NamedBinary > element;
					NamedBinary named;
					Serializable raw;
					
					named = Factory.TUPLIT.createNamedBinary();
					named.setName( entry.getKey() );
					try {
						raw = handle.getValue( Serializable.class );
						element = Factory.TUPLIT.createSerializable( named );
						if ( null == raw ) {
							element.setNil( true );
						} else {
							ByteArrayOutputStream byteStream;
							ObjectOutputStream objectStream;
							
							byteStream = new ByteArrayOutputStream();
							objectStream = new ObjectOutputStream( byteStream );
							objectStream.writeObject( raw );
							objectStream.close();
							named.setValue( byteStream.toByteArray() );
						}
						items.add( element );
					} catch ( DataHandleException exception ) {
						throw new ModuleSpecificException(
							Errors.INTG_TYPE,
							" (" + entry.getKey() + ')',
							exception
						);
					} catch ( IOException exception ) {
						throw new ModuleSpecificException(
							Errors.FAIL_SER,
							" (" + entry.getKey() + ')',
							exception
						);
					}
					break;
				}
				case SMALL_BINARY: {
					JAXBElement< NamedBinary > element;
					NamedBinary named;
					byte[] raw;
					
					named = Factory.TUPLIT.createNamedBinary();
					named.setName( entry.getKey() );
					try {
						raw = handle.getValue( byte[].class );
						element = Factory.TUPLIT.createBinary( named );
						if ( null == raw ) {
							element.setNil( true );
						} else {
							named.setValue( raw );
						}
						items.add( element );
					} catch ( DataHandleException exception ) {
						throw new ModuleSpecificException(
							Errors.INTG_TYPE,
							" (" + entry.getKey() + ')',
							exception
						);
					}
					break;
				}
				case STRING: {
					JAXBElement< NamedString > element;
					NamedString named;
					String raw;
					
					named = Factory.TUPLIT.createNamedString();
					named.setName( entry.getKey() );
					try {
						raw = handle.getValue( String.class );
						element = Factory.TUPLIT.createString( named );
						if ( null == raw ) {
							element.setNil( true );
						} else {
							named.setValue( raw );
						}
						items.add( element );
					} catch ( DataHandleException exception ) {
						throw new ModuleSpecificException(
							Errors.INTG_TYPE,
							" (" + entry.getKey() + ')',
							exception
						);
					}
					break;
				}
				case UUID: {
					JAXBElement< NamedUUID > element;
					NamedUUID named;
					UUID raw;
					
					named = Factory.TUPLIT.createNamedUUID();
					named.setName( entry.getKey() );
					try {
						raw = handle.getValue( UUID.class );
						element = Factory.TUPLIT.createUuid( named );
						if ( null == raw ) {
							element.setNil( true );
						} else {
							named.setValue( raw );
						}
						items.add( element );
					} catch ( DataHandleException exception ) {
						throw new ModuleSpecificException(
							Errors.INTG_TYPE,
							" (" + entry.getKey() + ')',
							exception
						);
					}
					break;
				}
			}
		}
	}	
}
