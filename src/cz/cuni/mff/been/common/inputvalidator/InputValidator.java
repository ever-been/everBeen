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
package cz.cuni.mff.been.common.inputvalidator;

import java.io.Serializable;

/**
 * Validator for the input control in the plugin configuration wizard screen.
 * 
 * Plugin configurator sends subclasses of this class with the input controls to
 * the web interface, which uses them  to validate user's input and display
 * error messages, if the input is incorrect. 
 * 
 * @author David Majda
 */
public abstract class InputValidator implements Serializable {

	private static final long	serialVersionUID	= 142308854211075837L;

	/**
	 * Validates user's input.
	 *  
	 * @param value input to validate
	 * @return <code>null</code> if the input is valid,
	 *          or error message describing the error if the input is invalid 
	 */
	public abstract String validate(String value);
}

