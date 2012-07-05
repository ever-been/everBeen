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
 * Represents one option of the select or multiselect control in the plugin
 * configuration wizard screen.
 * 
 * @author David Majda
 * @author Michal Tomcanyi
 */
public class Option implements Serializable {

	private static final long	serialVersionUID	= -5982816586514141614L;

	/** Option ID. */
	private String id;
	
	/** Option label. */
	private String label;
	
	/** @return option ID */
	public String getId() {
		return id;
	}
	
	/** @return option label */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Allocates a new <code>Option</code> object.
	 * 
	 * @param id option ID
	 * @param label option label
	 */
	public Option(String id, String label) {
		this.id = id;
		this.label = label;
	}
	
	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id;
	}
}
