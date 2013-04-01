package cz.cuni.mff.d3s.been.results;

/**
 * A generic identifier of the location where a persistent result should be
 * stored.
 * 
 * @author darklight
 * 
 */
public interface ResultContainerId {
	/**
	 * @return The name of the database where the result should be stored.
	 */
	String getDatabaseName();

	/**
	 * @return The name of the container where the result should be stored, e.g. a
	 *         NoSQL collection name or an SQL namespace
	 */
	String getContainerName();

	/**
	 * @return The name of the result's entity, e.g. a NoSQL document name or an
	 *         SQL table name.
	 */
	String getEntityName();
}
