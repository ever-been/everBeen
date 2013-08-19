package cz.cuni.mff.d3s.been.persistence.task;

import cz.cuni.mff.d3s.been.core.persistence.Entity;
import cz.cuni.mff.d3s.been.core.task.TaskContextState;

/**
 * A persistent wrapper for the final state of a context
 * 
 * @author darklight
 */
public class PersistentContextState extends Entity {

	private TaskContextState contextState;
	private String contextId;
	private String benchmarkId;

	public PersistentContextState() {
		created = System.currentTimeMillis();
	}

	public TaskContextState getContextState() {
		return contextState;
	}

	public void setContextState(TaskContextState contextState) {
		this.contextState = contextState;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getBenchmarkId() {
		return benchmarkId;
	}

	public void setBenchmarkId(String benchmarkId) {
		this.benchmarkId = benchmarkId;
	}
}
