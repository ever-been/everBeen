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



/**
 * Data handle referencing directly accessible float data.
 * 
 * @author Jan Tattermusch
 *
 */
public class FloatDataHandle extends DataHandle {

	private static final long serialVersionUID = 1957704157440756608L;
	private Float data;
	
	public FloatDataHandle(float data) {
		this.data = data;
	}
	
	public FloatDataHandle(Float data) {
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
		return DataType.FLOAT;
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FloatDataHandle other = (FloatDataHandle) obj;
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
			return data.toString();
		} else return "null";
	}

}
