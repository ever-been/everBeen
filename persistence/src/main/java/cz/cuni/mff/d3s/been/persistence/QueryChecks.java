package cz.cuni.mff.d3s.been.persistence;

/**
 * Simple checks on queries
 *
 * @author darklight
 */
public final class QueryChecks {

	/**
	 * See whether a query is a fetch query
	 *
	 * @param query Query to analyze
	 *
	 * @return <code>true</code> if the query is a fetch query, <code>false</code> otherwise
	 */
	public static boolean isFetch(Query query) {
		return query instanceof FetchQuery;
	}

	/**
	 * See whether a query is a delete query
	 *
	 * @param query Query to analyze
	 *
	 * @return <code>true</code> if the query is a delete query, <code>false</code> otherwise
	 */
	public static boolean isDelete(Query query) {
		return query instanceof DeleteQuery;
	}

	/**
	 * Check whether the query targets results
	 *
	 * @param query Query to analyze
	 *
	 * @return <code>true</code> if the query targets results, <code>false</code> otherwise
	 */
	public static boolean isResult(Query query) {
		return "result".equals(query.getEntityID().getKind());
	}

	/**
	 * Assert that a query is a fetch query
	 *
	 * @param query Query to analyze
	 *
	 * @throws DAOException If the query is not a fetch query
	 */
	public static void assertIsFetch(Query query) throws DAOException {
		if (!isFetch(query)) throw new DAOException(String.format("Query %s is not a fetch query", query.toString()));
	}

	/**
	 * Assert that a query is a delete query
	 *
	 * @param query Query to analyze
	 *
	 * @throws DAOException If the query is not a delete query
	 */
	public static void assertIsDelete(Query query) throws DAOException {
		if (!isDelete(query)) throw new DAOException(String.format("Query %s is not a delete query", query.toString()));
	}

	/**
	 * Assert that a query targets results
	 *
	 * @param query Query to analyze
	 *
	 * @throws DAOException If the query doesn't target results
	 */
	public static void assertIsResult(Query query) throws DAOException {
		if (!isResult(query)) throw new DAOException(String.format("Query %s does not target results", query.toString()));
	}
}
