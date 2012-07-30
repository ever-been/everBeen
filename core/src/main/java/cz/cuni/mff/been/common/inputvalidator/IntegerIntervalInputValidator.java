/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jaroslav Urban
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

/**
 * Input validator. The value must be an integer in a set interval.
 * 
 * @author Jaroslav Urban
 */
public class IntegerIntervalInputValidator extends IntegerInputValidator {

	private static final long	serialVersionUID	= 572984153893841841L;

	/** Minimum allowed value. */ 
	private int min;

	/** Maximum allowed value. */
	private int max;
	
	/**
	 * Allocates a new <code>IntegerIntervalInputValidator</code> object.
	 *
	 * @param min
	 * @param max
	 */
	public IntegerIntervalInputValidator(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	@Override
	public String validate(String value) {
		String message;
		if ((message = super.validate(value)) != null) {
			return message;
		}
		
		int i = Integer.valueOf(value);
		if (i < min) {
			return "Invalid value '" + value 
				+ "'. Must be greater or equal to '" + min + "'.";
		}
		if (i > max) {
			return "Invalid value '" + value 
				+ "'. Must be less or equal to '" + max + "'.";
		}
		
		// OK
		return null;
	}
}
