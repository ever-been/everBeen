package cz.cuni.mff.d3s.been.persistence;

import java.util.Collection;

/**
 * Factory for {@link QueryAnswer} objects
 *
 * @author darklight
 */
public final class QueryAnswerFactory {

	/**
	 * Create a query answer with resulting data
	 *
	 * @param data Data to send back
	 *
	 * @return The answer
	 */
	public static final QueryAnswer fetched(Collection<String> data) {
		return new DataQueryAnswer(QueryStatus.OK, data);
	}

	/**
	 * Create an answer saying that items were successfully deleted
	 *
	 * @return The answer
	 */
	public static final QueryAnswer deleted() {
		return new SimpleQueryAnswer(QueryStatus.OK);
	}

	/**
	 * Create an answer saying the persistence layer is down and that the query cannot be executed at the moment
	 *
	 * @return The answer
	 */
	public static final QueryAnswer persistenceDown() {
		return new SimpleQueryAnswer(QueryStatus.PERSISTENCE_DOWN);
	}

	/**
	 * Create an answer saying something went terribly wrong, but we don't know what it was
	 *
	 * @return The answer
	 */
	public static final QueryAnswer unknownError() {
		return new SimpleQueryAnswer(QueryStatus.UNKNOWN);
	}

	/**
	 * Create an answer saying the received query was somehow invalid or corrupted.
	 *
	 * @return The answer
	 */
	public static final QueryAnswer badQuery() {
		return new SimpleQueryAnswer(QueryStatus.INVALID_QUERY);
	}

	/**
	 * Create an answer saying the query timed out before it had a chance to be evaluated.
	 *
	 * @return The answer
	 */
	public static final QueryAnswer transportTimedOut() {
		return new SimpleQueryAnswer(QueryStatus.TRANSPORT_TIMED_OUT);
	}

	/**
	 * Create an answer saying the the query started to get evaluated, but didn't get back in time
	 *
	 * @return The answer
	 */
	public static final QueryAnswer processingTimedOut() {
		return new SimpleQueryAnswer(QueryStatus.PROCESSING_TIMED_OUT);
	}
}
