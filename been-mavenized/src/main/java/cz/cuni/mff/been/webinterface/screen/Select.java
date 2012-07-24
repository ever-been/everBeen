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

/**
 * Represents a select control in the plugin configuration wizard screen.
 * 
 * Select control allows user to choose one option from a set. Each option is
 * represented by instance of the <code>Option</code> class.
 * 
 * @author David Majda
 * @author Michal Tomcanyi
 */
public class Select extends Item {

	private static final long	serialVersionUID	= 4549868880597024013L;

	/** Set of options. */
	private Option[] options;
	/**
	 * Index of selected option. Allowed values are
	 * <code>0</code>..<code>options.length - 1</code>.
	 */
	private int selectedIndex;
	
	/** @return set of options */
	public Option[] getOptions() {
		return options;
	}
	
	/** @return index of selected option */
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	/**
	 * Sets the index of selected option.
	 * 
	 * @param selectedIndex index of selected option to set
	 * 
	 * @throws IllegalArgumentException if <code>selectedIndex</code> is not in
	 *                                   range <code>0</code>..<code>options.length - 1</code>
	 */
	public void setSelectedIndex(int selectedIndex) {
		if (selectedIndex < 0 || options.length <= selectedIndex) { 
			throw new IllegalArgumentException(
				"Invalid selectedIndex (" + selectedIndex + ")."
			);
		}
		
		this.selectedIndex = selectedIndex;
	}
	
	/** @return ID of the selected option */
	public String getSelectedId() {
		return options[selectedIndex].getId();
	}
	
	/**
	 * Selects an option by it's ID.
	 * 
	 * @param selectedId ID of the option to select
	 * 
	 * @throws IllegalArgumentException if no option has the ID 
	 *                                   <code>selectedId</code>
	 */
	public void setSelectedId(String selectedId) {
		for (int i = 0; i < options.length; i++) {
			if (options[i].getId().equals(selectedId)) {
				selectedIndex = i;
				return;
			}
		}
		
		throw new IllegalArgumentException(
			"Invalid selectedId (\"" + selectedId + "\")."
		);
	}

	/**
	 * Allocates a new <code>Select</code> object.
	 * 
	 * @param id select identifier
	 * @param label select label
	 * @param options set of optsions
	 * @param selectedIndex index of selected option
	 * 
	 * @throws IllegalArgumentException if <code>selectedIndex</code> is not in
	 *                                   range <code>0</code>..<code>options.length - 1</code>
	 */
	public Select(String id, String label, Option[] options, int selectedIndex) {
		super(id, label);

		if (selectedIndex < 0 || options.length <= selectedIndex) { 
			throw new IllegalArgumentException(
				"Invalid selectedIndex (" + selectedIndex + ")."
			);
		}
		
		this.options = options;
		this.selectedIndex = selectedIndex;
	}
	
	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Select(name=" + getLabel() + ", options="
			+ Arrays.toString(options) + ")";
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.webinterface.screen.Item#getValues()
	 */
	@Override
	public String[] getValues() {
		return new String[]{options[selectedIndex].getId()};
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.webinterface.screen.Item#getValue()
	 */
	@Override
	public String getValue() {
		return options[selectedIndex].getId();
	}
}
