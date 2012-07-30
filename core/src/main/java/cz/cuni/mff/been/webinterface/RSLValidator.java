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
package cz.cuni.mff.been.webinterface;

import cz.cuni.mff.been.common.rsl.ParserWrapper;
import cz.cuni.mff.been.common.rsl.Token;

/**
 * Simple class for syntactic checking of the RSL queries in the web interface.
 * It has only one public method, <code>validate</code>, which checks the RSL
 * query string and returns possible error messages in HTML format suitable for
 * output in the web interface. 
 * 
 * @author David Majda
 */
public class RSLValidator {
	private static final int TOKEN_KIND_EOF = 0;

	private static String formatCode(String rsl, Token errorToken,
		int maxExpectedTokenCount) {
		/* Compute begin and end columns.
		 * 
		 * Note columns in the tokens are 1-based, but sometimes
		 * Token.beginColumn is 0 (e.g. when empty RSL string is entered). We
		 * must handle it.
		 * 
		 * Our beginColumn and endColumn variables are 0-based and endColumn
		 * actually points *after* the token, so it can passed to
		 * String.substring easily.  
		 */
		int beginColumn = errorToken.beginColumn > 0
		? errorToken.beginColumn - 1
			: 0;
		int endColumn = errorToken.endColumn;

		Token t = errorToken;
		for (int i = 0; i < maxExpectedTokenCount; i++) {
			endColumn = t.endColumn;
			if (t.kind == TOKEN_KIND_EOF) {
				break;
			}
		}

		String result = "";

		/* Format the code. */
		result += "<pre>";
		result += "  " + Routines.htmlspecialchars(
			rsl.substring(0, beginColumn)
		);
		result += "<span style='background-color: #C07E00'>"
			+ Routines.htmlspecialchars(
				rsl.substring(beginColumn, endColumn)
			)
			+ "</span>";
		result += Routines.htmlspecialchars(
			rsl.substring(endColumn, rsl.length())
		);
		result += "</pre>";

		return result;
	}

	/* For some reason, Tomcat reports "Unresolved compilation problems" when
	 * PackageException is used in unqualified form (without the package name).
	 */
	private static String formatEncounteredTokens(
		cz.cuni.mff.been.common.rsl.ParseException e,
		Token errorToken, int maxExpectedTokenCount) {
		String result = "";

		Token t = errorToken;
		for (int i = 0; i < maxExpectedTokenCount; i++) {
			if (i != 0) {
				result += " ";
			}
			if (t.kind == TOKEN_KIND_EOF) {
				result += e.tokenImage[TOKEN_KIND_EOF];
				break;
			}
			result += Routines.htmlspecialchars(t.image);
			t = t.next; 
		}

		return result;
	}

	/* For some reason, Tomcat reports "Unresolved compilation problems" when
	 * PackageException is used in unqualified form (without the package name).
	 */
	private static String formatExpectedTokens(
		cz.cuni.mff.been.common.rsl.ParseException e) {
		String result = "";

		for (int i = 0; i < e.expectedTokenSequences.length; i++) {
			if (i != 0) {
				result += " or ";
			}
			for (int j = 0; j < e.expectedTokenSequences[i].length; j++) {
				result += e.tokenImage[e.expectedTokenSequences[i][j]] + " ";
			}
			if (e.expectedTokenSequences[i][e.expectedTokenSequences[i].length - 1]
			                                != TOKEN_KIND_EOF) {
				result += "...";
			}
		}

		return result;
	}

	/* For some reason, Tomcat reports "Unresolved compilation problems" when
	 * PackageException is used in unqualified form (without the package name).
	 */
	private static String formatParseExceptionMessage(String rsl,
		cz.cuni.mff.been.common.rsl.ParseException e) {
		String result = "";
		Token errorToken = e.currentToken.next;

		/* Error location. */
		result += "RSL syntax error at column " + errorToken.beginColumn + ":";

		/* Compute maximum size of the expected token chain. */
		int maxExpectedTokenCount = 0;
		for (int i = 0; i < e.expectedTokenSequences.length; i++) {
			if (maxExpectedTokenCount < e.expectedTokenSequences[i].length) {
				maxExpectedTokenCount = e.expectedTokenSequences[i].length;
			}
		}

		/* Code with marked-up error token. */
		result += formatCode(rsl, errorToken, maxExpectedTokenCount);

		/* What we encountered. */
		result += "Encountered \""
			+ formatEncounteredTokens(e, errorToken, maxExpectedTokenCount)
			+ "\", ";

		/* What we expected. */
		result += "expected " + formatExpectedTokens(e);

		return result;
	}

	/**
	 * Syntactically checks the RSL query string and returns possible error
	 * messages in HTML format suitable for output in the web interface. 
	 * 
	 * @param rsl RSL string to check
	 * @return error messages in HTML format or <code>null</code> if the RSL
	 *          query string is syntactically correct
	 */
	public static String validate(String rsl) {
		String result = null;
		try {
			ParserWrapper.parseString(rsl);
			/* For some reason, Tomcat reports "Unresolved compilation problems" when
			 * PackageException is used in unqualified form (without the package name).
			 */
		} catch (cz.cuni.mff.been.common.rsl.ParseException e) {
			result = formatParseExceptionMessage(rsl, e);
		}
		return result;
	}

	/**
	 * Private constructor to prevent instantiation.
	 */
	private RSLValidator() {
	}
}
