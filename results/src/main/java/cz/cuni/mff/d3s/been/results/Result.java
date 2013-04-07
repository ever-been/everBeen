package cz.cuni.mff.d3s.been.results;


/**
 * A skeletal benchmark result. Extend this class with benchmarking results.
 * 
 * @author darklight
 * 
 */
public abstract class Result {

	/**
	 * @return The name of the collection this result should be stored in. May be
	 *         a NoSQL collection name or an SQL database name, for example.
	 *         Defaults to "results".
	 */
	public String getCollectionName() {
		return "results";
	}

	/**
	 * @return The name of this entity. May be for example an object type name or
	 *         the name of the SQL table in which this result should be stored.
	 *         Defaults to simple classname.
	 */
	public String getEntityName() {
		return getClass().getSimpleName();
	}

}
