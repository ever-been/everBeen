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
 * list of already performed runs as 
 * a multimap with key RunKey and value RunProps. 
 * 
 * @author Jan Tattermusch
 *
 */
public class RunKey implements Comparable<Object> {
	
	/**
	 * Creates new instance of run key 
	 * @param version software version
	 * @param buildNumber build number
	 */
	public RunKey(String version, int buildNumber) {
		super();
		this.version = version;
		this.buildNumber = buildNumber;
	}

	/**
	 * Software version property
	 */
	private String version;
	
	/**
	 * Build number property
	 */
	private int buildNumber;

	/**
	 * 
	 * @return software version
	 */
	public String getVersion() {
		return version;
	}

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
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		RunKey other = (RunKey) obj;
		if (buildNumber != other.buildNumber)
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public int compareTo(Object o) {
		
		RunKey that = (RunKey) o;
		int result = this.version.compareTo(that.version);
		
		if (result == 0) {
			result = Integer.valueOf(this.buildNumber).compareTo(that.buildNumber);
		}
		
		return result;
	}

	@Override
	public String toString() {
		return "RunKey(" + version + ", " + buildNumber + ")";
	}
	
}