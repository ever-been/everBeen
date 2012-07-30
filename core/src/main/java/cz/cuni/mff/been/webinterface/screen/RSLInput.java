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

/**
 * Represents an input control for entering of the RSL expressions in the plugin
 * configuration wizard screen.
 * 
 * @author David Majda
 * @author Michal Tomcanyi
 */
public class RSLInput extends Item {

	private static final long	serialVersionUID	= 6524408555705738464L;

	/** Input value. */
	private String value;

	/** @return input value */
	@Override
	public String getValue() {
		return value;
	}

	/**
	 * Sets the input value.
	 * 
	 * @param value the input value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Allocates a new <code>RSLInput</code> object.
	 * 
	 * @param id input identifier
	 * @param label input label
	 * @param value input value
	 */
	public RSLInput(String id, String label, String value) {
		super(id, label);
		this.value = value;
	}
	
	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RSLInput(name=" + getLabel() + ", value='" + value + "')";
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.webinterface.screen.Item#getValues()
	 */
	@Override
	public String[] getValues() {
		return new String[]{value};
	}
}
