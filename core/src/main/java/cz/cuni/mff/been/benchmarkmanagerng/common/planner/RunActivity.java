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

import java.util.Set;

/**
 * Abstract representation for Run workflow in benchmark.
 * Object of this class is intended to be generated by DBRPlanner
 * and stores sufficient information for generator to be 
 * able to generate tasks that will fulfill activity described by 
 * this object.
 * 
 * Run activity is the lowest one in download-build-run hierarchy
 * and doesn't contain any dependent subactivities.
 *   
 * @author Jan Tattermusch
 *
 */
public class RunActivity {

	/**
	 * Constructs new instance of run activity 
	 * based on software version,
	 * build number and run number.
	 * 
	 * Suites are logical subunits of one run.
	 * They can be used if it makes sense considering 
	 * the nature of specific benchmark. If not,
	 * only one "DEFAULT" suite should be used.
	 * 
	 * @param version	software version to be run
	 * @param buildNumber build number to use for run
	 * @param runNumber run number
	 * @param suites suite set to run 
	 */
	public RunActivity(String version, int buildNumber, int runNumber, Set<String> suites) {
		super();
		this.version = version;
		this.buildNumber = buildNumber;
		this.runNumber = runNumber;
		this.suites = suites;
	}

	private String version;
	private int buildNumber;
	private int runNumber;
	private Set<String> suites;

	
	/**
	 * 
	 * @return software version to run
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * 
	 * @return build number to run
	 */
	public int getBuildNumber() {
		return buildNumber;
	}

	/**
	 * 
	 * @return run number to assign to the run
	 */
	public int getRunNumber() {
		return runNumber;
	}
	
	/**
	 * 
	 * @return set of suites that should be run
	 */
	public Set<String> getSuites() {
		return suites;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + buildNumber;
		result = prime * result + runNumber;
		result = prime * result + ((suites == null) ? 0 : suites.hashCode());
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
		RunActivity other = (RunActivity) obj;
		if (buildNumber != other.buildNumber)
			return false;
		if (runNumber != other.runNumber)
			return false;
		if (suites == null) {
			if (other.suites != null)
				return false;
		} else if (!suites.equals(other.suites))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "runActivity(" + version + ", " + buildNumber + "," + runNumber + "," + suites +")"; 
	}
}