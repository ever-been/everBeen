package cz.cuni.mff.d3s.been.persistence;

/**
 * Result status of a {@link Query}
 *
 * @author darklight
 */
public enum QueryStatus {
	OK,
	PERSISTENCE_DOWN,
	INVALID_QUERY,
	UNKNOWN
}
