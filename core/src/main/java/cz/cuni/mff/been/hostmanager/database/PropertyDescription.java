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

import org.w3c.dom.Node;

import cz.cuni.mff.been.common.util.XMLHelper;
import cz.cuni.mff.been.hostmanager.InputParseException;


import java.io.Serializable;

/**
 * This class represents one property in the host database, its description and type.
 *
 * @author Branislav Repcek
 */
public class PropertyDescription  implements Serializable {
	
	private static final long	serialVersionUID	= -5941711519349268474L;

	/**
	 * Path to the property in database. This path should not contain any indexing.
	 */
	private String propertyPath;
	
	/**
	 * Description of the property.
	 */
	private String description;
	
	/**
	 * Type of value stored in property.
	 */
	private String type;
	
	/**
	 * Unit.
	 */
	private String unit;
	
	/**
	 * Create property from XML Node.
	 * 
	 * @param node Node from XML file.
	 *  
	 * @throws InputParseException If node does not contain valid data.
	 */
	public PropertyDescription(Node node) throws InputParseException {
		
		propertyPath = XMLHelper.getAttributeValueByName("path", node);
		type = XMLHelper.getAttributeValueByName("type", node);
		description = XMLHelper.getNodeValue(node);
		
		if (XMLHelper.hasAttribute("unit", node)) {
			unit = XMLHelper.getAttributeValueByName("unit", node);
		} else {
			unit = null;
		}
	}
	
	/**
	 * Get path to the property.
	 * 
	 * @return String with property path.
	 */
	public String getPropertyPath() {
		
		return propertyPath;
	}
	
	/**
	 * Get description of property.
	 * 
	 * @return String with description of the property.
	 */
	public String getDescription() {
		
		return description;
	}
	
	/**
	 * Get name of the type of the value stored in property.
	 * 
	 * @return Type name String.
	 */
	public String getType() {
		
		return type;
	}
	
	/**
	 * Get unit of the value.
	 * 
	 * @return Symbol for the unit of the value (e.g. B for bytes). This may be <tt>null</tt> if
	 *         value does not use any unit (e.g. host name does not have unit). Note that unit may 
	 *         be multiple of basic unit (e.g. KB or MHz).
	 */
	public String getUnit() {
		
		return unit;
	}
}
