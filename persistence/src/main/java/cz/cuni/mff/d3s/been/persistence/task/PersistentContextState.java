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

	/**
	 * Create a persistent variant of the <em>task context</em> state
	 */
	public PersistentContextState() {
		created = System.currentTimeMillis();
	}

	/**
	 * Get the <em>task context</em> state
	 *
	 * @return The state
	 */
	public TaskContextState getContextState() {
		return contextState;
	}

	/**
	 * Set the <em>task context</em> state
	 *
	 * @param contextState State to set
	 */
	public void setContextState(TaskContextState contextState) {
		this.contextState = contextState;
	}

	/**
	 * Get the <em>task context</em> ID
	 *
	 * @return The context ID
	 */
	public String getContextId() {
		return contextId;
	}

	/**
	 * Set the <em>task context</em> ID
	 *
	 * @param contextId ID to set
	 */
	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	/**
	 * Get the ID of the <em>benchmark</em> within which this <em>context</em> was run
	 *
	 * @return The <em>benchmark</em> ID
	 */
	public String getBenchmarkId() {
		return benchmarkId;
	}

	/**
	 * Set the ID of the <em>benchmark</em> within which this <em>context</em> was run
	 *
	 * @param benchmarkId The <em>benchmark</em> ID to set
	 */
	public void setBenchmarkId(String benchmarkId) {
		this.benchmarkId = benchmarkId;
	}
}
