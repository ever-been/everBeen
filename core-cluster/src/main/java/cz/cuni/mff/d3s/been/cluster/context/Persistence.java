package cz.cuni.mff.d3s.been.cluster.context;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.core.persistence.Query;

import java.util.Collection;
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
	private final IMap<String, Collection<String>> queryAnswerMap;

	Persistence(ClusterContext ctx) {
		this.ctx = ctx;
		this.queryAnswerMap = ctx.getMap(Names.PERSISTENCE_QUERY_ANSWERS_MAP_NAME);
		this.queryQueue = ctx.getQueue(Names.PERSISTENCE_QUERY_QUEUE_NAME);
	}

	/**
	 * Query the persistence layer for a set of matching results. Wait for the results to be available.
	 *
	 * @param query Query to execute
	 *
	 * @return A collection of matching results (currently present in the persistence layer)
	 *
	 * @throws InterruptedException When the wait for the result collection is interrupted
	 */
	public final Collection<String> query(Query query) throws InterruptedException {
		final BlockingQueue<String> answerReadyNotifier = new LinkedBlockingQueue<String>();
		queryAnswerMap.addEntryListener(new MapEntryReadyHook(answerReadyNotifier), query.getId(), false);
		queryQueue.add(query);
		final String queryId = answerReadyNotifier.take();
		return queryAnswerMap.remove(queryId);
	}

	private class MapEntryReadyHook implements EntryListener<String, Collection<String>> {
		/**
		 * Queue that notifies the waiting thread that its value is in the map
		 */
		private final BlockingQueue<String> notifier;

		MapEntryReadyHook(BlockingQueue<String> notifier) {
			this.notifier = notifier;
		}

		@Override
		public void entryAdded(EntryEvent<String, Collection<String>> event) {
			notifier.add(event.getKey());
		}

		@Override
		public void entryUpdated(EntryEvent<String, Collection<String>> event) {
		}

		@Override
		public void entryEvicted(EntryEvent<String, Collection<String>> event) {
		}

		@Override
		public void entryRemoved(EntryEvent<String, Collection<String>> event) {
		}
	}
}
