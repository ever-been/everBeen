package cz.cuni.mff.d3s.been.persistence;

import java.io.Serializable;

/**
 * Result status of a {@link Query}
 *
 * @author darklight
 */
public enum QueryStatus implements Serializable {
	OK("Query successful", true),
	PERSISTENCE_DOWN("The persistence layer is currently down", false),
	INVALID_QUERY("Provided query was invalid", false),
	UNKNOWN("Unknown error", false),
	TRANSPORT_TIMED_OUT("Transporting the query to persistenc layer timed out", false),
	PROCESSING_TIMED_OUT("Query processing timed out", false);

	private final String desc;
	private final boolean ok;

	QueryStatus(String desc, boolean ok) {
		this.desc = desc;
		this.ok = ok;
	}

	public String getDescription() {
		return desc;
	}

	public boolean isOk() {
		return ok;
	}
}
