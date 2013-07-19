package cz.cuni.mff.d3s.been.cluster.context;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.Query;
import cz.cuni.mff.d3s.been.persistence.QueryAnswer;
import cz.cuni.mff.d3s.been.persistence.QuerySerializer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Cluster-based operations related to object persistence and retrieval
 *
 * @author darklight
 */
public class Persistence {

	private final ClusterContext ctx;
	private final IQueue<Query> queryQueue;
	private final IMap<String, QueryAnswer> queryAnswerMap;
	private final QuerySerializer querySerializer;

	Persistence(ClusterContext ctx) {
		this.ctx = ctx;
		this.queryAnswerMap = ctx.getMap(Names.PERSISTENCE_QUERY_ANSWERS_MAP_NAME);
		this.queryQueue = ctx.getQueue(Names.PERSISTENCE_QUERY_QUEUE_NAME);
		this.querySerializer = new QuerySerializer();
	}

	/**
	 * Query the persistence layer for a set of matching results. Wait for the results to be available.
	 *
	 * @param query Query to execute
	 *
	 * @return A {@link QueryAnswer} object representing the {@link Query}'s outcome
	 *
	 * @throws DAOException When the wait for the query outcome is interrupted or when provided query fails to serialize
	 */
	public final QueryAnswer query(Query query) throws DAOException {
		try {
			final BlockingQueue<String> answerReadyNotifier = new LinkedBlockingQueue<String>();
			queryAnswerMap.addEntryListener(new MapEntryReadyHook(answerReadyNotifier), query.getId(), false);
			queryQueue.add(query);
			final String queryId = answerReadyNotifier.take();
			return queryAnswerMap.remove(queryId);
		} catch (InterruptedException e) {
			throw new DAOException(String.format("Interrupted when waiting for query result. Query was %s", query.toString()));
		}
	}

	private class MapEntryReadyHook implements EntryListener<String, QueryAnswer> {
		/**
		 * Queue that notifies the waiting thread that its value is in the map
		 */
		private final BlockingQueue<String> notifier;

		MapEntryReadyHook(BlockingQueue<String> notifier) {
			this.notifier = notifier;
		}

		@Override
		public void entryAdded(EntryEvent<String, QueryAnswer> event) {
			notifier.add(event.getKey());
		}

		@Override
		public void entryUpdated(EntryEvent<String, QueryAnswer> event) {
		}

		@Override
		public void entryEvicted(EntryEvent<String, QueryAnswer> event) {
		}

		@Override
		public void entryRemoved(EntryEvent<String, QueryAnswer> event) {
		}
	}
}