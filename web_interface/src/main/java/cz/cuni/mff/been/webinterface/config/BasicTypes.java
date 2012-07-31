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
package cz.cuni.mff.been.webinterface.config;

import java.util.TreeMap;

import javax.servlet.ServletContext;

import cz.cuni.mff.been.jaxb.config.Item;
import cz.cuni.mff.been.webinterface.Routines;

/**
 * This enum lists a few trivial built-in data types and their transcoders.
 * 
 * @author Andrej Podzimek
 */
public enum BasicTypes implements ItemToXHTML {

	CHECKBOX( "checkbox" ) {

		@Override
		public String toXHTML( ServletContext application, String prefix, Item item ) {
			StringBuilder builder = new StringBuilder();
			String fullName;
			String escId;
			String escFullName;
			String escDesc;
			String checked;
			
			fullName = prefix + item.getId();
			escId = Routines.htmlspecialchars( item.getId() );
			escFullName = prefix + escId;
			escDesc = Routines.htmlspecialchars( item.getDesc() );
			
			if ( null == application.getAttribute( fullName ) ) {									// We don't care about the value.
				checked = strBool( item.getDefault() ) ? "\" checked=\"checked\"" : "\"";
			} else {
				checked = "\" checked=\"checked\"";
			}
			builder
			.append( "<label>" )
			.append( escDesc ).append( " (" ).append( escId ).append( ')' )
			.append( "<br/><input type=\"checkbox\" name=\"" )
			.append( escFullName )
			.append( "\" title=\"" )
			.append( escDesc )
			.append( checked );
			builder
			.append(
				item.isSetEnabled() ? 
					( item.isEnabled() ? "/>" : " disabled=\"disabled\"/>" ) : "/>"
			)
			.append( "</label>" );
			return builder.toString();
		}
	},
	
	RADIO( "radio" ) {

		@Override
		public String toXHTML( ServletContext application, String prefix, Item item ) {
			StringBuilder builder = new StringBuilder();
			String fullName;
			String escId;
			String escFullName;
			String escDesc;
			String checked;
			
			fullName = prefix + item.getId();
			escId = Routines.htmlspecialchars( item.getId() );
			escFullName = prefix + escId;
			escDesc = Routines.htmlspecialchars( item.getDesc() );
			
			if ( null == application.getAttribute( fullName ) ) {									// No way to check redio contract.
				checked = strBool( item.getDefault() ) ?  "\" checked=\"checked\"" : "\"";
			} else {
				checked = "\" checked=\"checked\"";
			}
			builder
			.append( "<label>" )
			.append( escDesc ).append( " (" ).append( escId ).append( ')' )
			.append( "<br/><input type=\"radio\" name=\"" )
			.append( escFullName )
			.append( "\" title=\"" )
			.append( escDesc )
			.append( checked );
			builder
			.append(
				item.isSetEnabled() ? 
					( item.isEnabled() ? "/>" : " disabled=\"disabled\"/>" ) : "/>"
			)
			.append( "</label>" );
			return builder.toString();
		}
	},
	
	TEXT( "text" ) {

		@Override
		public String toXHTML( ServletContext application, String prefix, Item item ) {
			StringBuilder builder = new StringBuilder();
			String fullName;
			String escId;
			String escFullName;
			String escDesc;
			String[] inputValues;
			String escInputValue;
			
			fullName = prefix + item.getId();
			escId = Routines.htmlspecialchars( item.getId() );
			escFullName = prefix + escId;
			escDesc = Routines.htmlspecialchars( item.getDesc() );
			inputValues = (String[]) application.getAttribute( fullName );
			
			if ( null == inputValues ) {
				escInputValue = Routines.htmlspecialchars( item.getDefault() );
			} else {
				escInputValue = Routines.htmlspecialchars( inputValues[ 0 ] );
			}
			builder
			.append( "<label>" )
			.append( escDesc ).append( " (" ).append( escId ).append( ')' )
			.append( "<br/><input type=\"text\" name=\"" )
			.append( escFullName )
			.append( "\" title=\"" )
			.append( escDesc );
			if ( null != escInputValue ) {
				builder
				.append( "\" value=\"" )
				.append( escInputValue );
			}
			builder
			.append(
				item.isSetEnabled() ? 
					( item.isEnabled() ? "\"/>" : " disabled=\"disabled\"/>" ) : "\"/>"
			)
			.append( "</label>" );
			return builder.toString();
		}
	},
	
	TEXTAREA( "textarea" ) {

		@Override
		public String toXHTML( ServletContext application, String prefix, Item item ) {
			StringBuilder builder = new StringBuilder();
			String fullName;
			String escId;
			String escFullName;
			String escDesc;
			String[] inputValues;
			String escInputValue;
			
			fullName = prefix + item.getId();
			escId = Routines.htmlspecialchars( item.getId() );
			escFullName = prefix + escId;
			escDesc = Routines.htmlspecialchars( item.getDesc() );			
			inputValues = (String[]) application.getAttribute( fullName );
			
			if ( null == inputValues ) {
				escInputValue = Routines.htmlspecialchars( item.getDefault() );
			} else {
				escInputValue = Routines.htmlspecialchars( inputValues[ 0 ] );
			}
			builder
			.append( "<label>" )
			.append( escDesc ).append( " (" ).append( escId ).append( ')' )
			.append( "<br/><textarea name=\"" )
			.append( escFullName )
			.append( "\" title=\"" )
			.append( escDesc )
			.append(
				item.isSetEnabled() ? 
					( item.isEnabled() ? "\"/>" : " disabled=\"disabled\"/>" ) : "\"/>"
			);
			if ( null != escInputValue ) {
				builder
				.append( escInputValue );
			}
			builder
			.append( "</textarea></label>" );
			return builder.toString();
		}
	};

	/** Reverse mapping of type names to transcoders. */
	private static final TreeMap< String, BasicTypes > reverseMap;
	
	static {
		reverseMap = new TreeMap< String, BasicTypes >();
		
		for ( BasicTypes basicType : BasicTypes.values() ) {
			reverseMap.put( basicType.toString(), basicType );
		}
	}
	
	/** Name of the type (used in XML). */
	private final String typeName;
	
	/**
	 * Enum element initializer, assigns type name.
	 * 
	 * @param typeName Name of the type (used in XML).
	 */
	private BasicTypes( String typeName ) {
		this.typeName = typeName;
	}
	
	@Override
	public String toString() {
		return typeName;
	}
	
	/**
	 * Transcoder getter.
	 * 
	 * @param string Name of the data type for which transcoder is requested.
	 * @return A suitable enum member or null if no transcoder is found.
	 */
	public static BasicTypes fromString( String string ) {
		return reverseMap.get( string );
	}
	
	/**
	 * String to boolean.
	 * 
	 * @param string String representation of a boolean.
	 * @return False in all cases but "true".
	 */
	private static boolean strBool( String string ) {												// null -> false
		return Boolean.valueOf( string );															// Something more clever...?
	}
}
