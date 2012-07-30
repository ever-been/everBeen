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
package cz.cuni.mff.been.clinterface.writers;

import java.io.IOException;
import java.util.regex.Pattern;

import cz.cuni.mff.been.clinterface.CommandLineResponse;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.database.NameValuePair;
import cz.cuni.mff.been.hostmanager.database.PropertyDescription;
import cz.cuni.mff.been.hostmanager.database.PropertyDescriptionTable;
import cz.cuni.mff.been.hostmanager.database.PropertyTreeReadInterface;

/**
 * A Writer that outputs data from PropertyTreeReadInterface instances.
 * 
 * @author Andrej Podzimek
 */
public final class PropertyTreeWriter extends AbstractWriter {

	/**
	 * Initializes a new (and empty) writer.
	 * 
	 * @param response The response to which all the write operations will be relayed.
	 */
	public PropertyTreeWriter( CommandLineResponse response ) {
		super( response );
	}

	/**
	 * Outputs a property object recursively with no filtering and no descriptions.
	 * 
	 * @param object The property object to traverse.
	 * @param indent Tree depth and indentation level.
	 * @throws IOException When it rains.
	 */
	public void sendObjPlain( PropertyTreeReadInterface object, int indent ) throws IOException {
		final int childIndent = indent + 1;
		
		for ( int i = 0; i < indent; ++i ) {
			builder().append( '\t' );
		}
		builder().append( object.getName( false ) ).append( '\n' );
		sendOut();

		for ( NameValuePair property : object.getProperties() ) {
			sendPropPlain( property, childIndent );
		}
		for ( PropertyTreeReadInterface child : object.getObjects() ) {
			sendObjPlain( child, childIndent );
		}
	}

	/**
	 * Outputs a property object recursively with filtering and no descriptions.
	 * 
	 * @param object The property object to traverse.
	 * @param indent Tree depth and indentation level.
	 * @param cpattern The regular expression for node name filtering.
	 * @throws IOException When it rains.
	 */
	public void sendObjFiltr(
		PropertyTreeReadInterface object,
		int indent,
		Pattern cpattern
	) throws IOException {
		final int childIndent = indent + 1;
		final String pathName = object.getName( true );
		final String childPathName = pathName + '.';
		
		for ( int i = 0; i < indent; ++i ) {
			builder().append( '\t' );
		}
		builder().append( object.getName( false ) ).append( '\n' );
		sendOut();
							
		for ( NameValuePair property : object.getProperties() ) {
			if ( cpattern.matcher( childPathName + property.getName() ).matches() ) {
				sendPropPlain( property, childIndent );
			}
		}					
		for ( PropertyTreeReadInterface child : object.getObjects() ) {
			sendObjFiltr( child, childIndent, cpattern );
		}
	}

	/**
	 * Outputs a property object recursively with no filtering and descriptions.
	 * 
	 * @param object The property object to traverse.
	 * @param indent Tree depth and indentation level.
	 * @param table The BEEN's table of property descriptions.
	 * @throws IOException When it rains.
	 */
	public void sendObjDescr(
		PropertyTreeReadInterface object,
		int indent,
		PropertyDescriptionTable table
	) throws IOException {
		final int childIndent = indent + 1;
		final String pathName = object.getName( true );
		final String childPathName = pathName + '.';
		PropertyDescription description;
		
		try {
			description = table.getDescription( pathName );
		} catch ( ValueNotFoundException exception ) {
			description = null;
		}
		
		for ( int i = 0; i < indent; ++i ) {
			builder().append( '\t' );
		}
		builder().append( object.getName( false ) );					
		if ( null == description ) {
			builder().append( '\n' );
		} else {
			builder()
			.append( " [" ).append( literal( description.getUnit() ) ).append( "] " )
			.append( quotedLiteral( description.getDescription() ) )
			.append( " (" ).append( description.getType() ).append( ")\n" ); 
		}
		sendOut();
		
		for ( NameValuePair property : object.getProperties() ) {
			sendPropDescr( property, childIndent, table, childPathName );
		}
		for ( PropertyTreeReadInterface child : object.getObjects() ) {
			sendObjDescr( child, childIndent, table );
		}
	}

	/**
	 * Outputs a property object recursively with filering and descriptions.
	 * 
	 * @param object The property object to traverse.
	 * @param indent Tree depth and indentation level.
	 * @param table The BEEN's table of property descriptions.
	 * @param cpattern The regular expression for node name filtering.
	 * @throws IOException When it rains.
	 */
	public void sendObjFilDs(
		PropertyTreeReadInterface object,
		int indent,
		PropertyDescriptionTable table,
		Pattern cpattern
	) throws IOException {
		final int childIndent = indent + 1;
		final String pathName = object.getName( true );
		final String childPathName = pathName + '.';
		PropertyDescription description;

		try {
			description = table.getDescription( pathName );
		} catch ( ValueNotFoundException exception ) {
			description = null;
		}
		
		for ( int i = 0; i < indent; ++i ) {
			builder().append( '\t' );
		}
		builder().append( object.getName( false ) );
		if ( null == description ) {
			builder().append( '\n' );
		} else {
			builder()
			.append( " [" ).append( literal( description.getUnit() ) ).append( "] " )
			.append( quotedLiteral( description.getDescription() ) )
			.append( " (" ).append( description.getType() ).append( ")\n" ); 
		}
		sendOut();
		
		for ( NameValuePair property : object.getProperties() ) {
			if ( cpattern.matcher( childPathName + property.getName() ).matches() ) {
				sendPropDescr( property, childIndent, table, childPathName );
			}
		}
		for ( PropertyTreeReadInterface child : object.getObjects() ) {
			sendObjFilDs( child, childIndent, table, cpattern );
		}
	}

	/**
	 * Outputs a single property in a short form with no description.
	 * 
	 * @param property The property entry to output.
	 * @param indent Tree depth and indentation level.
	 * @throws IOException When it rains.
	 */
	public void sendPropPlain( NameValuePair property, int indent ) throws IOException {
		for ( int i = 0; i < indent; ++i ) {
			builder().append( '\t' );
		}					
		builder()
		.append( property.getName() ).append( ' ' )
		.append( quotedLiteral( String.valueOf( property.getValue() ) ) ).append( ' ' )
		.append( property.getValue().getClass().getSimpleName() ).append( '\n' );
		sendOut();
	}

	/**
	 * Outputs a single property in a long for with a description.
	 * 
	 * @param property The property entry to output.
	 * @param indent Tree depth and indentation level.
	 * @param table The BEEN's table of property descriptions.
	 * @param parent Path to the parent node for path name synthesis.
	 * @throws IOException When it rains.
	 */
	public void sendPropDescr(
		NameValuePair property,
		int indent,
		PropertyDescriptionTable table,
		String parent
	) throws IOException {
		final String pathName = parent + property.getName();
		PropertyDescription description;
		
		try {
			description = table.getDescription( pathName );
		} catch ( ValueNotFoundException exception ) {
			description = null;
		}
		
		for ( int i = 0; i < indent; ++i ) {
			builder().append( '\t' );
		}					
		builder()
		.append( property.getName() ).append( ' ' )
		.append( quotedLiteral( String.valueOf( property.getValue() ) ) ).append( ' ' )
		.append( property.getValue().getClass().getSimpleName() );
		if ( null == description ) {
			builder().append( '\n' );
		} else {
			builder()
			.append( " [" ).append( literal( description.getUnit() ) ).append( "] " )
			.append( quotedLiteral( description.getDescription() ) )
			.append( " (" ).append( description.getType() ).append( ")\n" ); 
		}
		sendOut();
	}
}
