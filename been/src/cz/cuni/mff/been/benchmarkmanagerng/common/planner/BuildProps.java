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
package cz.cuni.mff.been.benchmarkmanagerng.common.planner;


/**
 * Download-Build-Run Planner accepts 
 * list of already performed builds as 
 * a multimap with key BuildKey and value BuildProps. 
 * 
 * @author Jan Tattermusch
 *
 */
public class BuildProps {

	/**
	 * Constructs a new instance of build properties.
	 * @param buildNumber build number
	 */
	public BuildProps( int buildNumber) {
		super();
		this.buildNumber = buildNumber;
	}

	/**
	 * build number property 
	 */
	private int buildNumber;

	/**
	 * 
	 * @return build number
	 */
	public int getBuildNumber() {
		return buildNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + buildNumber;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BuildProps other = (BuildProps) obj;
		if (buildNumber != other.buildNumber)
			return false;
		return true;
	}

	
}