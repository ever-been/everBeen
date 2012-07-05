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
package cz.cuni.mff.been.softwarerepository;

import java.io.Serializable;

/**
     * Callback interface to the Software Repository, which checks if the 
     * pluggable module provides given interface.
     *
     * @author Jan Tattermusch
     */
    public class ProvidedInterfaceQueryCallback
            implements PackageQueryCallbackInterface, Serializable {

	    private static final long serialVersionUID = -5621054370095541670L;
		/** Interface name to check. */
        private String interfaceName;

        /**
         * Allocates a new <code>ProvidedInterfaceQueryCallback</code> object.
         *
         * @param interfaceName fully classified name of interface name to be searched for
         */
        public ProvidedInterfaceQueryCallback(String interfaceName) {
            this.interfaceName = interfaceName;
        }

        /**
         * @see cz.cuni.mff.been.softwarerepository.PackageQueryCallbackInterface#match(cz.cuni.mff.been.softwarerepository.PackageMetadata)
         */
        public boolean match(PackageMetadata metadata) throws MatchException {
/*
        	if (!metadata.getType().equals(PackageType.MODULE)) {
        		return false;
        	}
 */
        	for(Object o : metadata.getProvidedInterfaces()) {
        		if (o.equals(this.interfaceName)) {
        			return true;
        		}
        	}
        	return false;
        }
    }