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
 * Represents a multiselect control in the plugin configuration wizard screen.
 * 
 * Multiselect control allows user to choose any number of options form a set.
 * Each option is represented by instance of the <code>Option</code> class.
 * 
 * @author David Majda
 * @author Michal Tomcanyi
 * @author Jiri Tauber
 */
public class MultiSelect extends Item {

	private static final long	serialVersionUID	= -3158839898084354006L;

	/** Set of options. */
	private Option[] options;
	
	/**
	 * Indexes of selected options. Allowed values are
	 * <code>0</code>..<code>options.length - 1</code>.
	 */
	private int[] selectedIndexes;

	/** Switch for allowing multiple selections of the same item. */
	private boolean allowMultiple = false; 

	/** @return set of options */
	public Option[] getOptions() {
		return options;
	}
		
	/** @return indexes of selected options */
	public int[] getSelectedIndexes() {
		return selectedIndexes;
	}
	
	private void verifySelectedIndexes(Option[] opts, int[] selected) {
		for (int i = 0; i < selected.length; i++) {
			if (selected[i] < 0 || opts.length <= selected[i]) {
				throw new IllegalArgumentException(
					"Invalid selectedIndexes[" + i + "] (" + selected[i] + ")."
				);
			}
		}
		if( !allowMultiple ){
			Arrays.sort(selected);
			for (int i = 0; i < selected.length-1; i++) {
				if( selected[i] == selected[i+1]){
					throw new IllegalArgumentException(
							"Repeating selectedIndexes (" +	selected[i] + ")"
					);
				}
			}
		}
	}

	/**
	 * Sets the indexes of selected options.
	 * 
	 * @param selectedIndexes indexes of selected options to set
	 * 
	 * @throws IllegalArgumentException if any of <code>selectedIndexes</code> is
	 *          not in range <code>0</code>..<code>options.length - 1</code>
	 */
	public void setSelectedIndexes(int[] selectedIndexes) {
		verifySelectedIndexes(options, selectedIndexes);
		this.selectedIndexes = selectedIndexes;
	}
	
	public String[] getSelectedIds() {
		String[] result = new String[selectedIndexes.length];
		for (int i : selectedIndexes) {
			result[i] = options[selectedIndexes[i]].getId();
		}
		return result;
	}
	
	/** @return whether this MultiSelect allows multiple seletions of the same item */
	public boolean getAllowMultiple(){
		return allowMultiple;
	}
	/**
	 * Selects the options by their ID.
	 * 
	 * @param selectedIds IDs of the options to select
	 * 
	 * @throws IllegalArgumentException if some option does not have the ID 
	 *          specified in <code>selectedIds</code>
	 */
	public void setSelectedIds(String[] selectedIds) {
		int[] newSelectedIndexes = new int[selectedIds.length];
		for (int i = 0; i < selectedIds.length; i++) {
			for (int j = 0; j < options.length; i++) {
				if (options[j].getId().equals(selectedIds[i])) {
					newSelectedIndexes[i] = j;
					break;
				}
			}
			assert selectedIds[i] != null
				: "Invalid selectedIds[" + i + "] (\"" + selectedIds[i] + "\").";
		}
	}

	/**
	 * Allocates a new <code>MultiSelect</code> object.
	 * 
	 * @param label select label
	 * @param options set of options
	 * @param selectedIndexes indexes of selected option
	 * 
	 * @throws IllegalArgumentException if any of <code>selectedIndexes</code> is
	 *          not in range <code>0</code>..<code>options.length - 1</code>
	 */
	@Deprecated
	public MultiSelect(String label, Option[] options, int[] selectedIndexes) {
		super(null, label);

		verifySelectedIndexes(options, selectedIndexes);
		
		this.options = options;
		this.selectedIndexes = selectedIndexes;
	}
	
	/**
	 * Allocates a new <code>MultiSelect</code> object.
	 * 
	 * @param id the item identifier
	 * @param label select label
	 * @param options set of options
	 * @param allowMultiple flag for allowing multiple selections of one option
	 * @param selectedIndexes indexes of selected option
	 * 
	 * @throws IllegalArgumentException if any of <code>selectedIndexes</code> is
	 *          not in range <code>0</code>..<code>options.length - 1</code> or
	 *          allowRepeating is set to <code>false</code> and
	 *          <code>selectedIndexes</code> contains repeating values
	 */
	public MultiSelect(String id, String label, Option[] options, boolean allowMultiple, int[] selectedIndexes) {
		super(id, label);

		this.allowMultiple = allowMultiple;
		verifySelectedIndexes(options, selectedIndexes);

		this.options = options;
		this.selectedIndexes = selectedIndexes;
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MultiSelect(name=" + getLabel() + ", options="
			+ Arrays.toString(options) + ")";
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.webinterface.screen.Item#getValues()
	 */
	@Override
	public String[] getValues() {
		String[] result = new String[selectedIndexes.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = options[selectedIndexes[i]].getId();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.webinterface.screen.Item#getValue()
	 */
	@Override
	public String getValue() {
		return options[selectedIndexes[0]].getId();
	}

}
