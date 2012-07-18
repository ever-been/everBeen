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

import cz.cuni.mff.been.hostmanager.HostManagerException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.ValueTypeIncorrectException;
import cz.cuni.mff.been.hostmanager.value.ValueCommonInterface;

/**
 * This interface provides means to manipulate collections of named objects and properties organised 
 * in tree-like structure.
 * <br>
 * Instances of the classes which implement this interface represent one node in the tree-like 
 * hierarchy of <i>objects</i> and <i>properties</i>. Structure of the tree should be based on the 
 * way how various objects and properties relate to each other. Objects are inner nodes of the tree 
 * and properties are always leaves.
 * <br>
 * <br>
 * <i>Object</i> is named container which is then more precisely described by its child objects or
 * properties. Each object can contain unlimited number of child objects or properties. Objects cannot
 * have any value attached to them.<br>
 * <i>Name</i> of each object has two parts. First part is <i>object's type name</i>, second part is
 * <i>object's index</i>. Object's type name is string of any length which can contain only alphabetic
 * characters. Object names are case-sensitive. Object index is integer greater than or equal to zero.
 * Index uniquely identifies given object in the list of all objects with the same type name and same
 * parent. First object has index zero.<br>
 * Full name of the object is then concatenation of the type name and index. Index is written between
 * parentheses ("(" and ")"). If there's only one instance of the object within given parent, 
 * object name does not need to contain index.<br>
 * For example <code>processor(2)</code> is the third object of the type <code>processor</code>.<br>
 * Each object in the tree is uniquely identified by its path from the root node. <i>Object path</i>
 * is then concatenation of names of all objects on the path from the root node to the given object.
 * Names of objects on the path are separated by the dot (".") character.
 * <br>
 * <br>
 * <i>Properties</i>, unlike the objects, always have value associated with them. Properties are always
 * leaf nodes and therefore cannot have any child nodes attached to them. Each property is 
 * identified by its name which has to be unique within its parent object.
 * <i>Property name</i> can be any string containing only alphabetic characters. Each property is 
 * uniquely identified in the hierarchy by the path from root node. This path consists of the path to
 * the parent object of the property and property name. Name of the property is separated by the dot
 * (".") character from the rest of the path.<br>
 * Values attached to properties can be only of certain types. Supported types are 
 * <code>ValueBoolean</code>, <code>ValueInteger</code>, <code>ValueDouble</code>, 
 * <code>ValueString</code>, <code>ValueRegexp</code>, <code>ValueVersion</code>, <code>ValueList</code>
 * and <code>ValueRange</code>.
 * 
 * @see cz.cuni.mff.been.hostmanager.database.PropertyTree
 *
 * @author Branislav Repcek
 */
public interface PropertyTreeInterface extends PropertyTreeReadInterface, XMLSerializableInterface {

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
	 * Set value of given property. Property must exist prior calling this method.
	 * 
	 * @param propertyName Name of property.
	 * @param newValue New value of property.
	 * 
	 * @return Old value of property.
	 * 
	 * @throws ValueNotFoundException If property has not been found.
	 * @throws InvalidArgumentException If property name has invalid syntax.
	 */
	ValueCommonInterface setPropertyValue(String propertyName, ValueCommonInterface newValue) 
		throws ValueNotFoundException, InvalidArgumentException;

	/**
	 * Set value of given property. Property must exist prior calling this method.
	 * 
	 * @param property <code>NameValuePair</code> containing name of property to set and new value.
	 * 
	 * @return Old value of property.
	 * 
	 * @throws ValueNotFoundException If property has not been found.
	 * @throws InvalidArgumentException If property name has invalid syntax. 
	 */
	ValueCommonInterface setPropertyValue(NameValuePair property)
		throws ValueNotFoundException, InvalidArgumentException;
	
	/**
	 * Add new property to the object.
	 * 
	 * @param propertyName Name of property to add.
	 * @param propValue Value of property.
	 * 
	 * @throws InvalidArgumentException If property name has invalid syntax or if given property 
	 *         already exists.
	 */
	void addProperty(String propertyName, ValueCommonInterface propValue)
		throws InvalidArgumentException;

	/**
	 * Add new property to the current object.
	 * 
	 * @param property <code>NameValuePair</code> which contains name and value of the new property.
	 * 
	 * @throws InvalidArgumentException If property name is invalid of if property with given name
	 *         already exists.
	 */
	void addProperty(NameValuePair property) throws InvalidArgumentException;
	
	/**
	 * Put property to the object. That is, if property already exists, change its value. If property
	 * does not exist, it will be created.
	 * 
	 * @param propertyName Name of property.
	 * @param value New value.
	 * 
	 * @return Old value of property or <code>null</code> if property has been just created.
	 * 
	 * @throws InvalidArgumentException If property name is invalid.
	 */
	ValueCommonInterface putProperty(String propertyName, ValueCommonInterface value) 
		throws InvalidArgumentException;
	
	/**
	 * Put property to the current object. If property already exists its value is overwritten.
	 * 
	 * @param property <code>NameValuePair</code> which contains name and value of the property.
	 * 
	 * @return Old value of the property or <code>null</code> if new property has been created.
	 * 
	 * @throws InvalidArgumentException If property name is invalid.
	 */
	ValueCommonInterface putProperty(NameValuePair property) throws InvalidArgumentException;
	
	/**
	 * Remove property from current object.
	 * 
	 * @param propertyName Name of property to remove.
	 * 
	 * @throws ValueNotFoundException If requested property has not been found. 
	 * @throws InvalidArgumentException If property name is invalid.
	 */
	void removeProperty(String propertyName) 
		throws ValueNotFoundException, InvalidArgumentException;

	/**
	 * Test whether current object has given property.
	 * 
	 * @param propertyName Name of property to test.
	 * 
	 * @return <code>true</code> if object contains property with given name, <code>false</code> otherwise.
	 * 
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
	PropertyTreeInterface getObject(String name) 
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
	PropertyTreeInterface getObject(String typeName, int index)
		throws ValueNotFoundException, InvalidArgumentException;

	/**
	 * Add new object to the list of child objects of current object. Object will be added to the end
	 * of the list of objects with the same type (if such objects already are children of current object).
	 * 
	 * @param object Object to add to the list.
	 */
	void addObject(PropertyTreeInterface object);

	/**
	 * Remove object with given name. All objects of the same type which are after this object in 
	 * the list of child objects will have their indices decreased by one.
	 * 
	 * @param name of object to remove. Note that you can only remove local objects (that is, you 
	 *        cannot remove object deeper in hierarchy).
	 *        
	 * @throws ValueNotFoundException If object with given name has not been found.
	 * @throws InvalidArgumentException If name of object is incorrect (contains wildcard, has invalid
	 *         format or is not local).
	 */
	void removeObject(String name) throws ValueNotFoundException, InvalidArgumentException;

	/**
	 * Remove object.
	 * 
	 * @param typeName Type name of the object to remove.
	 * @param index index of object in the list of objects with the same type name.
	 * 
	 * @throws ValueNotFoundException If no such object has been found.
	 * @throws InvalidArgumentException If type name is invalid.
	 */
	void removeObject(String typeName, int index) 
		throws ValueNotFoundException, InvalidArgumentException;
	
	/**
	 * Remove all objects of specified type.
	 * 
	 * @param type Typename of the objects to remove.
	 * 
	 * @throws ValueNotFoundException If no object of given has been found.
	 * @throws InvalidArgumentException If type has invalid syntax.
	 */
	void removeAllOfType(String type) throws ValueNotFoundException, InvalidArgumentException;
	
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
}
