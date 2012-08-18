/*
 *  BEEN: Benchmarking Environment
 *
 *  File author: David Majda
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
package cz.cuni.mff.been.softwarerepository;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Enum representing the package type (source, binary, task or data).
 * 
 * @author David Majda
 */
public enum PackageType implements Serializable {

	/** Source package type. */
	SOURCE( "source", "src" ),

	/** Binary package type. */
	BINARY( "binary", "bin" ),

	/** Task package type. */
	TASK( "task", "task" ),

	/** Data package type. */
	DATA ( "data", "data" ),

    /** Pluggable module data type. */
    MODULE( "module", "module" );

	/**The lower case string representation of the member.*/
	private String	lcString;

	/**String suffix of the lowercase representation...*/
	private String	suffix;

	/**Initializer that stores the data variablse.
	 * 
	 * @param lcString Lower case string representation of the member.
	 * @param suffix Lower case string suffix of the member.
	 */
	private PackageType( String lcString, String suffix ) {
		this.lcString = lcString;
		this.suffix = suffix;
	}

	private static final TreeMap< String, PackageType >	typeMap;

	/* Please note that children (enum members) are initialized BEFORE the ancestor
	 * (enum base class). If you take the other way round and try to add map items
	 * from the initializer above, you'll get a NullPointerException.
	 */
	static {
		typeMap = new TreeMap< String, PackageType >();
		for ( PackageType type : PackageType.values() ) {
			typeMap.put( type.lcString, type );
		}
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return lcString;
	}

	/**
	 * Return a <code>PackageType</code> object corresponding to given string. The
	 * passed string is not the name of the enum constant, but rather its
	 * <code>toString</code> value.
	 * 
	 * This method should be really named <code>valueOf</code>, but Java is not
	 * flexible enough to not allow it, as it generates implicit
	 * <code>valueOf</code> method for each enum class. 
	 * 
	 * @param name string representing a <code>PackageType</code> enum constant
	 * @return <code>PackageType</code> object corresponding to given string
	 * @throws NullPointerException if <code>name</code> is <code>null</code>
	 * @throws IllegalArgumentException if no enum constant corresponding to
	 *          <code>name</code> exists
	 */
	public static PackageType realValueOf( String name ) {
		if ( name == null ) {
			throw new NullPointerException( "Name is null" );
		}
		
		PackageType	result = typeMap.get( name );		
		if ( result == null ) {
			throw new IllegalArgumentException("No enum value named \"" + name + "\" exists.");
		}		
		return result;
	}

	/**
	 * Returns a package suffix for this package type.
	 * 
	 * @return packge suffix
	 */
	public String getSuffix() {
		return suffix;
	}
}
