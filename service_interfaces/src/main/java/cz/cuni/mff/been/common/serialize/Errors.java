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

import cz.cuni.mff.been.common.Message;

/**
 * Defines internal error codes that can be transformed to external ones using a suitable
 * enum class.
 * 
 * @author Andrej Podzimek
 */
public enum Errors implements Message {

	/** Class deserialization has failed. */
	FAIL_DESER( "Object deserialization failed." ),

	/** Class serialization has failed. */
	FAIL_SER( "Object serialization failed." ),

	/** Trying to deserialize an unknown object. */
	UNKN_CLASS( "Could not find or instantiate the class." ),

	/** Trying to deserialize or serialize something non-Serializable. */
	INVD_SER( "Attempting to deserialize a non-Serializable object." ),

	/** A bug - constructor referenced incorrectly. */
	INTG_REF( "Integrity error. Could not call a (String) constructor." ),

	/** The security manager did not allow instantiation. */
	FAIL_SECURITY( "The (String) constructor is banned by the Security Manager." ),

	/** Trying to instantiate an abstract class. */
	INVD_CLASS( "Attempting to instantiate an abstract class." ),

	/** The constructor exists, but is not accessible. */
	INVD_CTOR( "The (String) constructor is not accessible." ),

	/** The constructor threw an exception. */
	FAIL_CTOR( "The (String) constructor threw an exception." ),

	/** Unknown constructor referenced. */
	UNKN_CTOR( "Could not find a String constructor for the class." );

	/** The message the enum item will convey. */
	private final String message;

	/**
	 * Initializes the enum member with a human-readable error message.
	 *
	 * @param message The error message this enum member will contain.
	 */
	private Errors( String message ) {
		this.message = message;
	}

	@Override
	public final String getMessage() {
		return message;
	}

	/**
	 * Translates a local error enum constant to the one recognized by the calling module.
	 * 
	 * @param <T> The enum in which the target constant has been declared.
	 * @param type The Class object for the target enum.
	 * @return An element of the target enum with the same name as the current constant.
	 */
	< T extends Enum< T > > T error( Class< T > type ) {
		return T.valueOf( type, toString() );
	}
}
