package cz.cuni.mff.d3s.been.taskapi;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import cz.cuni.mff.d3s.been.mq.IMessageQueue;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.results.Result;
import cz.cuni.mff.d3s.been.results.ResultFilter;

final class JSONResultFacade implements ResultFacade, ResultPersisterCatalog {

	private static final Logger log = LoggerFactory.getLogger(JSONResultFacade.class);

	private final ObjectMapper om;
	private final IMessageQueue<String> queue;
	private final Collection<ResultPersister> allocatedPersisters;

	private JSONResultFacade(IMessageQueue<String> queue) {
		this.queue = queue;
		this.om = new ObjectMapper();
		this.allocatedPersisters = new HashSet<ResultPersister>();
		om.setSerializationConfig(om.getSerializationConfig().without(
				Feature.FAIL_ON_EMPTY_BEANS).withVisibilityChecker(
				new ResultFieldVisibilityChecker<>()));
	}

    /** Create a new result serialization facade */
    static JSONResultFacade create(IMessageQueue<String> queue) {
        return new JSONResultFacade(queue);
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
			throw new DAOException(String.format(
					"Unable to serialize Result %s to JSON.",
					result.toString()), e);
		}
		ec = new EntityCarrier();
		ec.setEntityId(entityId);
		ec.setEntityJSON(serializedResult);
		log.info("Facade serialized a result into >>{}<<", serializedResult);
		sendRC(ec);
	}

	@Override
	public Collection<Result> retrieveResults(ResultFilter filter) {
		throw new UnsupportedOperationException("Result querying is not supported yet.");
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
		for(ResultPersister persister: allocatedPersisters) {
			log.warn("Persister {} was not closed, purging automatically", persister.toString());
			persister.close();
		}
	}

	private void sendRC(EntityCarrier rc) throws DAOException {
		try {
			final String serializedRC = om.writeValueAsString(rc);
			log.info(
					"About to send this serialized result carrier to Host Runtime: >>{}<<",
					serializedRC);
			final IMessageSender<String> sender = queue.createSender();
			sender.send(serializedRC);
			sender.close();
		} catch (IOException e) {
			throw new DAOException("Unable to serialize result carrier to JSON", e);
		} catch (MessagingException e) {
			throw new DAOException("Unable to send serialized result carrier to Host Runtime");
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
		 * @param sender Sender to use for result trafficking
		 * @param unhookCatalog Catalog to use for uregistering once this persister is closed
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
				throw new DAOException(String.format(
						"Unable to serialize Result %s to json",
						result.toString()), e);
			}
			log.info("Persister serialized a result into >>{}<<", serializedResult);
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
