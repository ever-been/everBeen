package cz.cuni.mff.d3s.been.taskapi.results;

import java.io.IOException;
import java.util.Collection;

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

final class JSONResultFacade implements ResultFacade {

	private static final Logger log = LoggerFactory.getLogger(JSONResultFacade.class);

	private final ObjectMapper om;
	private final IMessageSender<String> sender;

	public JSONResultFacade(IMessageSender<String> sender) {
		this.sender = sender;
		this.om = new ObjectMapper();
		om.setSerializationConfig(om.getSerializationConfig().without(
				Feature.FAIL_ON_EMPTY_BEANS).withVisibilityChecker(
				new ResultFieldVisibilityChecker<>()));
	}
	@Override
	public
			void
			persistResult(Result result, EntityID entityId) throws DAOException {
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
	public ResultPersister createResultPersister(EntityID entityId) {
		return new JSONResultPersister(entityId);
	}

	/**
	 * A persister implementation that serializes the object into JSON.
	 * 
	 * @author darklight
	 */
	private class JSONResultPersister implements ResultPersister {

		private final EntityID entityId;

		/**
		 * Initialize a JSON persister bound to a specific persistence collection.
		 * 
		 * @param entityId
		 *          Persistent entity to bind to (determines target collection)
		 */
		JSONResultPersister(EntityID entityId) {
			this.entityId = entityId;
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
	}

	private void sendRC(EntityCarrier rc) throws DAOException {
		try {
			final String serializedRC = om.writeValueAsString(rc);
			log.info(
					"About to send this serialized result carrier to Host Runtime: >>{}<<",
					serializedRC);
			sender.send(serializedRC);
		} catch (IOException e) {
			throw new DAOException("Unable to serialize result carrier to JSON", e);
		} catch (MessagingException e) {
			throw new DAOException("Unable to send serialized result carrier to Host Runtime");
		}
	}
}
