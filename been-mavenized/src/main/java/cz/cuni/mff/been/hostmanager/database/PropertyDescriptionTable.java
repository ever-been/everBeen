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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.util.XMLHelper;

/**
 * This class manages all property descriptions stored in description file.
 * 
 * @author Branislav Repcek
 */
public class PropertyDescriptionTable implements Serializable {

	private static final long	serialVersionUID	= 4843662650653067960L;

	/**
	 * Descriptions. 
	 */
	private HashMap< String, PropertyDescription > descriptions;
	
	/**
	 * Load property descriptions from XML file.
	 * 
	 * @param fileName Name of the input file.
	 * @throws InputParseException If an error occurred while parsing file data.
	 * @throws FileNotFoundException If specified file does not exist.
	 */
	public PropertyDescriptionTable(String fileName) 
		throws InputParseException, FileNotFoundException {
		
		descriptions = new HashMap< String, PropertyDescription >();

		DocumentBuilder builder = null;

		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		
		File input = new File(fileName);
		
		if (!input.exists()) {
			throw new FileNotFoundException("File + \"" + fileName + "\" not found.");
		}
		
		Document document = null;
		
		try {
			document = builder.parse(input);
		} catch (Exception e) {
			throw new InputParseException("Unable to parse description file.", e);
		}
		
		Node descNode = XMLHelper.getSubNodeByName("descriptions", document);
		
		ArrayList< Node > childNodes = XMLHelper.getChildNodesByName("property", descNode);
		
		PropertyDescription	pd;
		for ( Node node : childNodes ) {
			pd = new PropertyDescription( node );
			descriptions.put(pd.getPropertyPath(), pd);
		}
	}
	
	/**
	 * Get description of the property or object based on its path.
	 * 
	 * @param path Full path to the property or object.
	 * 
	 * @return Instance of <code>PropertyDescription</code> class that contains details about requested
	 *         object or property.
	 * 
	 * @throws InvalidArgumentException
	 * @throws ValueNotFoundException
	 */
	public PropertyDescription getDescription(String path) 
		throws InvalidArgumentException, ValueNotFoundException {
		
		String typeNamePath = PropertyTreeFactory.extractTypePathFromPath(path);
		
		if (descriptions.containsKey(typeNamePath)) {
			return descriptions.get(typeNamePath);
		} else {
			throw new ValueNotFoundException("No description found for \"" + path + "\".");
		}
	}
	
	/**
	 * Get all descriptions.
	 * 
	 * @return List containing all descriptions. List is sorted by property path.
	 */
	public List< PropertyDescription > getAllDescriptions() {
		
		ArrayList< PropertyDescription > result = new ArrayList< PropertyDescription >();
		
		result.addAll(descriptions.values());
		
		Collections.sort(result, new PropertyDescriptionComparator());
		
		return result;
	}

	/**
	 * Comparator which is used when sorting PropertyDescription objects. Descriptions are compared
	 * only in property paths.
	 *
	 * @author Branislav Repcek
	 */
	private class PropertyDescriptionComparator implements Comparator< PropertyDescription > {

		/*
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(PropertyDescription o1, PropertyDescription o2) {
			
			return o1.getPropertyPath().compareTo(o2.getPropertyPath());
		}
	}
}
