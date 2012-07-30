/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Michal Tomcanyi
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

import java.net.URL;
import java.net.URLClassLoader;

/**
 * The greedy classloader (contradictory from other classloaders) loads classes 
 * from specified URLs at first, and only if unsuccessful delegates the request to
 * parent classloader
 * @author mtomcanyi
 *
 */
public class GreedyURLClassLoader extends URLClassLoader {

	/**
	 * Creates class loader loading only from given list of URLs
	 * @param urls - list of URLs to load classes from
	 * @see URLClassLoader#URLClassLoader(java.net.URL[])
	 */
	public GreedyURLClassLoader(URL[] urls) {
		super(urls);
	}

	/**
	 * Creates class loader loading from given list of URLs. If unsuccessful, delegates
	 * the request to <code>parentClassLoader</code>
	 * @param urls - list of urls to load classes from
	 * @param parentClassLoader - class loader to which request are delegated
	 * @see URLClassLoader#URLClassLoader(java.net.URL[], java.lang.ClassLoader)
	 */
	public GreedyURLClassLoader(URL[] urls, ClassLoader parentClassLoader) {
		super(urls, parentClassLoader);
	}
	
	@Override
	public Class< ? > loadClass(String name) throws ClassNotFoundException {
		// try to load the class by ourselves
		Class< ? > c;
		try {
			c = findClass(name);
		} catch (ClassNotFoundException cnfe) {
			c = getParent().loadClass(name);
		} 
		
		return c;
	}
	
}
