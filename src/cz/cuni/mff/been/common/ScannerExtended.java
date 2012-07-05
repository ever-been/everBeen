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

import java.util.NoSuchElementException;

/**
 * Extension routines for java.util.Scanner, 
 * to support scanning of quoted strings.
 */
public class ScannerExtended {

	private java.util.Scanner sc;

	/**
	 * Allocates a new <code>ScannerExtended</code> object.
	 *
	 * @param sc scanner that should be extended
	 */
	public ScannerExtended(java.util.Scanner sc) {
		this.sc = sc; 
	}
	
	/**
	 * Returns true if the next token is the beginning of a quoted string value.
	 * 
	 * @return true if the next token is the beginning of a quoted string value.
	 */
	private boolean hasNextQuotedString() {
		return sc.hasNext("\\\".*"); 
	}
	
	/**
	 * Scans a quoted string value from the scanner input.
	 * 
	 * @return quoted string value
	 */
	private String nextQuotedString() {
		if (!sc.hasNext()) {
			throw new java.util.NoSuchElementException();
		}
		if (!hasNextQuotedString()) {
			throw new java.util.InputMismatchException();
		}
		// This is necessary because findInLine would skip over other tokens.
		String s = sc.findInLine("\\\".*?\\\"");
		if (s == null) {
			throw new java.util.InputMismatchException();
		}
		
		return s.substring(1, s.length() - 1); 
	}

	/**
	 * Returns true if this scanner has another token in its input. 
	 * This method may block while waiting for input to scan. 
	 * The scanner does not advance past any input.
	 * 
	 * @return true if another token is in the input.
	 */
	public boolean hasNext() {
		return hasNextQuotedString() || sc.hasNext();
	}
	
	/**
	 * Finds and returns the next complete token from this scanner. 
	 * A complete token is preceded and followed by input that matches the 
	 * delimiter pattern. This method may block while waiting for input to scan, 
	 * even if a previous invocation of hasNext returned <code>true</code>.
	 *  
	 * @return next token
	 */
	public String next() {
		if (hasNextQuotedString()) {
			return nextQuotedString();
		} else if (sc.hasNext()) {
			return sc.next();
		} else {
			throw new NoSuchElementException();
		}
	}
}
