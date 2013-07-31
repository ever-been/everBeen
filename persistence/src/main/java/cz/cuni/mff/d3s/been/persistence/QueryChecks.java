package cz.cuni.mff.d3s.been.persistence;

/**
 * Simple checks on queries
 *
 * @author darklight
 */
public final class QueryChecks {
	public static boolean isFetch(Query query) {
		return query instanceof FetchQuery;
	}

	public static boolean isDelete(Query query) {
		return query instanceof DeleteQuery;
	}

	public static boolean isResult(Query query) {
		return "result".equals(query.getEntityID().getKind());
	}

	public static boolean isNative(Query query) {
		return query instanceof NativeQuery;
	}

	public static void assertIsFetch(Query query) throws DAOException {
		if (!isFetch(query)) throw new DAOException(String.format("Query %s is not a fetch query", query.toString()));
	}

	public static void assertIsDelete(Query query) throws DAOException {
		if (!isDelete(query)) throw new DAOException(String.format("Query %s is not a delete query", query.toString()));
	}

	public static void assertIsResult(Query query) throws DAOException {
		if (!isResult(query)) throw new DAOException(String.format("Query %s does not target results", query.toString()));
	}

	public static void assertIsNative(Query query) throws DAOException {
		if (!isNative(query)) throw new DAOException(String.format("Query %s is not native", query.toString()));
	}
}
