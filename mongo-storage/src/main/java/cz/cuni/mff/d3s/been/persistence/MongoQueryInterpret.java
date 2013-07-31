package cz.cuni.mff.d3s.been.persistence;

/**
 * A native query interpretation helper
 *
 * @author darklight
 */
public final class MongoQueryInterpret {
	private MongoQueryInterpret() {}

	/**
	 * Unpack the query string from a native query
	 *
	 * @param query Query to unpack
	 *
	 * @return The query string
	 *
	 * @throws DAOException If the provided query is not native
	 */
	public static String getMongoQueryString(Query query) throws DAOException {
		QueryChecks.assertIsNative(query);
		return ((NativeQuery) query).getQueryString();
	}
}
