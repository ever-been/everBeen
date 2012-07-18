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

import java.util.Arrays;
import java.util.UUID;

/**
 * State-full planning facility for Hintserver generator.
 * It is initialized with numerous information and then generating continues
 * in steps of either requesting another build or another run to be planned.<br>
 * Currently no runs will be planned for builds so the order of requests doesn't
 * matter.
 * 
 * @author Jiri Tauber
 */
public class Planner {

	/** Build points available for planner - nothing will be planned when there is not enough */
	private int points;
	/** The number of planner points needed to plan a build */
	private int buildPrice;
	/** The number of planner points needed to plan a run */
	private int runPrice;
	/** Maximal number of runs for each build */
	private int runRatio;
	/** Maximal number of builds per version */
	private int maxBuilds;

	/** used to determine build number for the next run */
	private BinaryData[] binData;
	/** used to determine version number for the next build */
	private VersionData[] versionData;


	/**
	 * Construct and initialize new planner
	 * 
	 * @param points - number of planner points available
	 * @param buildPrice - number of planner points required to plan a new build
	 * @param runPrice - number of planner points required to plan a new run
	 * @param runRatio - maximal number of runs per build
	 * @param maxBuilds - maximal number of builds per version
	 * @param binData - information about already built binaries
	 * @param headVersion - the highest possible revision number
	 */
	public Planner(int points, int buildPrice, int runPrice, int runRatio,
			int maxBuilds, BinaryData[] binData, int headVersion ) {
		super();
		this.points = points;
		this.buildPrice = buildPrice;
		this.runPrice = runPrice;
		this.runRatio = runRatio;
		this.maxBuilds = maxBuilds;
		this.binData = binData;

		Arrays.sort(this.binData, new BinaryData.VersionComparator());
		VersionData[] vd = new VersionData[headVersion+1];
		for (int i = 0; i < vd.length; i++) {
			vd[i] = new VersionData(i, 0, 0);
		}
		vd[0].setBuilds(maxBuilds);
		vd[0].setRuns(maxBuilds*runRatio);
		int version;
		for (BinaryData build : binData) {
			version = build.getVersion();
			vd[version].setBuilds(vd[version].getBuilds()+1);
			vd[version].setRuns(vd[version].getRuns()+build.getRunCount());
		}
		this.versionData = vd;

		Arrays.sort(this.binData, new BinaryData.RunComparator());
		Arrays.sort(this.versionData, new VersionData.BuildComparator());
	}

	//------------------------------------------------------------------------//
	/**
	 * Determines which version should be built.
	 * 
	 * @return The version to build or null if there is nothing to be done
	 */
	public Integer planBuild() {
		if (points < buildPrice) {
			return null;
		}

		// Take the version with the least runs
		VersionData current = versionData[0];
		if (current.getBuilds() >= maxBuilds){
			return null;
		}
		current.setBuilds(current.getBuilds()+1);

		// Put the version in the correct place in the sorted list
		int i = 0;
		while (i < versionData.length && versionData[i].getBuilds() > current.getBuilds()) {
			i++;
		}
		versionData[0] = versionData[i];
		versionData[i] = current;

		if (current != null) {
			current.setBuilds(current.getBuilds()+1);
			// TODO: update build data - i.e. do we want to plan runs for this build?
			points -= buildPrice;
		}
		return current.getVersion();
	}

	/**
	 * Plans another run action.
	 * Determines which binary is the least tested one and if it needs to be ran again.
	 * 
	 * @return The identification number of build that should be ran or null
	 */
	public UUID planRun() {
		if (points < runPrice) {
			return null;
		}
		// Take the build with the least runs
		BinaryData current = binData[0];
		if (current.getRunCount() >= runRatio) {
			return null;
		}
		current.setRunCount(current.getRunCount()+1);

		// Put the chosen run in the sorted sequence
		int i = 0;
		while (i < binData.length && binData[i].getRunCount() > current.getRunCount()) {
			i++;
		}
		binData[0] = binData[i];
		binData[i] = current;

		points -= runPrice;
		return current.getBinaryId();
	}
}
