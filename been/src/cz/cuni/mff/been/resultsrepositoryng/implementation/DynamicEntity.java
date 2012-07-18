/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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
package cz.cuni.mff.been.resultsrepositoryng.implementation;

import java.lang.reflect.Field;

import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;

/**
 * Base class for dynamic entities used to work with RR datasets' data 
 * @author Jan Tattermusch
 *
 */
public abstract class DynamicEntity {
	
	/**
	 * Name of special purpose field "serial". Random suffix added to prevent clash with other entity's fields.
	 */
	public static final String SERIAL_FIELD_NAME = "serial_zornldewbt";
	
	/**
	 * Index name prefix (random characters added to prevent name clash 
	 */
	public static final String INDEX_NAME_PREFIX = "idx_akhdwyh";
	
	/**
	 * Maximum length of LOB data 
	 * (derby doesn't provide suitable default)
	 */
	public static final int LOB_MAX_LENGTH = 2000000000;
	
	
	/**
	 * Sets field of given name 
	 * @param fieldName field name
	 * @param value value to set
	 */
	
	public void setField(String fieldName, DataHandle value) {
		try {
			Field f = this.getClass().getDeclaredField(fieldName);
			f.set(this, getDataHandleRawValue(value));
		} catch(Exception e) {
			//TODO: replace by suitable exception
			throw new RuntimeException("Error accessing dynamic entity field", e);
		}
	}
	
	/** Sets serial number field. 
	 * @param serial value
	 */
	public void setSerial(Long serial) {
		try {
			Field f = this.getClass().getDeclaredField(SERIAL_FIELD_NAME);
			f.set(this, serial);
		} catch(Exception e) {
			//TODO: replace by suitable exception
			throw new RuntimeException("Error accessing dynamic entity field", e);
		}
	}
	/**
	 * Gets serial number field. Every dynamic entity has a serial number field
	 * which indicates order in which was entities inserted into results repository.
	 * @return serial number
	 */
	public Long getSerial() {
		try {
			Field f = this.getClass().getDeclaredField(SERIAL_FIELD_NAME);
			return (Long) f.get(this);
		} catch(Exception e) {
			//TODO: replace by suitable exception
			throw new RuntimeException("Error accessing dynamic entity field");
		}
	}
	
	/**
	 * Retrieves field of given name
	 * @param fieldName name of field
	 * @param requestedType requested type of result
	 * @return retrieved data handle
	 */
	public DataHandle getField(String fieldName, DataHandle.DataType requestedType) {
		try {
			Field f = this.getClass().getDeclaredField(fieldName);
			Object fieldValue = f.get(this);
			return getDataHandle(requestedType, fieldValue);
		} catch(Exception e) {
			//TODO: change exception type
			throw new RuntimeException("Error accessing dynamic entity field \"" + fieldName + "\"", e);
		}
	}
	
	
	/**
	 * According to dataset descriptor, reads fields of this entity 
	 * and constructs a data handle tuple from them.
	 * @param datasetDescriptor dataset descriptor which describes this entity
	 * @return data handle tuple constructed from this entity
	 */
	public DataHandleTuple toDataHandleTuple(DatasetDescriptor datasetDescriptor) {
		DataHandleTuple tuple = new DataHandleTuple();
		for (String name : datasetDescriptor.tags()) {
			DataHandle.DataType dataType = datasetDescriptor.get(name);
			tuple.set(name, this.getField(name, dataType));
		}
		tuple.setSerial( this.getSerial() );
		return tuple;
	}
	
	/**
	 * Loads data from data handle tuple to this entity.
	 * 
	 * Goes through data handle tuple's tags and saves them to
	 * this entity.
	 * @param dataHandleTuple data handle tuple to load
	 */
	public void loadDataHandleTuple(DataHandleTuple dataHandleTuple) {
		for (String name : dataHandleTuple.getKeys()) {
			setField(name, dataHandleTuple.get(name));
		}
	}
	
	/**
	 * Retrieves raw value of data handle
	 * @param data data handle to be converted
	 * @return raw data handle value (that can be persisted by hibernate)
	 */
	private Object getDataHandleRawValue(DataHandle data) {
		boolean serialize = data.getType().isPersistSerialized();
		if (!serialize) {
			try {
				return data.getValue(data.getType().getJavaType());
			
			} catch(DataHandleException e) {
				// TODO: replace by appropriate exception
				throw new RuntimeException("Error extracting datahandle's raw value",e);
			}
		} else {
			/* data will stored in serialized form,
			 * just try to load 
			 */
			return data;  
		}
	}
	

	/**
	 * Gets data handle for java object
	 * @param dataType requested data type 
	 * @param value java object to be converted
	 * @return data handle representing java object
	 */
	private DataHandle getDataHandle(DataHandle.DataType dataType, Object value) {
		return DataHandle.create(dataType, value);
	}

}
