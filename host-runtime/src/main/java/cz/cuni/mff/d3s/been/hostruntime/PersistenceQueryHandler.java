package cz.cuni.mff.d3s.been.hostruntime;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.persistence.Query;
import cz.cuni.mff.d3s.been.socketworks.SocketHandlerException;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReadReplyHandler;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Handler for queries into persistence layer.
 *
 * @author darklight
 */
public class PersistenceQueryHandler implements ReadReplyHandler {

	private static final Logger log = LoggerFactory.getLogger(PersistenceQueryHandler.class);

	private final HandlerRecycler recycler;
	private final IMap<String, Collection<String>> answerMap;
	private final IQueue<Query> queryQueue;
	private final BlockingQueue<String> answerReadyNotifier;
	private final ObjectReader queryReader;

	PersistenceQueryHandler(ClusterContext ctx, ObjectMapper om, HandlerRecycler recycler) {
		this.answerMap = ctx.getMap(Names.PERSISTENCE_QUERY_ANSWERS_MAP_NAME);
		this.queryQueue = ctx.getQueue(Names.PERSISTENCE_QUERY_QUEUE_NAME);
		this.queryReader = om.reader(Query.class);
		this.recycler = recycler;
		this.answerReadyNotifier = new LinkedBlockingQueue<String>();
	}

	public String handle(String message) throws SocketHandlerException, InterruptedException {
		log.debug("Got {}", message);
		Query q = null;
		try {
			q = queryReader.readValue(message);
		} catch (IOException e) {
			throw new SocketHandlerException(String.format("Failed to deserialize query '%s'", message), e);
		}
		answerMap.addEntryListener(new MapHook(answerReadyNotifier), q.getId(), false);
		queryQueue.add(q);
		final String queryId = answerReadyNotifier.take();
		if (!q.getId().equals(queryId)) {
			throw new SocketHandlerException(String.format("Query ID '%s' does not match the ID from notifier ('%s')", q.getId(), queryId));
		}
		final String answer = answerMap.remove(queryId).toString();
		log.debug("Replying {}", answer);
		return answer;
	}

	@Override
	public void markAsRecyclable() {
		if (recycler != null) {
			recycler.recycle(this);
		}
	}

	private class MapHook implements EntryListener<String, Collection<String>> {
		/**
		 * Queue that notifies the waiting thread that its value is in the map
		 */
		private final BlockingQueue<String> notifier;

		MapHook(BlockingQueue<String> notifier) {
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
