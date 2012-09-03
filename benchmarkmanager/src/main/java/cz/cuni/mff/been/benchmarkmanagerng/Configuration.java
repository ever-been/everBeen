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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Class used to pass configuration between BM components.
 * Configuration is simple set of name-values (String-String[]) pairs.
 * The String array in the value is for the purpose of multiselects. 
 * Validation is performed by the one who needs the value.
 * 
 *  @author Jiri Tauber
 */
/**
 * @author BoRiS
 *
 */
public class Configuration implements Serializable {
	private static final long serialVersionUID = -7108813774092892857L;

	/** The key-value mapping */
	private HashMap<String, String[]> tuple = new HashMap<String, String[]>();

	//----------------------------------------//

	private Integer hashCode;
	
	/**
	 * @return Iterable list of keys
	 */
	public Iterable<String> keySet(){
		return tuple.keySet();
	}
	
	/**
	 * @return key-values mappings in an iterable set.
	 */
	public Iterable<Entry<String, String[]>> entrySet(){
		return tuple.entrySet();
	}

	/**
	 * Sets the value list for a key in the configuration
	 * @param name The configuration key
	 * @param values The configuration values
	 */
	public void set(String name, String[] values){
		tuple.put(name, values);
		hashCode = null;
	}
	
	/**
	 * @param name The configuration key
	 * @param index The index of the value in the 
	 * @return The n-th value in the configuration field or null if it's not there.
	 * @throws IndexOutOfBoundsException if the key exists but doesn't have enough values
	 */
	public String get(String name, int index){
		String[] retval = tuple.get(name);
		if( retval == null ){
			return null;
		} else {
			return retval[index];
		}
	}
	
	/**
	 * @param name The configuration key
	 * @return All the configuration values.
	 */
	public String[] get(String name){
		return tuple.get(name);
	}
	// TODO It might be useful to implement getters which would return different types.


	@Override
	public boolean equals(Object obj) {
		if( !(obj instanceof Configuration) ) return false;
		Configuration other = (Configuration)obj;

		if( this.tuple.size() != other.tuple.size() ) return false;
		for(String key : this.tuple.keySet()) {
			if( !other.tuple.containsKey(key) ) return false;
			if( !Arrays.equals(this.tuple.get(key), other.tuple.get(key)) ) return false;
		}
		// no need to check it the other way around 

		return true;
	}

	@Override
	public int hashCode() {
		if (null == hashCode) {
			int newHashCode = -1 | tuple.size();
			
			for (Entry<String, String[]> entry : tuple.entrySet()) {
				newHashCode |= entry.getKey().hashCode() | Arrays.hashCode(entry.getValue());
			}
			hashCode = newHashCode;
		}
		return hashCode;
	}

}
