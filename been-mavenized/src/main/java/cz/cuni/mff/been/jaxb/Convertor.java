/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
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
package cz.cuni.mff.been.jaxb;

import cz.cuni.mff.been.common.rsl.Condition;
import cz.cuni.mff.been.common.rsl.ParseException;
import cz.cuni.mff.been.common.rsl.ParserWrapper;

/**
 * A static class with convertors for some unusual binding types, especially those for which
 * data conversion can throw. Note that only parsers are needed here. For printing, toString()
 * is sufficient in most cases.
 * 
 * @author Andrej Podzimek
 */
public final class Convertor {
	
	/**
	 * No, don't do this.
	 */
	private Convertor() {
	}

	/**
	 * Parses a RSL string to a Condition object.
	 * 
	 * @param rsl The RSL expression to parse.
	 * @return A Condition object representing the expression.
	 */
	public static Condition parseRSL( String rsl ) {
		try {
			return ParserWrapper.parseString( rsl );
		} catch ( ParseException exception ) {
			throw new ConvertorTransparentException( "Malformed RSL expression.", exception );
		}
	}
}
