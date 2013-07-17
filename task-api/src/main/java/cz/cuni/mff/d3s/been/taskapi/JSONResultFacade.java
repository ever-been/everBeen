package cz.cuni.mff.d3s.been.taskapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import cz.cuni.mff.d3s.been.persistence.Query;
import cz.cuni.mff.d3s.been.persistence.QuerySerializer;
import cz.cuni.mff.d3s.been.util.JsonException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.mq.IMessageQueue;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.results.Result;
import cz.cuni.mff.d3s.been.socketworks.NamedSockets;
import cz.cuni.mff.d3s.been.socketworks.twoway.Requestor;

final class JSONResultFacade implements ResultFacade, ResultPersisterCatalog {

	private static final Logger log = LoggerFactory.getLogger(JSONResultFacade.class);

	private final ObjectMapper om;
	private final QuerySerializer querySerializer;
	private final IMessageQueue<String> queue;
	private final Collection<ResultPersister> allocatedPersisters;
	String taskId, contextId, benchmarkId;

	private JSONResultFacade(IMessageQueue<String> queue) {
		this.queue = queue;
		this.om = new ObjectMapper();
		this.querySerializer = new QuerySerializer();
		this.allocatedPersisters = new HashSet<ResultPersister>();

		om.setSerializationConfig(om.getSerializationConfig().without(Feature.FAIL_ON_EMPTY_BEANS).withVisibilityChecker(
				new FieldVisibilityChecker()));
		om.setDeserializationConfig(om.getDeserializationConfig().withVisibilityChecker(new FieldVisibilityChecker()));
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
	}

	/** Create a new result serialization facade */
	static JSONResultFacade create(IMessageQueue<String> queue) {
		return new JSONResultFacade(queue);
	}

	@Override
	public <T extends Result> T createResult(Class<T> resultClass) throws IllegalAccessException, InstantiationException {
		final T inst = resultClass.newInstance();
		inst.setTaskId(taskId);
		inst.setContextId(contextId);
		inst.setBenchmarkId(benchmarkId);
		inst.setCreated(System.currentTimeMillis());
		return inst;
	}

	@Override
	public void persistResult(Result result, EntityID entityId) throws DAOException {
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
		ec.setEntityId(entityId);
		ec.setEntityJSON(serializedResult);
		log.debug("Facade serialized a result into >>{}<<", serializedResult);
		sendRC(ec);
	}

	@Override
	public <T extends Result> Collection<T> query(Query fetchQuery, Class<T> resultClass) throws DAOException {
		Requestor requestor = null;
		String queryString = null;
		String replyString = null;

		try {
			queryString = querySerializer.serializeQuery(fetchQuery);
		} catch (JsonException e) {
			throw new DAOException("Failed to serialize query", e);
		}

		log.debug(
				"Attempting to connect to persistence querying socket on {}",
				NamedSockets.TASK_RESULT_QUERY_0MQ.getConnection());
		try {
			requestor = Requestor.create(NamedSockets.TASK_RESULT_QUERY_0MQ.getConnection());
		} catch (MessagingException e) {
			throw new DAOException("Failed to create result query request", e);
		}

		log.debug("Querying persistence with {}", queryString);
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
		log.debug("Persistence replied {}", replyString);

		try {
			final Collection<String> coll = om.readValue(replyString, Collection.class);
			final ArrayList<T> res = new ArrayList<T>(coll.size());
			for (String elm : coll) {
				res.add(om.readValue(elm, resultClass));
			}
			return res;
		} catch (IOException e) {
			throw new DAOException(String.format("Failed to deserialize results matching query %s", queryString), e);
		}
	}

	@Override
	public ResultPersister createResultPersister(EntityID entityId) throws DAOException {
		try {
			final JSONResultPersister persister = new JSONResultPersister(entityId, queue.createSender(), this);
			allocatedPersisters.add(persister);
			return persister;
		} catch (MessagingException e) {
			// TODO rethink whether forwarding the stacktrace to the user is reasonable here
			throw new DAOException("Cannot open result sending channel.", e);
		}
	}

	@Override
	public void unhook(ResultPersister persister) {
		if (allocatedPersisters.contains(persister)) {
			allocatedPersisters.remove(persister);
		}
	}

	/** Clean up any dangling persisters */
	void purge() {
		for (ResultPersister persister : allocatedPersisters) {
			log.warn("Persister {} was not closed, purging automatically", persister.toString());
			persister.close();
		}
	}

	private void sendRC(EntityCarrier rc) throws DAOException {
		try {
			final String serializedRC = om.writeValueAsString(rc);
			log.debug("About to request this serialized result carrier to Host Runtime: >>{}<<", serializedRC);
			final IMessageSender<String> sender = queue.createSender();
			sender.send(serializedRC);
			sender.close();
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
	private class JSONResultPersister implements ResultPersister {

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
		JSONResultPersister(EntityID entityId, IMessageSender<String> sender, ResultPersisterCatalog unhookCatalog) {
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
			log.debug("Persister serialized a result into >>{}<<", serializedResult);
			final EntityCarrier rc = new EntityCarrier();
			rc.setEntityId(entityId);
			rc.setEntityJSON(serializedResult);
			sendRC(rc);
		}

		@Override
		public void close() {
			unhookCatalog.unhook(this);
			sender.close();
		}
	}
}
