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

import java.util.Arrays;

public class RadiosWithSections extends Item {

	private static final long	serialVersionUID	= 8746606258565045990L;

	/** Set of items. */
	private RadioWithSectionItem[] items;
	
	/**
	 * Index of selected item. Allowed values are
	 * <code>0</code>..<code>items.length - 1</code>.
	 */
	private int selectedIndex;
	
	/** @return set of items */
	public RadioWithSectionItem[] getItems() {
		return items;
	}

	/** @return selected item */
	public RadioWithSectionItem getSelectedItem(){
		return items[selectedIndex];
	}

	/** @return index of selected item */
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	/**
	 * Sets the index of selected item.
	 * 
	 * @param selectedIndex index of selected item to set
	 * 
	 * @throws IllegalArgumentException if <code>selectedIndex</code> is not in
	 *                                   range <code>0</code>..<code>items.length - 1</code>
	 */
	public void setSelectedIndex(int selectedIndex) {
		if (selectedIndex < 0 || items.length <= selectedIndex) { 
			throw new IllegalArgumentException(
				"Invalid selectedIndex (" + selectedIndex + ")."
			);
		}
		
		this.selectedIndex = selectedIndex;
	}
	
	/** @return ID of the selected option */
	public String getSelectedId() {
		return items[selectedIndex].getOption().getId();
	}
	
	/**
	 * Selects an item by its option ID.
	 * 
	 * @param selectedId ID of the item's option to select
	 * 
	 * @throws IllegalArgumentException if no item's option has the ID 
	 *                                   <code>selectedId</code>
	 */
	public void setSelectedId(String selectedId) {
		for (int i = 0; i < items.length; i++) {
			if (items[i].getOption().getId().equals(selectedId)) {
				selectedIndex = i;
				return;
			}
		}
		
		throw new IllegalArgumentException(
			"Invalid selectedId (\"" + selectedId + "\")."
		);
	}

	/**
	 * Allocates a new <code>RadiosWithSections</code> object.
	 * 
	 * @param id item identifier
	 * @param items set of items
	 * @param selectedIndex index of selected option
	 * 
	 * @throws IllegalArgumentException if <code>selectedIndex</code> is not in
	 *                                   range <code>0</code>..<code>items.length - 1</code>
	 */
	public RadiosWithSections(String id, RadioWithSectionItem[] items, int selectedIndex) {
		super(id, null);

		if (selectedIndex < 0 || items.length <= selectedIndex) { 
			throw new IllegalArgumentException(
				"Invalid selectedIndex (" + selectedIndex + ")."
			);
		}
		
		this.items = items;
		this.selectedIndex = selectedIndex;

		for (int i = 0; i < this.items.length; i++) {
			Section section = this.items[i].getSection();
			if (section != null) {
				section.setParent(this);
				section.setIndex(i);
			}
		}		
	}
	
	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RadiosWithSections(name=" + getLabel() + ", items="
			+ Arrays.toString(items) + ")";
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.webinterface.screen.Item#getValues()
	 */
	@Override
	public String[] getValues() {
		return new String[]{items[selectedIndex].getOption().getId()};
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.webinterface.screen.Item#getValue()
	 */
	@Override
	public String getValue() {
		return items[selectedIndex].getOption().getId();
	}
}
