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
package cz.cuni.mff.been.webinterface.screen;

import java.io.Serializable;

/**
 * Abstract class, which represents one item in the section of the plugin
 * configuration wizard screen. Items are controls, which allow to set
 * individual configuration values and are rendered as HTML form elements.
 * 
 * Subclasses of <code>Item</code> implement various types of controls. 
 * 
 * @author David Majda
 */
public abstract class Item implements Serializable {

	private static final long	serialVersionUID	= -2985112391145858545L;

	/** Item identifier - may be null */
	private String id;

	/** Item label. */
	private String label;
	
	/** Parent section. Initialised to <code>null</code>, which
	 * is invalid value, but this valued is changed in the <code>Section</code>
	 * constructor, where the items are integrated to their sections.*/
	private Section parent;
	
	/**
	 * Item's index in the parent section. Initialised to <code>-1</code>, which
	 * is invalid value, but this valued is changed in the <code>Section</code>
	 * constructor, where the items are integrated to their sections.
	 */
	private int index = -1;

	/** @return item id */
	public String getId(){
		return id;
	}

	/** @return item label */
	public String getLabel() {
		return label;
	}

	/** @return parent section */
	public Section getParent() {
		return parent;
	}

	/**
	 * Sets the parent section.
	 * 
	 * @param parent parent section to set
	 */
	public void setParent(Section parent) {
		this.parent = parent;
	}

	/** @return item's index in the parent section */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Sets the item's index in the parent section.
	 * 
	 * @param index item's index in the parent section to set
	 */
	void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Allocates a new <code>Item</code> object.
	 * 
	 * @param id item identifier
	 * @param label item label
	 */
	public Item(String id, String label) {
		this.id = id;
		this.label = label;
	}
	
	/**
	 * Returns base of the name of the form element name represented by this item.
	 * 
	 * @return base of name of the form element name represented by this item
	 */
	public String formElementNameBase() {
		assert parent != null : "Item's parent can't be null.";
		assert index != -1 : "Item's index can't be -1.";
		
		return parent.formElementNamePrefix() + "-" + Integer.toString(index);
	}

	/**
	 * Returns name of the form element name represented by this item.
	 * 
	 * @return name of the form element name represented by this item
	 */
	public String formElementName() {
		assert parent != null : "Item's parent can't be null.";
		assert index != -1 : "Item's index can't be -1.";
		
		return "item[" + formElementNameBase() + "]";
	}

	/**
	 * @return all the values selected by this item.
	 * Most of subclasses will return only one row.
	 */
	public abstract String[] getValues();

	/**
	 * Convenience function substitute for {@code getValues()[0]}.
	 * This function is good for use with single-value items.
	 * For {@code MultiSelect}s you should use {@code getValues()}
	 * 
	 * @return First value 
	 */
	public abstract String getValue();
}
