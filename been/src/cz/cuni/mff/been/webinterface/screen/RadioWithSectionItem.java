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
 * Represents one item of the radio-with-sections control in the plugin
 * configuration wizard screen.
 * 
 * @author David Majda
 */
public class RadioWithSectionItem implements Serializable {

	private static final long	serialVersionUID	= -7442954629577916416L;

	/** Item option. */
	private Option option;
	
	/** Item section. */
	private Section section;

	/** @return the option */
	public Option getOption() {
		return option;
	}
	
	/**
	 * Sets the item option. 
	 *
	 * @param option the option to set 
	 */
	public void setOption(Option option) {
		this.option = option;
	}
	
	/** @return the section */
	public Section getSection() {
		return section;
	}

	/**
	 * Sets the item section.
	 * 
	 * @param section the section to set
	 */
	public void setSection(Section section) {
		this.section = section;
	}

	/**
	 * Allocates a new <code>RadioWithSectionItem</code> object.
	 * 
	 * @param option item option
	 * @param section item section
	 */
	public RadioWithSectionItem(Option option, Section section) {
		this.option = option;
		this.section = section;
	}		

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RadioWithSectionItem(option=" + getOption() + ", section="
			+ section + ")";
	}
}
