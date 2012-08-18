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

import java.io.Serializable;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.common.util.XMLHelper;
import cz.cuni.mff.been.hostmanager.InputParseException;

/**
 * This class provides means to create conditions from several <code>ObjectRestriction</code>s 
 * connected with logical or connective. Object satisfies <code>AlternativeRestriction</code> if
 * it satisfies at least one of the restrictions in int.
 *
 * @author Branislav Repcek
 */
public class AlternativeRestriction 
	implements RestrictionInterface, Serializable, XMLSerializableInterface {
	
	private static final long	serialVersionUID	= -6339195120285001802L;
	
	/**
	 * List of restrictions.
	 */
	private ObjectRestriction []restrictions;

	/**
	 * Create alternative with only two restrictions.
	 * 
	 * @param restr1 First restriction.
	 * @param restr2 Second restriction.
	 * 
	 * @throws IllegalArgumentException If one of restrictions is null.
	 */
	public AlternativeRestriction(ObjectRestriction restr1, ObjectRestriction restr2) 
		throws IllegalArgumentException {
		
		if ((restr1 == null) || (restr2 == null)) {
			throw new IllegalArgumentException("null parameters are not allowed for AlternativeRestriction.");
		}
		
		restrictions = new ObjectRestriction[2];
		
		restrictions[0] = restr1;
		restrictions[1] = restr2;
	}

	/**
	 * Create alternative consisting of three restrictions.
	 * 
	 * @param restr1 First restriction.
	 * @param restr2 Second restriction.
	 * @param restr3 Third restriction.
	 * 
	 * @throws IllegalArgumentException If one of restrictions is <code>null</code>.
	 */
	public AlternativeRestriction(ObjectRestriction restr1, ObjectRestriction restr2, ObjectRestriction restr3) 
		throws IllegalArgumentException {
		
		if ((restr1 == null) || (restr2 == null) || (restr3 == null)) {
			throw new IllegalArgumentException("null parameters are not allowed for "
					+ "AlternativeRestriction.");
		}
		
		restrictions = new ObjectRestriction[3];
		
		restrictions[0] = restr1;
		restrictions[1] = restr2;
		restrictions[2] = restr3;
	}
	
	/**
	 * Create alternative from list of restrictions.
	 * 
	 * @param restr Array containing restrictions. This array must contain at least one element.
	 *
	 * @throws IllegalArgumentException If parameter is <code>null</code> or empty array.
	 */
	public AlternativeRestriction(ObjectRestriction []restr) 
		throws IllegalArgumentException {
		
		if (restr == null) {
			throw new IllegalArgumentException("null parameters are not allowed for AlternativeRestriction.");
		}
		
		if (restr.length == 0) {
			throw new IllegalArgumentException("Empty restriction array.");
		}
		
		restrictions = restr;
	}
	
	/**
	 * Create new instance from data in XML node.
	 * 
	 * @param node Node with data.
	 * 
	 * @throws InputParseException If there was an error while parsing node.
	 */
	public AlternativeRestriction(Node node) throws InputParseException {
		
		parseXMLNode(node);
	}
	
	/**
	 * Get list of restrictions.
	 * 
	 * @return Array with list of restrictions.
	 */
	public ObjectRestriction []getRestrictions() {
		
		return restrictions;
	}
	
	/**
	 * Create string which represents this class. Resulting string will contain list of all 
	 * restrictions and their properties. Result will span multiple lines.
	 * 
	 * @return String representing class.
	 */
	@Override
	public String toString() {
		
		String result = "Alternative { \n";
		
		for (int i = 0; i < restrictions.length; ++i) {
			
			result += "\t" + restrictions[i].toString().replaceAll("\t", "\t\t").replaceAll("}", "\t}") + "\n";
		}

		result += "}";
		
		return result;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode
	 */
	public void parseXMLNode(Node node) throws InputParseException {

		if (!node.getNodeName().equals(getXMLNodeName())) {
			throw new InputParseException("Node does not contain alternative restriction data."
					+ " Node name is \"" +  node.getNodeName() + "\".");
		}
		
		ArrayList< Node > restr = XMLHelper.getChildNodesByName("restriction", node);
		restrictions = new ObjectRestriction[restr.size()];
		
		int i = 0;
		for (Node current: restr) {
			
			restrictions[i] = new ObjectRestriction(current);
			i += 1;
		}
		
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement
	 */
	public Element exportAsElement(Document doc) {
		
		/* Node:
		 * 
		 * <alternative>
		 *    <object-restr #1/>
		 *    <object-restr #2/>
		 *           .
		 *           .
		 *           .
		 *    <object-restr #n/>
		 * </alternative>
		 * 
		 * where <object-rest #i/> are serialised ObejctRestrictions.
		 */
		
		Element element = doc.createElement(getXMLNodeName());
		
		for (ObjectRestriction current: restrictions) {
			
			element.appendChild(current.exportAsElement(doc));
		}
		
		return element;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName
	 */
	public String getXMLNodeName() {
		
		return "alternative";
	}
	
	/*
	 * @see java.lang.Object#equals
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o instanceof AlternativeRestriction) {
			return equals((AlternativeRestriction) o);
		} else {
			return false;
		}
	}
	
	/**
	 * Compare two AlternativeRestrictions. AlternativeRestrictions are equal if they produce same
	 * results when matched against hosts. This is true if both objects contain same ObjectRestrictions
	 * but it does not depend on the order in which restrictions appear.
	 * 
	 * @param o AlternativeRestriction to compare to this.
	 * 
	 * @return <code>true</code> if both objects are equal, <code>false</code> otherwise.
	 */
	public boolean equals(AlternativeRestriction o) {
		
		if (restrictions.length != o.restrictions.length) {
			return false;
		}

		/* This can be quite slow (O(n^2)), but that should not matter since this should not be
		 * used too often (it's not that important to compare various restrictions).
		 */
		for (ObjectRestriction current: restrictions) {
			if (!isInArray(current, o.restrictions)) {
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * @see java.lang.Object#hashCode
	 */
	@Override
	public int hashCode() {
		
		int result = 0;
		
		for (ObjectRestriction o: restrictions) {
			result += o.hashCode();
		}
		
		return result;
	}

	/**
	 * Test if given element is in array. It uses simple linear search and therefore array does not
	 * need to be sorted (this also makes this method not so suitable for sorted arrays).
	 * 
	 * @param <T> Type of the element to look for.
	 * 
	 * @param what Element which we are searching for.
	 * @param where Array in which we search.
	 * 
	 * @return <code>true</code> if given element is in array, <code>false</code> otherwise.
	 */
	private static < T > boolean isInArray(T what, T []where) {
		
		for (T current: where) {
			if (current.equals(what)) {
				return true;
			}
		}
		
		return false;
	}
}
