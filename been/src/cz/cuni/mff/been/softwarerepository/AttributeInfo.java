/*
 *  BEEN: Benchmarking Environment
 *  ==============================
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

import java.lang.reflect.Method;
import java.util.EnumSet;

/**
 * Describes an attribute in the package metadata.
 * 
 * @author David Majda
 */
public class AttributeInfo {
	/** Attribute name. */
	private String name;
	/** Human-readable attribute name (for use in UI). */
	private String humanName;
	/** Attribute class. */
	private Class< ? > klass;
	/** Attribute helper. */
	private AttributeHelper< ? > helper;
	/** Where is this attribute allowed. */
	private EnumSet<PackageType> allowed;
	/** Where is this attribute required. */
	private EnumSet<PackageType> required;
	/** Attribute getter. */
	private Method getter;
	
	/** @return the name */
	public String getName() {
		return name;
	}
	
	/** @return the humanName */
	public String getHumanName() {
		return humanName;
	}
	
	/** @return the class */
	public Class< ? > getKlass() {
		return klass;
	}
	
	/** @return the helper */
	public AttributeHelper< ? > getHelper() {
		return helper;
	}
	
	/** @return the getter */
	public Method getGetter() {
		return getter;
	}
	
	/** @return the allowed */
	public EnumSet<PackageType> getAllowed() {
		return allowed;
	}

	/** @return the required */
	public EnumSet<PackageType> getRequired() {
		return required;
	}
	
	/**
	 * Allocates a new <code>AttributeInfo</code> object.
	 * 
	 * @param name attrbiute name
	 * @param humanName human-readable attribute name
	 * @param klass attribute class
	 * @param helper attribute helper
	 * @param allowed where is this attribute allowed
	 * @param required where is this attribute required 
	 * @param getter attribute getter
	 */
	public AttributeInfo(String name, String humanName, Class< ? > klass,
			AttributeHelper< ? > helper, EnumSet<PackageType> allowed,
			EnumSet<PackageType> required, Method getter) {
		assert allowed.containsAll(required): "Required implies allowed";
		
		this.name = name;
		this.humanName = humanName;
		this.klass = klass;
		this.helper = helper;
		this.allowed = allowed;
		this.required = required;
		this.getter = getter;
	}
}
