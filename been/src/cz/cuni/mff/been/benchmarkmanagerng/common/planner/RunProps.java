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
public class RunProps {
	
	/**
	 * Creates new instance of run proprs 
	 * @param runNumber
	 * @param suiteName
	 */
	public RunProps( int runNumber, String suiteName) {
		super();
		this.runNumber = runNumber;
		this.suiteName = suiteName;
	}

	/**
	 * run number property 
	 */
	private int runNumber;
	
	/**
	 * suite name property
	 */
	private String suiteName;
	

	/**
	 *  
	 * @return run number
	 */
	public int getRunNumber() {
		return runNumber;
	}

	/**
	 * 
	 * @return suite name
	 */
	public String getSuiteName() {
		return suiteName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + runNumber;
		result = prime * result
				+ ((suiteName == null) ? 0 : suiteName.hashCode());
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
		RunProps other = (RunProps) obj;
		if (runNumber != other.runNumber)
			return false;
		if (suiteName == null) {
			if (other.suiteName != null)
				return false;
		} else if (!suiteName.equals(other.suiteName))
			return false;
		return true;
	}
}