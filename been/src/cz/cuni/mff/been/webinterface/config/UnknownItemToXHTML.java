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

import javax.servlet.ServletContext;

import cz.cuni.mff.been.jaxb.config.Item;
import cz.cuni.mff.been.webinterface.Routines;

/**
 * When an unknown data type is encountered, it will be rendered as a standard text input
 * with warning notes all around. This class takes care of that.
 * The JAXB parser should be improved and extended with ID namespaces so that configuring
 * illegal types (in XML and (hopefully) also on the Java side) becomes impossible.
 * 
 * @author Andrej Podzimek
 */
public final class UnknownItemToXHTML implements ItemToXHTML {
	
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
		.append( " (UNKNOWN TYPE!!!)" )
		.append( "<br/><input type=\"text\" name=\"" )
		.append( escFullName )
		.append( "\" title=\"(UNKNOWN TYPE!!!) " )
		.append( escDesc );
		if ( null != escInputValue ) { 
			builder
			.append( "\" value=\"" )
			.append( escInputValue );
		}
		builder
		.append(
			item.isSetEnabled() ? 
				( item.isEnabled() ? "\"/>" : "\" disabled=\"disabled\"/>" ) : "\"/>"
		)
		.append( "</label>" );
		return builder.toString();
	}
}
