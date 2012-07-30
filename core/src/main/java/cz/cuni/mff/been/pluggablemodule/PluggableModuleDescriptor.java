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

import cz.cuni.mff.been.jaxb.pmc.Dependency;

/**
 * Descriptor of pluggable module.
 *
 * Class is immutable.
 *
 * @author Jan Tattermusch
 */
public class PluggableModuleDescriptor {
	
    /** name of pluggable module */
    private final String name;

    /** version of pluggable module */
    private final String version;

    /**
     * Creates an instance of pluggable module descriptor from a Dependency entry.
     * 
     * @param dependency The dependency entry obtained from JAXB.
     */
    public PluggableModuleDescriptor(Dependency dependency) {
    	this(dependency.getModuleName(), dependency.getModuleVersion());
    }
    
    /** Creates instance of pluggable module descriptor
     *
     * @param name name of pluggable module
     * @param version version of pluggable module
     */
    public PluggableModuleDescriptor(String name, String version) {
        this.name = name;
        this.version = version;
    }

    /**
     * @return the name of pluggable module
     */
    public String getName() {
        return name;
    }

    /**
     * @return the version of pluggable module
     */
    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PluggableModuleDescriptor other = (PluggableModuleDescriptor) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 89 * hash + (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[" + this.name + "," + this.version +"]";
    }

}
