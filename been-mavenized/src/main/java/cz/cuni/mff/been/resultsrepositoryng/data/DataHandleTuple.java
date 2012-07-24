/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiří Täuber
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
import java.util.Map;
import java.util.TreeMap;

/**
 * Class used for sending data to the RR.
 *
 * @author Jiří Täuber
 * @author Andrej Podzimek
 */
public final class DataHandleTuple implements Serializable {
	private static final long serialVersionUID = 7172381799212708177L;

	/**
	 * Data handle tuple's actual data
	 */
	private TreeMap<String, DataHandle> tuple;

	/* 
	 * Data serial number read from database
	 */
	private Long serial = null;

	public DataHandleTuple() {
		this.tuple = new TreeMap<String, DataHandle>();
	}

	// inserts new name-value pair or overwrites existing one
	/**
	 * Inserts a new name-value pair in the tuple
	 * 
	 * @param name inserted tag name
	 * @param value inserted tag value
	 */
	public void set( String name, DataHandle value ){
		tuple.put(name, value);
	}
	public void set( String name, int value ){
		tuple.put(name, new IntegerDataHandle(value));
	}
	public void set( String name, long value ){
		tuple.put(name, new LongDataHandle(value));
	}
	public void set( String name, float value ){
		tuple.put(name, new FloatDataHandle(value));
	}
	public void set( String name, double value ){
		tuple.put(name, new DoubleDataHandle(value));
	}
	public void set( String name, String value ){
		tuple.put(name, new StringDataHandle(value));
	}
	public void set( String name, byte[] value ){
		tuple.put(name, new ByteArrayDataHandle(value));
	}

	/**
	 * @param name name of the name-value pair
	 * @return value of the requested name-value pair or null on invalid tag
	 */
	public DataHandle get( String name ) {
		return tuple.get(name);
	}

	/**
	 * Method used to get names of the name-value pairs stored in this tuple
	 *
	 * @return iterable set of the "name" parts in this tuple
	 */
	public Iterable<String> getKeys() {
		return tuple.keySet();
	}
	
	
	/**
	 * @return iterable set of name-value pairs
	 */
	public Iterable<Map.Entry<String, DataHandle>> getEntries() {
		return tuple.entrySet();
	}

	public Long getSerial() {
		return serial;
	}

	public void setSerial(Long serial) {
		this.serial = serial;
	}

	/**
	 * Hashcode does not consider value of serial field.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tuple == null) ? 0 : tuple.hashCode());
		return result;
	}

	/**
	 * Equals does not consider value of serial field.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataHandleTuple other = (DataHandleTuple) obj;
		if (tuple == null) {
			if (other.tuple != null)
				return false;
		} else if (!tuple.equals(other.tuple))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return tuple.toString();
	}
}
