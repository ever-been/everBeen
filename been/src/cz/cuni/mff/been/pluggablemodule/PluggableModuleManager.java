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

/**
 * Common interface for pluggable module managers.
 * 
 * @author Jan Tattermusch
 *
 */
public interface PluggableModuleManager {

	/**
	 * Gets an instance of pluggable module identified by descriptor.
	 * Method first lookups registry and if does not find 
	 * appropriate module loaded, it calls <code>loadModule</code>
	 * to resolve the new module.
	 * 
	 * @param moduleDescriptor
	 * @return instance of requested pluggable module
	 */
	public abstract PluggableModule getModule(
			PluggableModuleDescriptor moduleDescriptor)
			throws PluggableModuleException;

	/**
	 * Gets pluggable module package from software repository (or package cache),
	 * adds its jars to system classpath and instatiates an appropriate
	 * class that extends pluggable module. Before module is loaded,
	 * all its prerequisite modules are also loaded.
	 * @param moduleDescriptor descriptor of pluggable module to load
	 * @return instance of requested pluggable module
	 * @throws PluggableModuleException
	 */
	public abstract PluggableModule loadModule(
			PluggableModuleDescriptor moduleDescriptor)
			throws PluggableModuleException;

	/**
	 * Returns whether given pluggable module is loaded or not.
	 * @param moduleDescriptor module's descriptor
	 * @return true if module is loaded.
	 */
	public abstract boolean isModuleLoaded(
			PluggableModuleDescriptor moduleDescriptor);

	/** 
	 * Returns classloader associated with pluggable module manager.
	 * @return class loader
	 */

	public abstract ClassLoader getClassLoader();

}
