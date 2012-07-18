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

import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.MultiMap;

/**
 * Planner interface for generic benchmark Download-Build-Run workflow. 
 * 
 * @author Jan Tattermusch
 *
 */

public interface DBRPlanner {

	/**
	 * 
	 * @return list of download activities that have been generated during the last plan()
	 * invocation
	 */
	public abstract List<DownloadActivity> getDownloadActivities();

	/**
	 * 
	 * @return list of build activities that have been generated during the last plan()
	 * invocation
	 */
	public abstract List<BuildActivity> getBuildActivities();

	/**
	 * 
	 * @return list of run activities that have been generated during the last plan()
	 * invocation
	 */
	public abstract List<RunActivity> getRunActivities();

	/**
	 * Computes a plan and stores its activities to this objects' properties.
	 * 
	 * Plan is computed based on set of source code versions available in external system (version control),
	 * set of software source code versions already obtained from external source,
	 * multimap of builds that were already performed and multimap of runs that were alredy performed. 
	 * 
	 * Every implementation of DBRPlanner defines its own rules which activities will be 
	 * generated based on input data.
	 * 
	 *  @param externalSources set of sources available in external source (version control)
	 * @param sources set of downloaded sources
	 * @param buildLog set of already planned builds
	 * @param availableBinaries set of available binaries
	 * @param runLog set of already planned runs
	 */
	public abstract void plan(Set<SourceKey> externalSources, Set<SourceKey> sources,
			MultiMap<BuildKey, BuildProps> buildLog, MultiMap<BuildKey, BuildProps> availableBinaries,
			MultiMap<RunKey, RunProps> runLog);

}