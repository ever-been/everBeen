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
package cz.cuni.mff.been.common.rsl;

import java.io.StringReader;

/**
 * Little class which wraps the RSL parser. It exists for two reasons:
 * 
 * 1. To simplify the invocation of the parser.
 * 2. The Parser class has only package-level visibility and we want to expose
 *    it.
 * 
 * @author David Majda
 */
public class ParserWrapper {
	/**
	 * Parses RSL expression given as a parameter and returns its representation
	 * as a <code>Condition</code> object. 
	 * 
	 * @param s RSL expression to parser
	 * @return <code>Condtition</code> representing parsed RSL expression
	 * @throws ParseException if some parsing error occurs
	 */
	public static Condition parseString(String s) throws ParseException {
		Parser p = new Parser(new StringReader(s));
		return p.Start(System.out);
	}

	/**
	 * Private construcor is so no instances can be created.
	 */
	private ParserWrapper() {
	}
}
