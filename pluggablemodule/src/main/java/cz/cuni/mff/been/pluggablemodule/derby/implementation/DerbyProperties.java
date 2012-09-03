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

package cz.cuni.mff.been.pluggablemodule.derby.implementation;

/**
 * Representation of Derby pluggable module's properties.
 *
 * Class is immutable.
 *
 * @author Jan Tattermusch
 */
public class DerbyProperties {

    /** Derby home path property */
    private String home;

    /** Whether Derby should be accessible over network */
    private boolean networkAccessible;
    
    /** On which port derby will be accessible over network */
    private int networkPort;

    /**
     * Creates new instance of <code>DerbyProperties</code>.
     * @param home Derby's home path
     * @param networkAccessible whether Derby should be accessible over network.
     */
    public DerbyProperties(String home, boolean networkAccessible) {
        this.home = home;
        this.networkAccessible = networkAccessible;
        this.networkPort = 1527;
    }
    
    /**
     * Creates new instance of <code>DerbyProperties</code>.
     * @param home Derby's home path
     * @param networkAccessible whether Derby should be accessible over network.
     */
    public DerbyProperties(String home, boolean networkAccessible, int networkPort) {
        this.home = home;
        this.networkAccessible = networkAccessible;
        this.networkPort = networkPort;
    }

    /**
     * Derby home path.
     * @return Derby's home path.
     */
    public String getHome() {
        return home;
    }

    /**
     * Whether Derby should be accessible over network.
     * @return true if derby will be accessible over network.
     */
    public boolean isNetworkAccessible() {
        return networkAccessible;
    }

	public int getNetworkPort() {
		return networkPort;
	}

}
