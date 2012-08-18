/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jan Tattermusch
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

/**
 * Utility methods regarding debugging. 
 *
 */
public class Debug {
	
	/** name of environment variable indicating 
	 * that debugging mode is on
	 */
	private static final String ENV_BEEN_DEBUG = "BEEN_DEBUG";
	
	/**
	 * 
	 * @return true if BEEN runs in debugging mode
	 */
	public static boolean isDebugModeOn() {
		String beenDebug = System.getenv(ENV_BEEN_DEBUG);
		if (beenDebug == null) return false;
		return (beenDebug.length() > 0); 
	}
	
}
