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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;

/**
 * Use this class to create new instances of class that implements PropertyTreeInterface. This is
 * the only way to create such object, since classes implementing PropertyTreeInterface are private.
 *
 * @author Branislav Repcek
 */
public class PropertyTreeFactory {

	/**
	 * Pattern which matches only valid typenames of objects.
	 */
	private static Pattern pathTypeOnly;
	
	/**
	 * Pattern which matches valid object paths.
	 */
	private static Pattern pathValid;
	
	/**
	 * Pattern which matches valid property path.
	 */
	private static Pattern propPathValid;

	/**
	 * Static initialisation, compile all regular expressions.
	 */
	static {
		pathTypeOnly = Pattern.compile("[\\p{Alpha}_]+");
		pathValid = Pattern.compile("([\\p{Alpha}_]+(\\(([0-9]+|\\?)\\))?\\.?)*[\\p{Alpha}_]+(\\(([0-9]+|\\?)\\))?");
		propPathValid = Pattern.compile("([\\p{Alpha}_]+(\\(([0-9]+|\\?)\\))?\\.?)*[\\p{Alpha}_]+");
	}
	
	/**
	 * Create new instance of class implementing PropertyTreeInterface.
	 * 
	 * @param name Name of the property tree to create.
	 * 
	 * @return Instance of class implementing PropertyTreeInterface. Created class will be root node. 
	 */
	public static PropertyTreeInterface create(String name) {
		
		return new PropertyTree(name);
	}
	
	/**
	 * Create new instance of class implementing PropertyTreeInterface.
	 * 
	 * @param name Name of the property tree to create.
	 * @param parent Reference to property tree node which will be set as parent of object created.
	 * 
	 * @return Instance of class implementing PropertyTreeInyterface.
	 */
	public static PropertyTreeInterface create(String name, PropertyTreeInterface parent) {
		
		return new PropertyTree(name, parent);
	}

	/**
	 * Create new instance of class implementing PropertyTreeInterface,
	 * 
	 * @param node XML file node which contains property tree data.
	 * 
	 * @return Instance of class implementing PropertyTreeInterface.
	 * 
	 * @throws InputParseException If an error occurred while parsing input.
	 */
	public static PropertyTreeInterface create(Node node) throws InputParseException {
		
		return new PropertyTree(node);
	}
	
	/**
	 * Test if path to the object is valid.
	 * 
	 * @param path Path to test.
	 * 
	 * @return <code>true</code> if path is valid, <code>false</code> otherwise.
	 */
	public static boolean isValidObjectPath(String path) {

		Matcher m = pathValid.matcher(path);
		
		return m.matches();
	}

	/**
	 * Test if property path is valid.
	 * 
	 * @param path Property path to test.
	 * 
	 * @return <code>true</code> if path is valid, <code>false</code> otherwise.
	 */
	public static boolean isValidPropertyPath(String path) {
		
		Matcher m = propPathValid.matcher(path);
		
		return m.matches();
	}

	/**
	 * Test if typename is valid.
	 * 
	 * @param tname Typename to test.
	 * 
	 * @return <code>true</code> if typename is valid, <code>false</code> otherwise.
	 */
	public static boolean isValidTypeName(String tname) {
		
		Matcher m = pathTypeOnly.matcher(tname);
		
		return m.matches();
	}
	
	/**
	 * Extract typename path of the property or object from the full path of property or object. 
	 * Typename path is path which consists only of typenames of objects on the path to the given
	 * object or property (that is, all wildcards and indices are removed). It is used when querying 
	 * descriptions of properties and objects. For example, input <i>village(3).indian(4).wife.name</i> 
	 * will produce <i>village.indian.wife.name</i>.
	 * 
	 * @param path Object or property path.
	 * @return Typename path to the object or property.
	 * 
	 * @throws InvalidArgumentException If given object or property path is not valid.
	 */
	public static String extractTypePathFromPath(String path) throws InvalidArgumentException {
		
		if (isValidPropertyPath(path) || isValidObjectPath(path)) {
			String result = "";
			int start = 0;
			
			while (start < path.length()) {
				int end = path.indexOf('.', start);
				
				if (end == -1) {
					end = path.length();
				}
				
				String objectName = path.substring(start, end);
				int par = objectName.indexOf('(');
				
				if (par == -1) {
					result = concatenatePaths(result, objectName);
				} else {
					result = concatenatePaths(result, objectName.substring(0, par));
				}
				
				start = end + 1;
			}
			
			return result;
		} else {
			throw new InvalidArgumentException("Specified property or object path is not valid: \"" 
					+ path + "\".");
		}
	}
	
	/**
	 * Concatenate two object or property paths. No error checking is done.
	 * 
	 * @param p1 First path.
	 * @param p2 Second path.
	 * 
	 * @return Concatenation of path specified.
	 */
	private static String concatenatePaths(String p1, String p2) {
		
		if (p1.length() == 0) {
			return p2;
		}
		
		if (p2.length() == 0) {
			return p1;
		}
		
		if (p1.endsWith(".") || p2.startsWith(".")) {
			return p1 + p2;
		} else {
			return p1 + "." + p2;
		}
	}
	
	/**
	 * Empty private constructor to prevent instantiation.
	 */
	private PropertyTreeFactory() {
	}
}
