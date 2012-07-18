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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Jiri Tauber
 *
 */
class AnalysesTracker {

	/**
	 * Data for one context within analysis metadata
	 */
	private class ContextMetadata {
		/** state of the current context */
		private AnalysisState state = AnalysisState.UNKNOWN;
		/** generator registered in this context */
		private String generatorId = null;
		
		public ContextMetadata(AnalysisState state, String generatorId){
			this.state = state;
			this.generatorId = generatorId;
		}

		public AnalysisState getState() { return state; }
		public void setState(AnalysisState state) { this.state = state; }
		public String getGeneratorId() { return generatorId; }
		//public void setGeneratorId(String generatorId) { this.generatorId = generatorId; }
	}


	/**
	 * Metadata for one analysis.
	 * Contains information about all contexts registered in analysis.
	 */
	private class AnalysisMetadata {
		/** state of analysis determined as {@code min(context.sate.ordinal())}
		 * for all contexts registered in this analysis */
		private AnalysisState state = AnalysisState.UNKNOWN;
		/** Metadata for all contexts registered in this analysis */
		private final HashMap<String, ContextMetadata> contextMetadata = new HashMap<String, ContextMetadata>();

		//public AnalysisState getState() { return state; }
		public ContextMetadata getContextMetadata(String contextId){ return contextMetadata.get(contextId); }

		/**
		 * Smart function that sets context metadata and adjusts analysis state if necessary.
		 * It removes contexts that are iddle.
		 *   
		 * @param contextId
		 * @param metadata
		 */
		public void setContextMetadata(String contextId, ContextMetadata metadata){
			AnalysisState newState = metadata.getState();
			if( newState.equals(AnalysisState.IDLE) ){
				contextMetadata.remove(contextId);
			} else {
				contextMetadata.put(contextId, metadata);
			}
			// determine the lowest context state
			for (ContextMetadata cmd : contextMetadata.values()) {
				if( newState.equals(AnalysisState.GENERATING) ){
					break; // no need to check the rest
				}
				if( cmd.getState().ordinal() < newState.ordinal() ){
					newState = cmd.getState();
				}
			}
			state = newState;
		}
	}

	/** Metadata for all analyses indexed by analysis safeName */
	private HashMap<String, AnalysisMetadata> tracker = new HashMap<String, AnalysisMetadata>();

	//-----------------------------------------------------------------------//
	/**
	 * Create new context metadata for analysis, set it as GENERATING and associate specified generatorId. 
	 * @param contextId
	 * @param generatorId
	 */
	public void analysisStarted(String contextId, String generatorId ) {
		String analysisId = contextToSafeName(contextId);
		AnalysisMetadata metadata = tracker.get(analysisId);
		if( metadata == null ){
			// no metadata for this analysis yet
			metadata = new AnalysisMetadata();
		}
		metadata.setContextMetadata(contextId, new ContextMetadata(AnalysisState.GENERATING, generatorId));
		tracker.put(analysisId, metadata);
	}


	/**
	 * Find (or create) context metadata for analysis and set it as RUNNING.
	 * @param contextId
	 * @param generatorId
	 * @throws BenchmarkManagerException if different generator is registered for context
	 */
	public void generatorFinished(String contextId, String generatorId) throws BenchmarkManagerException{
		ContextMetadata cmd = findContextMetadata(contextId);
		if( cmd == null ){
			cmd = new ContextMetadata(AnalysisState.RUNNING, generatorId);
		} else if( !generatorId.equals(cmd.generatorId) ){
			throw new BenchmarkManagerException("Different generator registered in context "+contextId);
		}
		cmd.setState(AnalysisState.RUNNING);

		AnalysisMetadata amd = tracker.get(contextToSafeName(contextId));
		amd.setContextMetadata(contextId, cmd);
	}


	/**
	 * Find (or create) context metadata for analysis and set it as IDDLE.
	 * @param contextId
	 */
	public void analysisFinished(String contextId){
		ContextMetadata cmd = findContextMetadata(contextId);
		if( cmd == null ){
			cmd = new ContextMetadata(AnalysisState.IDLE, null);
		} else {
			cmd.setState(AnalysisState.IDLE);
		}

		AnalysisMetadata amd = tracker.get(contextToSafeName(contextId));
		amd.setContextMetadata(contextId, cmd);
	}

	//-----------------------------------------------------------------------//
	/**
	 * Returns state of analysis. That is determined as the minimal
	 * ordinal value state picked from all the registered contexts.
	 * @param safeName
	 * @return analysis state
	 */
	public AnalysisState getAnalysisState(String safeName){
		AnalysisMetadata metadata = tracker.get(safeName);
		if( metadata == null ){
			return AnalysisState.UNKNOWN;
		} else {
			return metadata.state;
		}
	}

	/**
	 * @param contextId
	 * @return generator id registered for context or null
	 */
	public String getGeneratorId(String contextId){
		ContextMetadata cmd = findContextMetadata(contextId);
		if( cmd == null ){
			return null;
		} else {
			return cmd.getGeneratorId();
		}
	}

	/**
	 * @param analysisName the analysis we want contexts for
	 * @return list of active contexts and their state (may be empty but not null)
	 */
	public Map<String, AnalysisState> getActiveContexts(String analysisName){
		HashMap<String, AnalysisState> result = new HashMap<String, AnalysisState>();
		AnalysisMetadata analysis = tracker.get(analysisName);
		if( analysis == null ){
			return result; // empty map
		}
		for(Entry<String, ContextMetadata> entry : analysis.contextMetadata.entrySet()){
			result.put( entry.getKey(), entry.getValue().getState() );
		}
		return result;
	}

	//-----------------------------------------------------------------------//
	/**
	 * Gets analysis safeName from provided context.
	 * @param contextId
	 * @return analysis safeName
	 */
	private String contextToSafeName(String contextId) {
		return contextId.substring(0,contextId.indexOf('-'));
	}

	/**
	 * Finds context metadata. This function creates analysis metadata if necessary.
	 * @param contextId
	 * @return context metadata or null - either way it creates analysis metadata
	 */
	private ContextMetadata findContextMetadata(String contextId){
		AnalysisMetadata amd = tracker.get(contextToSafeName(contextId));
		if( amd == null ){
			// no metadata for running analysis - create it
			amd = new AnalysisMetadata();
			tracker.put(contextToSafeName(contextId), amd);
		}
		ContextMetadata cmd = amd.getContextMetadata(contextId);
		return cmd;
	}
}
