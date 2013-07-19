package cz.cuni.mff.d3s.been.persistence;

import java.io.Serializable;

/**
 * Result status of a {@link Query}
 *
 * @author darklight
 */
public enum QueryStatus implements Serializable {
	OK("Query successful"),
	PERSISTENCE_DOWN("The persistence layer is currently down"),
	INVALID_QUERY("Provided query was invalid"),
	UNKNOWN("Unknown error");

	private final String desc;

	QueryStatus(String desc) {
		this.desc = desc;
	}

	public String getDescription() {
		return desc;
	}
}
