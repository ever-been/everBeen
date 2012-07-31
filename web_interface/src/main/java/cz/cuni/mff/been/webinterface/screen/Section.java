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
import java.util.Arrays;

/**
 * Represents one section of the plugin configuration wizard screen.
 * 
 * @author David Majda
 */
public class Section implements Serializable  {

	private static final long	serialVersionUID	= 579364853286516034L;

	/** Section items. */
	private Item[] items;
	/**
	 * Section title. Can be <code>null</code> to express that section has no
	 * title. 
	 */
	private String title;
	/**
	 * Section desription. Can be <code>null</code> to express that section has no
	 * description. 
	 */
	private String description;
	/** Parent item or <code>null</code>, if this section is at the top level. */
	private Item parent;
	/**
	 * Item's index in the parent item, or <code>-1</code>, if this section is
	 * at the top level. 
	 */
	private int index = -1;
	
	/** @return section items */
	public Item[] getItems() {
		return items;
	}

	/** @return section title */
	public String getTitle() {
		return title;
	}
	
	/** @return section description */
	public String getDescription() {
		return description;
	}
	
	/** @return parent item or <code>null</code> */
	public Item getParent() {
		return parent;
	}

	/**
	 * Sets the parent item.
	 * 
	 * @param parent parent item to set or <code>null</code>
	 */
	public void setParent(Item parent) {
		this.parent = parent;
	}

	/** @return item's index in the parent item or <code>-1</code> */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Sets the item's index in the parent item.
	 * 
	 * @param index item's index in the parent item to set or <code>-1</code>
	 */
	void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Allocates a new <code>Section</code> object.
	 * 
	 * @param items section items
	 * @param title section title; can be <code>null</code> to express that
	 *         section has no title
	 * @param description section description; can be <code>null</code> to
	 *         express that section has no description
	 */
	public Section(Item[] items, String title, String description) {
		this.items = items;
		this.title = title;
		this.description = description;
		
		for (int i = 0; i < this.items.length; i++) {
			this.items[i].setParent(this);
			this.items[i].setIndex(i);
		}
	}
	
	/**
	 * Returns prefix of the name of the form elements in this section.
	 * 
	 * @return prefix of the name of the form elements in this section
	 */
	public String formElementNamePrefix() {
		return (parent != null ? parent.formElementNameBase() + "-" : "")
			+ Integer.toString(index);
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Section("
			+ (title == null ? title : "(no title)") + ","
			+ (description == null ? title : "(no description)")
			+ ","	+ Arrays.toString(items) + ")";
	}
}
