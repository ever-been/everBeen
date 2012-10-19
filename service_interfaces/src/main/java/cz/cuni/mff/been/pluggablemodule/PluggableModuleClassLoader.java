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

package cz.cuni.mff.been.pluggablemodule;

import java.net.URL;
import java.net.URLClassLoader;


/** 
 * Classloader used for loading pluggable module classes by
 * pluggable module manager.
 * 
 * @author Jan Tattermusch
 *
 */
public class PluggableModuleClassLoader extends URLClassLoader {
	
	/** 
	 * Creates new instance of class loader with empty classpath.
	 *
	 */
	public PluggableModuleClassLoader() {
		//Use the class loader we actually use (might be one-jar's loader)
		//not the system one

		//TODO check if this is what we actually want to do
		super(new URL[] {}, PluggableModuleClassLoader.class.getClassLoader());
	}

	/**
	 * Adds a new URL to this class loader's classpath.
	 */
	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}
}
