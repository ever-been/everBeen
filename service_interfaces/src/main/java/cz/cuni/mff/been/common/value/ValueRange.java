/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Branislav Repcek
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package cz.cuni.mff.been.common.value;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.common.util.XMLHelper;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.ValueTypeIncorrectException;
import cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface;

/**
 * Class which represents range of values (closed interval). Endpoints can be
 * only of basic types (derived from <code>ValueBasicInterface</code>
 * interface).
 * 
 * @param <T>
 *            Type of endpoints of the interval. This type has to extend
 *            ValueBasicInterface< T >
 * 
 * @author Branislav Repcek
 */
public class ValueRange<T extends ValueBasicInterface<T>> implements
		ValueCompoundInterface<T>, Serializable, XMLSerializableInterface {

	private static final long serialVersionUID = -8807172343629295221L;

	/** Type of values this list will store. */
	private ValueType elementType;

	/**
	 * Lower bound of interval.
	 */
	private T valueMin;

	/**
	 * Upper bound of interval.
	 */
	private T valueMax;

	/**
	 * Is left endpoint of the interval open or closed?
	 */
	private boolean leftOpen;

	/**
	 * Is right endpoint of the interval open or closed?
	 */
	private boolean rightOpen;

	/**
	 * Allocate {@code ValueRange} which represents the whole interval. This
	 * constructor is dangerous and deprecated, since it doesn't set the element
	 * value type properly. This constructor must be immediately followed by a
	 * call to {@code parseXmlNode()} that will set the instance into a valid
	 * state. This constructor exists only to make reflection tricks work. It is
	 * not meant to be invoked directly!
	 */
	@Deprecated
	public ValueRange() {

		this((ValueType) null);
	}

	/**
	 * Allocate <code>ValueRange</code> value which represents whole interval.
	 */
	public ValueRange(ValueType elementType) {

		this.elementType = elementType;
		this.valueMin = this.valueMax = null;
		this.leftOpen = this.rightOpen = true;
	}

	/**
	 * Create new <code>ValueRange</code> with given endpoints. Both endpoints
	 * will be closed for finite values. For infinite value, endpoint will be
	 * open.
	 * 
	 * @param min
	 *            Left endpoint of interval. Set to <code>null</code> for
	 *            -&infin;.
	 * @param max
	 *            Right endpoint of interval. Set to <code>null</code> for
	 *            +&infin;.
	 */
	public ValueRange(T min, T max, ValueType elementType) {

		this.elementType = elementType;
		this.valueMin = min;
		this.valueMax = max;

		this.leftOpen = this.valueMin == null;
		this.rightOpen = this.valueMax == null;
	}

	/**
	 * Create new <code>ValueRange</code> with given endpoints.
	 * 
	 * @param min
	 *            Left endpoint. Set to <code>null</code> for -&infin;.
	 * @param max
	 *            Right endpoint. Set to <code>null</code> for +&infin;.
	 * @param leftOpen
	 *            If <code>true</code>, left endpoint will be open. Endpoint
	 *            will be closed if <code>false</code>. Left endpoint will be
	 *            always created as open if its value is set to negative
	 *            infinity.
	 * @param rightOpen
	 *            If <code>true</code>, right endpoint will be open. Endpoint
	 *            will be closed if <code>false</code>. Right endpoint will be
	 *            always created as open if its value is set to positive
	 *            infinity.
	 */
	public ValueRange(T min, T max, boolean leftOpen, boolean rightOpen,
			ValueType elementType) {

		this.elementType = elementType;
		this.valueMin = min;
		this.valueMax = max;

		this.leftOpen = null == this.valueMin ? true : leftOpen;
		this.rightOpen = null == this.valueMax ? true : rightOpen;
	}

	/**
	 * Create new range from XML node.
	 * 
	 * @param node
	 *            Node containing range data.
	 * 
	 * @throws InputParseException
	 *             If there was an error parsing node data.
	 */
	public ValueRange(Node node) throws InputParseException {

		parseXMLNode(node);
	}

	/**
	 * Convert value to string.
	 * 
	 * @return String object representing current value. Open side of interval
	 *         is indicated by round brace, closed endpoint is indicated by
	 *         square bracket
	 */
	@Override
	public String toString() {

		String left = valueMin == null ? "-infinity" : valueMin.toString();
		String right = valueMax == null ? "+infinity" : valueMax.toString();

		return (leftOpen ? "(" : "[") + left + "," + right
				+ (rightOpen ? ")" : "]");
	}

	/**
	 * Test whether given value is contained within interval.
	 * 
	 * @param elem
	 *            Value to test.
	 * @return <code>true</code> if <code>elem</code> is inside current
	 *         interval, <code>false</code> otherwise.
	 */
	@Override
	public boolean contains(T elem) {

		if ((valueMin == null) && (valueMax == null)) {
			// whole space, both boundaries are null
			return true;
		}

		boolean leftCond = false;
		boolean rightCond = false;

		if (valueMin == null) {
			leftCond = true;
		} else {
			if (leftOpen) {
				leftCond = elem.greaterThan(valueMin);
			} else {
				leftCond = elem.greaterThan(valueMin) || elem.equals(valueMin);
			}
		}

		if (valueMax == null) {
			rightCond = true;
		} else {
			if (rightOpen) {
				rightCond = elem.lessThan(valueMax);
			} else {
				rightCond = elem.lessThan(valueMax) || elem.equals(valueMax);
			}
		}

		return leftCond && rightCond;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {

		if (o instanceof ValueRange<?>) {
			try {
				return equals((ValueRange<?>) o);
			} catch (ValueTypeIncorrectException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Compare specified <code>ValueRange</code> with this value for equality.
	 * Values are equal if and only if both endpoints and their flags are equal.
	 * 
	 * @param vc
	 *            Value to compare this to.
	 * @return <code>true</code> if values are equal, <code>false</code>
	 *         otherwise.
	 * @throws ValueTypeIncorrectException
	 *             If endpoints in ranges being compared do not have matching
	 *             types.
	 */
	public boolean equals(ValueRange<?> vc) throws ValueTypeIncorrectException {

		boolean res = true;

		if (valueMin != null) {
			res &= valueMin.equals(vc.valueMin);
		} else {
			res &= (vc.valueMin == null);
		}

		if (valueMax != null) {
			res &= valueMax.equals(vc.valueMax);
		} else {
			res &= (vc.valueMax == null);
		}

		res &= (leftOpen == vc.leftOpen) & (rightOpen == vc.rightOpen);

		return res;
	}

	/**
	 * Test whether left endpoint of the interval is open.
	 * 
	 * @return <code>true</code> if left endpoint is open, <code>false</code> if
	 *         it is closed.
	 */
	public boolean isLeftOpen() {

		return leftOpen;
	}

	/**
	 * Test whether right endpoint of the interval is open.
	 * 
	 * @return <code>true</code> if right endpoint is open, <code>false</code>
	 *         if it is closed.
	 */
	public boolean isRightOpen() {

		return rightOpen;
	}

	/**
	 * Set endpoint type for left endpoint.
	 * 
	 * @param open
	 *            If set to <code>true</code> endpoint will be set as open. If
	 *            set to <code>false</code> endpoint is set as closed (unless it
	 *            is -&infin; which is always open).
	 */
	public void setLeftOpen(boolean open) {

		leftOpen = open;

		if (!leftOpen && (valueMin == null)) {
			leftOpen = true;
		}
	}

	/**
	 * Set endpoint type for right endpoint.
	 * 
	 * @param open
	 *            If set to <code>true</code> endpoint will be set as open. If
	 *            set to <code>false</code> endpoint is set as closed (unless it
	 *            is +&infin; which is always open).
	 */
	public void setRightOpen(boolean open) {

		rightOpen = open;

		if (!rightOpen && (valueMax == null)) {
			rightOpen = true;
		}
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		return toString().hashCode();
	}

	/**
	 * Empty class name (duh).
	 */
	private static final String EMPTY_CLASS_NAME = "(none)";

	/*
	 * @see
	 * cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode
	 * (org.w3c.dom.Node)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void parseXMLNode(Node node) throws InputParseException {

		if (!node.getNodeName().equals(getXMLNodeName())) {
			throw new InputParseException(
					"Node does not contain range data. Node name is \""
							+ node.getNodeName() + "\".");
		}

		String elementClassName = XMLHelper.getAttributeValueByName(
				"subtype",
				node);
		elementType = ValueType.forName(elementClassName);

		if (null == elementType) {
			// both endpoints are null
			valueMin = valueMax = null;
			leftOpen = rightOpen = true;
		} else {

			valueMax = valueMin = null;
			leftOpen = rightOpen = true;

			if (XMLHelper.hasSubNode("min", node)) {
				// load lower bound data
				Node min = null;

				try {
					min = XMLHelper.getSubNodeByName("min", node);
				} catch (Exception e) {
					assert false : "Node has mysteriously disapperead.";
				}

				leftOpen = XMLHelper.getAttributeValueByName("open", min)
						.equals("yes");

				try {
					valueMin = (T) Class.forName(elementType.NAME)
							.newInstance();
				} catch (Exception e) {
					throw new InputParseException(
							"Unable to create instance of the lower bound class.",
							e);
				}

				try {
					valueMin.parseXMLNode(XMLHelper.getSubNodeByName(
							valueMin.getXMLNodeName(),
							min));
				} catch (NullPointerException e) {
					throw new InputParseException(
							"Error parsing lower bound data.",
							e);
				}
			}

			if (XMLHelper.hasSubNode("max", node)) {
				// load upper bound data
				Node max = null;

				try {
					max = XMLHelper.getSubNodeByName("max", node);
				} catch (Exception e) {
					assert false : "Node has mysteriously disapperead.";
				}

				rightOpen = XMLHelper.getAttributeValueByName("open", max)
						.equals("yes");

				try {
					valueMax = (T) Class.forName(elementType.NAME)
							.newInstance();
				} catch (Exception e) {
					throw new InputParseException(
							"Unable to create instance of the upper bound class.",
							e);
				}

				try {
					valueMax.parseXMLNode(XMLHelper.getSubNodeByName(
							valueMax.getXMLNodeName(),
							max));
				} catch (NullPointerException e) {
					throw new InputParseException(
							"Error parsing upper bound data.",
							e);
				}
			}
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#
	 * exportAsElement(org.w3c.dom.Document)
	 */
	@Override
	public Element exportAsElement(Document document) {

		/*
		 * Resulting node
		 * 
		 * <range subtype="<subtype-name>"> <left open="<is-open>">
		 * <subtypenode/> </left> <right open="<is-open>"> <subtypenode/>
		 * </right> </range>
		 * 
		 * where <subtype-name> is canonical name of the endpoints class,
		 * <is-open> is either "yes" if given side of the range is open or "no"
		 * if it is closed, <subtypenode> is serialised node of the subtype (for
		 * more info about these see serialisation methods for ValueInteger,
		 * ValueBoolean, ValueString and ValueRegexp). If one of the endpoints
		 * is null, node for that endpoint is not present.
		 */

		Element element = document.createElement(getXMLNodeName());

		element.setAttribute("subtype", null == elementType ? EMPTY_CLASS_NAME
				: elementType.NAME);

		if (valueMin != null) {
			Element min = document.createElement("min");

			min.setAttribute("open", leftOpen ? "yes" : "no");
			element.appendChild(min);

			min.appendChild(valueMin.exportAsElement(document));
		}

		if (valueMax != null) {
			Element max = document.createElement("max");

			max.setAttribute("open", rightOpen ? "yes" : "no");
			element.appendChild(max);

			max.appendChild(valueMax.exportAsElement(document));
		}

		return element;
	}

	/*
	 * @see
	 * cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName
	 * ()
	 */
	@Override
	public String getXMLNodeName() {

		return "range";
	}

	/**
	 * @return Value of the right endpoint of the range.
	 */
	public T getMaxValue() {

		return valueMax;
	}

	/**
	 * @param valueMax
	 *            New value of the right endpoint of the range.
	 */
	public void setMaxValue(T valueMax) {

		this.valueMax = valueMax;
	}

	/**
	 * @return Value of the left endpoint of the range.
	 */
	public T getMinValue() {

		return valueMin;
	}

	/**
	 * @param valueMin
	 *            New value of the left endpoint of the range.
	 */
	public void setMinValue(T valueMin) {

		this.valueMin = valueMin;
	}

	@Override
	public ValueType getElementType() {

		return elementType;
	}

	@Override
	public ValueType getType() {

		return ValueType.RANGE;
	}
}
