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

package cz.cuni.mff.been.resultsrepositoryng.data;


import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * 
 * DataHandle is basic RR data container.
 * 
 * @author Jan Tattermusch
 *
 */
public abstract class DataHandle implements Serializable {
	
	private static final long serialVersionUID = 7993033760571741736L;

	public enum DataType {
		INT(  IntegerDataHandle.class, Integer.class, "Ljava/lang/Integer;" , false),
		
		LONG( LongDataHandle.class, Long.class, "Ljava/lang/Long;", false),
		
		FLOAT(  FloatDataHandle.class, Float.class, "Ljava/lang/Float;", false),
		
		DOUBLE( DoubleDataHandle.class, Double.class, "Ljava/lang/Double;", false ),

		
		STRING(  StringDataHandle.class, String.class, "Ljava/lang/String;", false ),
		
		UUID( UUIDDataHandle.class, UUID.class, "Ljava/util/UUID;", false ),
		
		SMALL_BINARY( ByteArrayDataHandle.class, byte[].class, "Lcz/cuni/mff/been/resultsrepositoryng/data/ByteArrayDataHandle;", true ),
		
		FILE( FileDataHandle.class, UUID.class, "Ljava/util/UUID;", false ),
		
		SERIALIZABLE( SerializableDataHandle.class, Serializable.class, "Lcz/cuni/mff/been/resultsrepositoryng/data/SerializableDataHandle;", true );		
		
		private Class<?> javaType;
		private String javaSignature;
		private Class<? extends DataHandle> dataHandleClass;
		private boolean persistSerialized; 
		
		

		private DataType(  Class<? extends DataHandle> dataHandleClass, Class<?> javaType, String javaSignature, boolean persistSerialized ) {
			this.dataHandleClass = dataHandleClass;
			this.javaType = javaType;
			this.javaSignature = javaSignature;
			this.persistSerialized = persistSerialized;
		}
		
		/**
		 * 
		 * @return data handle class that is native container used for data of this type
		 */
		public Class<? extends DataHandle> getDataHandleClass() {
			return dataHandleClass;
		}
		
		/** 
		 * @return returns java type that can represent content of datahandle.
		 */
		public Class<?> getJavaType() {
			return javaType;
		}
		
		/** 
		 * @return returns java signature of this type when
		 * part of DB entity.
		 */
		public String getJavaSignature() {
			return javaSignature;
		}

		/**
		 * 
		 * @return true if entity is stored in RR in serialized 
		 * form. False means value is stored in database in its 
		 * natural form (and thus searches can be performed
		 * over fields with this types).
		 */	
		public boolean isPersistSerialized() {
			return persistSerialized;
		}
	}
	
	/**
	 * 
	 * @return type of data referenced by this data handle. 
	 */
	public abstract DataType getType();
	
	/**
	 * Extracts value of data referenced by this data handle.
	 * @param <T> 
	 * @param valueType type of result we want to receive
	 * @return value of data handle's data
	 * @throws DataHandleException if something goes wrong
	 */
	public abstract <T> T getValue(Class<T> valueType) throws DataHandleException;

	/**
	 * Equality operator
	 * @param o object to compare with
	 * @return true if object are equal
	 */
	@Override
	public abstract boolean equals(Object o);

	@Override
	public abstract int hashCode();

	/**
	 * creates data handle for java object
	 * @param dataType requested data handle type 
	 * @param value java object to be converted
	 * @return data handle representing java object
	 */
	public static DataHandle create(DataHandle.DataType dataType, Object value) {
		boolean serialized = dataType.isPersistSerialized();
		if (serialized) {
			/* serialized datahandles don't have to be created */
			DataHandle result = (DataHandle) value;
			if (!dataType.equals(result.getType())) {
				throw new IllegalArgumentException("Supplied object has wrong datahandle type");
			}
			return result;
		} else {
			try {
				Class<? extends DataHandle> dataHandleClass = dataType
				.getDataHandleClass();
				Constructor<? extends DataHandle> c = dataHandleClass
				.getConstructor(new Class<?>[] { dataType.getJavaType() });
				DataHandle result = c
				.newInstance(new Object[] { dataType.getJavaType().cast(value) });
				return result;
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(
						"Unexpected error occured when creating data handle.",
						e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(
						"Unexpected error occured when creating data handle.",
						e);
			} catch (InstantiationException e) {
				throw new RuntimeException(
						"Unexpected error occured when creating data handle.",
						e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(
						"Unexpected error occured when creating data handle.",
						e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(
						"Unexpected error occured when creating data handle.",
						e);
			}
		}
	}
}
