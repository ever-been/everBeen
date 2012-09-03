/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
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

package cz.cuni.mff.been.hostmanager.database;

import cz.cuni.mff.been.common.value.ValueCommonInterface;
import cz.cuni.mff.been.hostmanager.HostManagerException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.ValueTypeIncorrectException;

/**
 * Read-only part of the PropertyTreeInterface.
 * 
 * @author Branislav Repcek
 */
public interface PropertyTreeReadInterface extends XMLSerializableInterface {

	/**
	 * Get list of names of properties of this object.
	 * 
	 * @return List of names of object's properties.
	 */
	String[] getPropertyNames();
	
	/**
	 * Get number of properties this object has.
	 * 
	 * @return Number of properties of this object.
	 */
	int getPropertyCount();

	/**
	 * Get list of names of all child nodes.
	 * 
	 * @return Array with names of child objects.
	 */
	String[] getObjectNames();

	/**
	 * Get value of property with given name.
	 * 
	 * @param propertyName Name of property.
	 * @return Value of requested property.
	 * 
	 * @throws ValueNotFoundException If property has not been found in this object.
	 * @throws InvalidArgumentException If name of property has invalid syntax.
	 */
	ValueCommonInterface getPropertyValue(String propertyName)
		throws ValueNotFoundException, InvalidArgumentException;

	/**
	 * Get value of property. This property can also be in child object which is specified by its path.
	 * 
	 * @param objectPath Path to object which contains property. If path is empty string property belongs
	 *        to this object.
	 * @param propertyName Name of property.
	 * @return Value of requested property.
	 * 
	 * @throws ValueNotFoundException If property or objects has not been found.
	 * @throws InvalidArgumentException If object path of property name has invalid syntax.
	 */
	ValueCommonInterface getPropertyValue(String objectPath, String propertyName)
		throws ValueNotFoundException, InvalidArgumentException;

	/**
	 * Test whether current object has given property.
	 * 
	 * @param propertyName Name of property to test.
	 * @return <code>true</code> if object contains property with given name, <code>false</code> otherwise.
	 * @throws InvalidArgumentException If property name has invalid syntax.
	 */
	boolean hasProperty(String propertyName) throws InvalidArgumentException;

	/**
	 * Get object with given name.
	 * 
	 * @param name Name of object to get.
	 * @return Requested object.
	 * 
	 * @throws ValueNotFoundException If object with given name does not exist.
	 * @throws InvalidArgumentException If object path has invalid syntax.
	 */
	PropertyTreeReadInterface getObject(String name) 
		throws ValueNotFoundException, InvalidArgumentException;

	/**
	 * Get object with specified type and index.
	 * 
	 * @param typeName Type name of object.
	 * @param index Index of object in list of objects of given type.
	 * @return Requested object or <code>null</code> if index is bigger than number of objects of 
	 *         given type or if index is less than zero.
	 * 
	 * @throws ValueNotFoundException If object with specified type or index does not exist.
	 * @throws InvalidArgumentException If type name has invalid syntax.
	 */
	PropertyTreeReadInterface getObject(String typeName, int index)
		throws ValueNotFoundException, InvalidArgumentException;

	/**
	 * Get name of object.
	 * 
	 * @param absolute If <code>true</code> absolute path (path from root node) to current object 
	 *        is returned. If <code>false</code> only name of current object is returned.
	 * @return String with name relative or absolute path to object.
	 */
	String getName(boolean absolute);

	/**
	 * Get type name of object.
	 * 
	 * @return String with type name.
	 */
	String getTypeName();

	/**
	 * Get index of object. This method should be used only for counted objects.
	 * 
	 * @return Index of object in array of objects of the same type.
	 */
	int getIndex();

	/**
	 * Get number of child objects.
	 * 
	 * @return Number of child objects.
	 */
	int getObjectCount();

	/**
	 * Get number of child objects of given type.
	 * 
	 * @param name Type name.
	 * @return Number of child objects of given type.
	 * 
	 * @throws InvalidArgumentException If type name has invalid syntax.
	 */
	int getObjectCount(String name) throws InvalidArgumentException;

	/**
	 * Test current object against given criteria. Object satisfies given set of criteria if it 
	 * satisfies each criterion in list (that is logical and is used as operator).
	 * 
	 * @param restrictions List of criteria.
	 * @param ignoreMissing If <code>true</code> missing properties or objects in the list will 
	 *        not cause <code>ValueNotFoundException</code> to be thrown but the host will not be 
	 *        accepted. If <code>false</code> missing object or property will throw.
	 *        
	 * @return <code>true</code> if object satisfies given criteria, <code>false</code> otherwise.
	 * 
	 * @throws ValueNotFoundException If object or property specified in some criterion has not been found.
	 * @throws ValueTypeIncorrectException If condition for given property has type incompatible with
	 *         value type of property.
	 * @throws HostManagerException Other error occurred.
	 */
	boolean test(RestrictionInterface[] restrictions, boolean ignoreMissing)
			throws ValueNotFoundException, ValueTypeIncorrectException, HostManagerException;

	/**
	 * Test current object against given criteria. Object satisfies given set of criteria if it 
	 * satisfies each criterion in list (that is logical and is used as operator).
	 * 
	 * @param restriction Criteria object has to match.
	 * @param ignoreMissing If <code>true</code> missing properties or objects in the list will 
	 *        not cause <code>ValueNotFoundException</code> to be thrown but the host will not be
	 *        accepted. If <code>false</code> missing object or property will throw.
	 *        
	 * @return <code>true</code> if object satisfies given criteria, <code>false</code> otherwise.
	 * 
	 * @throws ValueNotFoundException If object or property specified in some criterion has not been found.
	 * @throws ValueTypeIncorrectException If condition for given property has type incompatible with
	 *         value type of property.
	 * @throws HostManagerException Other error occurred.
	 */
	boolean test(RestrictionInterface restriction, boolean ignoreMissing)
		throws ValueNotFoundException, ValueTypeIncorrectException, HostManagerException;
	
	/**
	 * Test whether given path is valid.
	 * 
	 * @param path Path to test.
	 * 
	 * @return <code>true</code> if path is valid object path, <code>false</code> otherwise. 
	 *         This will check only whether path is syntactically correct (method does not check if
	 *         such object actually exists).
	 */
	boolean isValidObjectPath(String path);
	
	/**
	 * Test whether given path to property has correct syntax.
	 * 
	 * @param path Path to test.
	 * @return <code>true</code> if syntax of path is valid, <code>false</code> otherwise.
	 */
	boolean isValidPropertyPath(String path);

	/**
	 * Test whether given typename string is valid one.
	 * 
	 * @param tname Typename to test.
	 * @return <code>true</code> if typename has correct form, <code>false</code> otherwise. 
	 *         This will only check syntax of the string, method does not test whether objects of
	 *         such type actually exist somewhere in hierarchy.
	 */
	boolean isValidTypeName(String tname);

	/**
	 * Get iterator over the set of child objects.
	 * 
	 * @return Iterator over the set of child objects.
	 */
	Iterable< PropertyTreeReadInterface > getObjects();
	
	/**
	 * Get iterator over the set of properties of current object.
	 * 
	 * @return Iterator over the set of properties.
	 */
	Iterable< NameValuePair > getProperties();
}
