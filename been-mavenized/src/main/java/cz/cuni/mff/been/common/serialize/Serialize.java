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
package cz.cuni.mff.been.common.serialize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import cz.cuni.mff.been.jaxb.Factory;
import cz.cuni.mff.been.jaxb.td.StrVal;

/**
 * A bunch of static methods for serialization to XML.
 * 
 * @author Andrej Podzimek
 */
public final class Serialize {
	
	/**
	 * Converts an object to an XML representation consisting of a Base64 encoded serialized
	 * instance.
	 * 
	 * @param object The object to convert.
	 * @return A byte array representation of the object, a serialized instance.
	 * @throws IOException
	 */
	public static byte[] toBase64( Serializable object ) throws IOException {
		ObjectOutputStream objectStream;
		ByteArrayOutputStream byteStream;
		
		byteStream = new ByteArrayOutputStream();
		objectStream = new ObjectOutputStream( byteStream );
		objectStream.writeObject( object );
		objectStream.close();
		return byteStream.toByteArray();
	}
	
	/**
	 * Converts and object to an XML representation consisting of the values of toString() and
	 * getClass().getName(). This should only be used for objects with well-defined toString(),
	 * so that {@code something.equals(Deserialize.fromString(Serialize.toString(something)))}
	 * holds.
	 * 
	 * @param object The object to convert.
	 * @return A XML representation containing string equivalents of the object.
	 */
	public static StrVal toString( Serializable object ) {
		StrVal result;
		
		result = Factory.TD.createStrVal();
		result.setClazz( object.getClass().getName() );
		result.setValue( String.valueOf( object ) );
		return result;
	}
}
