/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
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
 * Input is valid when it is java identifier 
 * 
 * @author Jiri Tauber
 *
 */
public class JavaIdentifierInputValidator extends InputValidator {

	private static final long serialVersionUID = 4551953045297549694L;
	private static final String JAVA_ID_REGEXP = "[a-zA-Z][a-zA-Z0-9_]*";

	/* (non-Javadoc)
	 * @see cz.cuni.mff.been.common.inputvalidator.InputValidator#validate(java.lang.String)
	 */
	@Override
	public String validate(String value) {
		if( !value.matches(JAVA_ID_REGEXP) ){
			return "Value must be Java Identifier!";
		}
		return null;
	}

}
