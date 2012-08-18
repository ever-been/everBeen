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
package cz.cuni.mff.been.common.value;

import java.util.TreeMap;

/**
 * A simple list of possible value types for easy switching between them.
 * 
 * @author Andrej Podzimek
 */
public enum ValueType {
	
	BOOLEAN( ValueBoolean.class.getCanonicalName() ),
	
	DOUBLE( ValueDouble.class.getCanonicalName() ),
	
	INTEGER( ValueInteger.class.getCanonicalName() ),
	
	LIST( ValueList.class.getCanonicalName() ),
	
	RANGE( ValueRange.class.getCanonicalName() ),
	
	REGEXP( ValueRegexp.class.getCanonicalName() ),
	
	STRING( ValueString.class.getCanonicalName() ),
	
	VERSION( ValueVersion.class.getCanonicalName() );
	
	private static final TreeMap< String, ValueType > typeMap;
	
	static {
		typeMap = new TreeMap< String, ValueType >();
		for ( ValueType valueType : ValueType.values() ) {
			typeMap.put( valueType.NAME, valueType );
		}
	}
	
	public final String NAME;
	
	private ValueType( String name ) {
		this.NAME = name;
	}
	
	public static ValueType forName( String name ) {
		return typeMap.get( name );
	}
}
