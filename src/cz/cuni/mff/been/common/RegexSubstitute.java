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
package cz.cuni.mff.been.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.PatternSyntaxException;

/**
 * Performs regular expression substitution in a file.
 * 
 * @author Jaroslav Urban
 */
public class RegexSubstitute {
	/**
	 * Substitutes every match of a regular expression in a file with a 
	 * substitution string.
	 * 
	 * @param path path to the file.
	 * @param regex regular expression.
	 * @param substitution substitution of the matched strings.
	 * @throws IOException if an IO error occured during the editing of the file.
	 * @throws PatternSyntaxException if a syntax error was found in the regular 
	 * expression. 
	 */
	//@SuppressWarnings("null")
	public static void substitute(String path, String regex, String substitution)
	throws IOException, PatternSyntaxException {
		// we don't want to use the the match in the substitution string, so escape all backslashes
		// this way the substitution string can contain backslashes without sideeffects
		String newSubstitution = substitution.replaceAll("\\\\", "\\\\\\\\");

		StringBuffer contents = new StringBuffer();
		/* Read the original contents of the file */
		BufferedReader input = null;

		String line = null;
		input = new BufferedReader(new FileReader(path));

		while ((line = input.readLine()) != null) {
			contents.append(line + "\n");
		}

		input.close();

		/* Create the new contents of the file, i.e. do the substitution */
		String newContents = null;
		newContents = (contents.toString()).replaceAll(regex, newSubstitution);

		/* Overwrite the file with new contents */
		BufferedWriter output = null;
		output = new BufferedWriter(new FileWriter(path));
		output.write(newContents.toString());

		output.close();
	}
}
