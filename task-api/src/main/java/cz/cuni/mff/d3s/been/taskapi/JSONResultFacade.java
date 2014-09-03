package cz.cuni.mff.d3s.been.taskapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import cz.cuni.mff.d3s.been.util.JsonToTypedMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.mq.IMessageQueue;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.persistence.*;
import cz.cuni.mff.d3s.been.results.Result;
import cz.cuni.mff.d3s.been.socketworks.NamedSockets;
import cz.cuni.mff.d3s.been.socketworks.twoway.Requestor;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

final class JSONResultFacade implements ResultFacade, ResultPersisterCatalog {

	private static final Logger log = LoggerFactory.getLogger(JSONResultFacade.class);

	private final ObjectMapper om;
	private final QuerySerializer querySerializer;
	private final JSONUtils jsonUtils;
	private final IMessageQueue<String> queue;
	private final Collection<Persister> allocatedPersisters;

	/** Whether this instance has been cleaned-up */
	private boolean isPurged = false;
	String taskId, contextId, benchmarkId;

	private JSONResultFacade(IMessageQueue<String> queue) {
		this.queue = queue;
		this.om = new ObjectMapper();
		this.querySerializer = new QuerySerializer();
		this.allocatedPersisters = new HashSet<>();

		om.setSerializationConfig(om.getSerializationConfig().without(Feature.FAIL_ON_EMPTY_BEANS).withVisibilityChecker(
				new FieldVisibilityChecker()));
		om.setDeserializationConfig(om.getDeserializationConfig().withVisibilityChecker(new FieldVisibilityChecker()));
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);

