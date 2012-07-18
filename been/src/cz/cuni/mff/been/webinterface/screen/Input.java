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

import cz.cuni.mff.been.common.inputvalidator.InputValidator;

/**
 * Represents an input control in the plugin configuration wizard screen.
 * 
 * Input control allows user to enter any string, vhich is then validated (on
 * the web interface side) by the validator (instance of the
 * <code>InputValidator</code> class).
 * 
 * @author David Majda
 * @author Michal Tomcanyi
 */
public class Input extends Item {

	private static final long	serialVersionUID	= 6047201615149592795L;

	/**
	 * Input size.
	 * 
	 * @author David Majda
	 */
	public enum Size {
		/** Small size. Should be used for entering numbers, versions etc. */
		SMALL,
		/** Big size. Should be used for entering longer strings. */
		BIG,
		/** Text Area. Should be used for entering multi-line strings. */
		AREA
	}

	/** Input value. */
	private String value;

	/** Input size. */
	private Size size;
	
	/** Input validator. Can be <code>null</code> if no validation is needed. */
	private InputValidator validator;
	
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
	
	/** @return input size */
	public Size getSize() {
		return size;
	}

	/** @return input validator */
	public InputValidator getValidator() {
		return validator;
	}

	/**
	 * Allocates a new <code>Input</code> object.
	 *
	 * @param id input identifier
	 * @param label input label
	 * @param value input value
	 * @param size input size
	 * @param validator input validator; can be <code>null</code> if no
	 *         validation is needed
	 */
	public Input(String id, String label, String value, Size size, InputValidator validator) {
		super(id, label);
		this.value = value;
		this.size = size;
		this.validator = validator;
	}
	
	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Input(name=" + getLabel() + ", value='" + value + "')";
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.webinterface.screen.Item#getValues()
	 */
	@Override
	public String[] getValues() {
		return new String[]{value};
	}

}
