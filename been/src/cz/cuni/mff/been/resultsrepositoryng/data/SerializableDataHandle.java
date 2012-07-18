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


/**
 * Data handle referencing directly accessible arbitraty serializable data.
 * 
 * Data handles of type SERIALIZABLE don't define usable equals method.
 * 
 * @author Jan Tattermusch
 *
 */
public class SerializableDataHandle extends DataHandle {

	private static final long serialVersionUID = 45718234462547874L;
	private Serializable data;
	
	public SerializableDataHandle(Serializable data) {
		this.data = data;
	}

	@Override
	public <T> T getValue(Class<T> valueType) throws DataHandleException {
		if (data == null) return null;
		
		if (valueType.isInstance(data)) {
			return valueType.cast(data);
		} else throw new DataHandleException("Cannot extract value of type \"" + valueType.getName() + "\" from data handle.");
	}

	@Override
	public DataType getType() {
		return DataType.SERIALIZABLE;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		SerializableDataHandle other = (SerializableDataHandle) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (data != null) {
			return "SERIALIZABLE_OBJECT";
		} else return "null";
	}
	

}