		jsonUtils = JSONUtils.newInstance(om);
	}

	/** Create a new result serialization facade */
	static JSONResultFacade create(IMessageQueue<String> queue) {
		return new JSONResultFacade(queue);
	}

	@Override
	public <T extends Result> T createResult(Class<T> resultClass) throws DAOException {
		try {
			final T inst = resultClass.newInstance();
			inst.setTaskId(taskId);
			inst.setContextId(contextId);
			inst.setBenchmarkId(benchmarkId);
			inst.setCreated(System.currentTimeMillis());
			return inst;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new DAOException("Cannot create result", e);
		}
	}

	@Override
	public synchronized void persistResult(Result result, String group) throws DAOException {
		if (result == null) {
			throw new DAOException("Cannot serialize a null object.");
		}
		EntityCarrier ec = null;
		String serializedResult = null;
		try {
			serializedResult = om.writeValueAsString(result);
		} catch (IOException e) {
			throw new DAOException(String.format("Unable to serialize Result %s to JSON.", result.toString()), e);
		}
		ec = new EntityCarrier();
		ec.setEntityId(new EntityID().withKind("result").withGroup(group));
		ec.setEntityJSON(serializedResult);
		log.trace("Facade serialized a result into >>{}<<", serializedResult);

		try (final IMessageSender<String> sender = queue.createSender()) {
			sendRC(ec, sender);
		} catch (MessagingException e) {
			throw new DAOException("Cannot persist a result", e);
		}

	}

	private QueryAnswer performFetchQuery(Query fetchQuery) throws DAOException {
		QueryChecks.assertIsFetch(fetchQuery);
		QueryChecks.assertIsResult(fetchQuery);

		final String queryString;
		try {
			queryString = querySerializer.serializeQuery(fetchQuery);
		} catch (JsonException e) {
			throw new DAOException("Failed to serialize query", e);
		}

		final Requestor requestor;
		try {
			requestor = Requestor.create(NamedSockets.TASK_RESULT_QUERY_0MQ.getConnection());
		} catch (MessagingException e) {
			throw new DAOException("Failed to create result query requestor", e);
		}

		log.trace("Querying persistence with {}", queryString);

		final String replyString;
		try {
			replyString = requestor.request(queryString);
		} finally {
			try {
				requestor.close();
			} catch (MessagingException e) {
				log.error("Result querying connection left hanging. Task will not finish.", e);
			}
		}
		if (replyString == null) {
			throw new DAOException(String.format("Unknown failure when processing request %s", queryString));
		}

		log.trace("Persistence replied {}", replyString);

		final QueryAnswer answer;
		try {
			answer = querySerializer.deserializeAnswer(replyString);
		} catch (JsonException e) {
			throw new DAOException(String.format("Failed to split the result of query >>>%s<<< to single objects", fetchQuery.toString()), e);
		}
		if (!answer.isCarryingData()) {
			throw new DAOException(String.format(
					"Query returned with no data. Answer status is: '%s'",
					answer.getStatus().getDescription()));
		}
		return answer;
	}

	@Override
	public synchronized
			<T extends Result>
			Collection<T>
			query(Query fetchQuery, Class<T> resultClass) throws DAOException {

		final QueryAnswer answer = performFetchQuery(fetchQuery);
		try {
			return jsonUtils.deserialize(answer.getData(), resultClass);
		} catch (JsonException e) {
			throw new DAOException(String.format("Failed to deserialize results matching query %s", fetchQuery.toString()), e);
		}
	}

	@Override
	public Collection<Map<String, Object>> query(Query fetchQuery, ResultMapping mapping) throws DAOException {
		final QueryAnswer answer = performFetchQuery(fetchQuery);
		final Collection<Map<String, Object>> answerObjects = new ArrayList<Map<String, Object>>(answer.getData().size());
		try {
			jsonUtils.deserialize(answer.getData(), mapping.getTypeMapping(), false);
		} catch (JsonException e) {
			throw new DAOException("Cannot deserialize query results", e);
		}
		return answerObjects;
	}

	// @Override
	// Not permitted because of general contract, but remains here as a proof of concept
	public synchronized void delete(Query deleteQuery) throws DAOException {
		QueryChecks.assertIsDelete(deleteQuery);
		QueryChecks.assertIsResult(deleteQuery);

		String queryString = null;
		Requestor requestor = null;
		String answerString = null;

		try {
			queryString = querySerializer.serializeQuery(deleteQuery);
		} catch (JsonException e) {
			throw new DAOException("Failed to serialize query", e);
		}

		try {
			requestor = Requestor.create(NamedSockets.TASK_RESULT_QUERY_0MQ.getConnection());
		} catch (MessagingException e) {
			throw new DAOException("Failed to create query requestor", e);
		}

		try {
			answerString = requestor.request(queryString);
		} finally {
			try {
				requestor.close();
			} catch (MessagingException e) {
				throw new DAOException("Querying connection left hanging. Task will not finish", e);
			}
		}

		try {
			final QueryAnswer answer = querySerializer.deserializeAnswer(answerString);
			if (!QueryStatus.OK.equals(answer.getStatus())) {
				throw new DAOException(String.format("Query failed: %s", answer.getStatus().getDescription()));
			}
		} catch (JsonException e) {
			throw new DAOException("Failed to deserialize query answer", e);
		}
	}

	@Override
	public synchronized Persister createResultPersister(String group) throws DAOException {
		final EntityID eid = new EntityID().withKind("result").withGroup(group);
		return createPersister(eid);
	}

	synchronized Persister createPersister(EntityID entityID) throws DAOException {
		if (isPurged) {
			throw new DAOException("Result Facade is already purged!");
		}

		try {
			final JSONPersister persister = new JSONPersister(entityID, queue.createSender(), this);
			allocatedPersisters.add(persister);
			return persister;
		} catch (MessagingException e) {
			// TODO rethink whether forwarding the stacktrace to the user is reasonable here
			throw new DAOException("Cannot open result sending channel.", e);
		}
	}

	@Override
	public synchronized void unhook(Persister persister) {
		if (allocatedPersisters.contains(persister)) {
			allocatedPersisters.remove(persister);
		}
	}

	/** Clean up any dangling persisters */
	synchronized void purge() {
		isPurged = true;

		Collection<Persister> copy = new ArrayList<>(allocatedPersisters);

		for (Persister persister : copy) {
			log.warn("Persister {} was not closed, purging automatically", persister.toString());
			persister.close();
		}
	}

	private void sendRC(final EntityCarrier rc, final IMessageSender<String> sender) throws DAOException {
		try {
			final String serializedRC = om.writeValueAsString(rc);
			log.trace("About to request this serialized result carrier to Host Runtime: >>{}<<", serializedRC);
			sender.send(serializedRC);

			// TODO: Temporary bug workaround for JeroMQ big message bug
			// If sending big message and closing the socket immediately after JereMQ will
			// sometimes corrupt the message. This needs fixing at JeroMQ side ...
			if (serializedRC.length() > 100_000) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					//quell
				}
			}
		} catch (IOException e) {
			throw new DAOException("Unable to serialize result carrier to JSON", e);
		} catch (MessagingException e) {
			throw new DAOException("Unable to request serialized result carrier to Host Runtime");
		}
	}

	/**
	 * A persister implementation that serializes the object into JSON.
	 * 
	 * @author darklight
	 */
	private class JSONPersister implements Persister {

		private final EntityID entityId;
		private final IMessageSender<String> sender;
		private final ResultPersisterCatalog unhookCatalog;

		/**
		 * Initialize a JSON persister bound to a specific persistence collection.
		 * 
		 * @param entityId
		 *          Persistent entity to bind to (determines target collection)
		 * @param sender
		 *          Sender to use for result trafficking
		 * @param unhookCatalog
		 *          Catalog to use for uregistering once this persister is closed
		 */
		JSONPersister(EntityID entityId, IMessageSender<String> sender, ResultPersisterCatalog unhookCatalog) {
			this.entityId = entityId;
			this.sender = sender;
			this.unhookCatalog = unhookCatalog;
		}

		@Override
		public void persist(Result result) throws DAOException {
			String serializedResult = null;
			try {
				serializedResult = om.writeValueAsString(result);
			} catch (IOException e) {
				throw new DAOException(String.format("Unable to serialize Result %s to json", result.toString()), e);
			}
			log.trace("Persister serialized a result into >>{}<<", serializedResult);
			final EntityCarrier rc = new EntityCarrier();
			rc.setEntityId(entityId);
			rc.setEntityJSON(serializedResult);
			sendRC(rc, sender);
		}

		@Override
		public void close() {
			unhookCatalog.unhook(this);
			sender.close();
		}
	}
}
