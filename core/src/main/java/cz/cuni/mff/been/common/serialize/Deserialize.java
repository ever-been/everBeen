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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import cz.cuni.mff.been.jaxb.td.StrVal;

/**
 * A bunch of static methods for deserialization from XML.
 * 
 * @author Andrej Podzimek
 */
public final class Deserialize {

	/**
	 * Deserializes an object from a Base64 binary representation.
	 * 
	 * @param binVal The Base64 representation obtained from JAXB.
	 * @return A Serializable instance obtained by deserializing the object.
	 * @throws DeserializeException When something goes wrong.
	 */
	public static Serializable fromBase64( byte[] binVal ) throws DeserializeException {
		Serializable result;
		
		try {
			result = (Serializable) new ObjectInputStream(
				new ByteArrayInputStream( binVal )
			).readObject();
		} catch ( IOException exception ) {
			throw new DeserializeException( Errors.FAIL_DESER, exception );
		} catch ( ClassNotFoundException exception ) {
			throw new DeserializeException( Errors.UNKN_CLASS, exception );
		} catch ( ClassCastException exception ) {
			throw new DeserializeException( Errors.INVD_SER, exception );
		}
		
		return result;
	}
	
	/**
	 * Deserializes an object from a String by looking up and invoking a (String) constructor.
	 * This should only be used for objects with well-defined toString(), so that
	 * {@code something.equals(Deserialize.fromString(Serialize.toString(something)))} holds.
	 * 
	 * @param strVal The string representation obtained from JAXB.
	 * @return A Serializable instance obtained by finding and invoking a (String) constructor.
	 * @throws DeserializeException When something goes wrong.
	 */
	public static Serializable fromString( StrVal strVal ) throws DeserializeException {
		Serializable result;

		try {
			result = (Serializable) Class
			.forName( strVal.getClazz() )
			.getConstructor( String.class )
			.newInstance( strVal.getValue() );
		} catch ( IllegalArgumentException exception ) {
			throw new DeserializeException( Errors.INTG_REF, exception );
		} catch ( SecurityException exception ) {
			throw new DeserializeException( Errors.FAIL_SECURITY, exception );
		} catch ( InstantiationException exception ) {
			throw new DeserializeException( Errors.INVD_CLASS, exception );
		} catch ( IllegalAccessException exception ) {
			throw new DeserializeException( Errors.INVD_CTOR, exception );
		} catch ( InvocationTargetException exception ) {
			throw new DeserializeException( Errors.FAIL_CTOR, exception );
		} catch ( NoSuchMethodException exception ) {
			throw new DeserializeException( Errors.UNKN_CTOR, exception );
		} catch ( ClassNotFoundException exception ) {
			throw new DeserializeException( Errors.UNKN_CLASS, exception );
		} catch ( ClassCastException exception ) {
			throw new DeserializeException( Errors.INVD_SER, exception );
		}
		
		return result;
	}
}
