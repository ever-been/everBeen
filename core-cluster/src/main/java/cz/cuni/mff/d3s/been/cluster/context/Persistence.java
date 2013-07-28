package cz.cuni.mff.d3s.been.cluster.context;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.core.PropertyReader;
import cz.cuni.mff.d3s.been.core.persistence.Entity;
import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.*;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static cz.cuni.mff.d3s.been.cluster.ClusterPersistenceConfiguration.*;

/**
 * Cluster-based operations related to object persistence and retrieval
 *
 * @author darklight
 */
public class Persistence {

	private final Long queryTimeout;
	private final Long queryProcessingTimeout;

	private final IQueue<EntityCarrier> asyncPersistence;
	private final IQueue<Query> queryQueue;
	private final IMap<String, QueryAnswer> queryAnswerMap;
	private final JSONUtils jsonUtils;

	Persistence(ClusterContext ctx) {
		final PropertyReader propertyReader = PropertyReader.on(ctx.getProperties());
		this.queryTimeout = propertyReader.getLong(QUERY_TIMEOUT, DEFAULT_QUERY_TIMEOUT);
		this.queryProcessingTimeout = propertyReader.getLong(QUERY_PROCESSING_TIMEOUT, DEFAULT_QUERY_PROCESSING_TIMEOUT);

		this.asyncPersistence = ctx.getQueue(Names.PERSISTENCE_QUEUE_NAME);
		this.queryAnswerMap = ctx.getMap(Names.PERSISTENCE_QUERY_ANSWERS_MAP_NAME);
		this.queryQueue = ctx.getQueue(Names.PERSISTENCE_QUERY_QUEUE_NAME);
		this.jsonUtils = JSONUtils.newInstance();
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
		final BlockingQueue<String> answerReadyNotifier = new LinkedBlockingQueue<String>();
		final MapEntryReadyHook hook = new MapEntryReadyHook(answerReadyNotifier);
		queryAnswerMap.addEntryListener(hook, query.getId(), false);
		try {
			queryQueue.add(query);
			if (answerReadyNotifier.poll(queryTimeout, TimeUnit.SECONDS) == null) {
				if (queryQueue.remove(query)) {
					return QueryAnswerFactory.transportTimedOut();
				} else {
					if (answerReadyNotifier.poll(queryProcessingTimeout, TimeUnit.SECONDS) != null) {
						return queryAnswerMap.remove(query.getId());
					} else {
						return QueryAnswerFactory.processingTimedOut();
					}
				}
			}
			return queryAnswerMap.remove(query.getId());
		} catch (InterruptedException e) {
			throw new DAOException(String.format("Interrupted when waiting for query result. Query was %s", query.toString()));
		} finally {
			queryAnswerMap.removeEntryListener(hook);
		}
	}

	/**
	 * Asynchronously persist an object
	 *
	 * @param entityID ID of the targeted entity (determines targeted collection)
	 * @param entity Persistable object to save
	 *
	 * @throws DAOException When the persistence attempt is thwarted
	 */
	public final void asyncPersist(EntityID entityID, Entity entity) throws DAOException {
		try {
			final String entityJson = jsonUtils.serialize(entity);
			asyncPersistence.put(new EntityCarrier().withId(entityID).withData(entityJson));
		} catch (JsonException e) {
			throw new DAOException("Cannot serialize persistent object", e);
		} catch (InterruptedException e) {
			throw new DAOException("Interrupted when trying to enqueue object for persistence", e);
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
