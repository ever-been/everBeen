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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Stores a set of pluggable modules.
 * @author Jan Tattermusch
 */
public class PluggableModuleRegistry {

    /** map of pluggable modules */
    private Map<PluggableModuleDescriptor, PluggableModule> modules = new HashMap<PluggableModuleDescriptor, PluggableModule> ();

    /** 
     * Creates a new instance of <code>PluggableModuleRegistry</code>.
     */
    public PluggableModuleRegistry() {

    }

    /**
     * Registers a module descriptor
     * @param moduleDescriptor
     */
    public synchronized void registerModule(PluggableModuleDescriptor moduleDescriptor, PluggableModule module) {
        if (moduleDescriptor == null) {
            throw new RuntimeException("Tried to register a null module descriptor.");
        }
        if (modules.containsKey(moduleDescriptor)) {
            throw new RuntimeException("Module descriptor " + moduleDescriptor + " already registered.");
        }
        modules.put(moduleDescriptor, module);
    }

    /**
     * Removes module descriptor from registry
     * @param moduleDescriptor module descriptor
     */
    public synchronized void unregisterModule(PluggableModuleDescriptor moduleDescriptor) {
        if (moduleDescriptor == null) {
            throw new RuntimeException("Tried to unregister a null module descriptor.");
        }
        if (!modules.containsKey(moduleDescriptor)) {
            throw new RuntimeException("Failed to unregistered non-registered module " + moduleDescriptor + ".");
        }

        modules.remove(moduleDescriptor);
    }

    /**
     * Tests whether module is registered in this registry
     * @param moduleDescriptor module descriptor
     * @return true if module is registered
     */
    public synchronized boolean isRegistered(PluggableModuleDescriptor moduleDescriptor) {
        return modules.containsKey(moduleDescriptor);
    }

    /**
     * Retrieves a pluggable module from registry.
     * @param moduleDescriptor module's descriptor
     * @return pluggable module or null if no such module is registered.
     */
    public synchronized PluggableModule getModule(PluggableModuleDescriptor moduleDescriptor) {
        return modules.get(moduleDescriptor);
    }

    /**
     * @return iterator for all registered module descriptors
     */
    public Iterator<PluggableModuleDescriptor> iterator() {
        return modules.keySet().iterator();
    }

}
