/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
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

package cz.cuni.mff.been.benchmarkmanagerng;

/**
 * Sorted (!!!) list of analysis state.
 * AnalysisTracker uses ordering to determine
 * state of analysis in case more than one context is active.
 * In that case field with smaller {@code ordinal()} has priority. 
 * 
 * @author Jiri Tauber
 */
public enum AnalysisState{
	/** analysis has just started - generator is still running */
	GENERATING( true ),

	/** generator planned all tasks and they are running now */
	RUNNING( true ),

	/** Analysis has finished */
	IDLE( false ),

	/** No data about analysis state available */
	UNKNOWN( false );

	private final boolean isRunning;
	
	private AnalysisState( boolean isRunning ){
		this.isRunning = isRunning;
	}
	
	public boolean isRunning(){ return isRunning; }
}