package cz.cuni.mff.d3s.been.persistence;

import java.io.Serializable;

/**
 * Result status of a {@link Query}
 *
 * @author darklight
 */
public enum QueryStatus implements Serializable {
	/** The query was executed successfully */
	OK("Query successful", true),
	/** The query could not be executed, because the <em>ObjectRepository</em> cannot reach its underlying database */
	PERSISTENCE_DOWN("The persistence layer is currently down", false),
	/** The query could not be executed, because it requested unsupported features */
	UNSUPPORTED_QUERY("Provided query not recognized", false),
	/** The query failed for unknown reasons, check <em>ObjectRepository</em> logs for more information */
	UNKNOWN("Unknown error", false),
	/** The query was not received by an <em>ObjectRepository</em> instance in time. <em>The Object Repository</em> is either busy or disconnected */
	TRANSPORT_TIMED_OUT("Transporting the query to persistence layer timed out", false),
	/** The query was received by an <em>ObjectRepository</em>, but an answer was not returned within the time limit specified in configuration */
	PROCESSING_TIMED_OUT("Query processing timed out", false),
	/** Query execution failed in <em>ObjectRepository</em>'s underlying database */
	QUERY_EXECUTION_FAILED("Query failed to execute", false);

	private final String desc;
	private final boolean ok;

	QueryStatus(String desc, boolean ok) {
		this.desc = desc;
		this.ok = ok;
	}

	/**
	 * Get a human readable description of the query status
	 *
	 * @return The description
	 */
	public String getDescription() {
		return desc;
	}

	/**
	 * Boolean check whether the query was executed without problems
	 *
	 * @return <code>true</code> if the query was OK, <code>false</code> otherwise
	 */
	public boolean isOk() {
		return ok;
	}
}
