/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2010 Distributed Systems Research Group,
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
package cz.seznam.been.module.hintserver.generator;

import java.util.Comparator;

/**
 * Data class for Hintserver revision data. It holds information about number
 * of builds and runs for a particular revision.
 * 
 * @author Jiri Tauber
 */
public class VersionData {

	/**
	 * Comparator class for sorting VersionData by build count in ASC order
	 * @author Jiri Tauber
	 */
	public static class BuildComparator implements Comparator<VersionData> {
		@Override
		public int compare(VersionData o1, VersionData o2) {
			if (o1.builds < o2.builds) return -1;
			if (o1.builds > o2.builds) return 1;
			return 0;
		}
	}

	//------------------------------------------------------------------------//
	private int version;
	private int builds;
	private int runs;

	public VersionData(int version, int builds, int runs) {
		super();
		this.version = version;
		this.builds = builds;
		this.runs = runs;
	}

	public int getVersion() {
		return version;
	}

	public int getBuilds() {
		return builds;
	}

	public void setBuilds(int builds) {
		this.builds = builds;
	}

	public int getRuns() {
		return runs;
	}

	public void setRuns(int runs) {
		this.runs = runs;
	}

}
