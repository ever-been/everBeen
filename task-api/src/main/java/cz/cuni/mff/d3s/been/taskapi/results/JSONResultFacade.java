package cz.cuni.mff.d3s.been.taskapi.results;

import java.io.IOException;
import java.util.Collection;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.results.DAOException;
import cz.cuni.mff.d3s.been.results.Result;
import cz.cuni.mff.d3s.been.results.ResultCarrier;
import cz.cuni.mff.d3s.been.results.ResultContainerId;
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
	public void persistResult(Result result, ResultContainerId containerId) throws DAOException {
		if (result == null) {
			throw new DAOException("Cannot serialize a null object.");
		}
		ResultCarrier rc = null;
		String serializedResult = null;
		try {
			serializedResult = om.writeValueAsString(result);
		} catch (IOException e) {
			throw new DAOException(String.format(
					"Unable to serialize Result %s to JSON.",
					result.toString()), e);
		}
		rc = new ResultCarrier();
		rc.setContainerId(containerId);
		rc.setData(serializedResult);
		log.info("Facade serialized a result into >>{}<<", serializedResult);
		sendRC(rc);
	}

	@Override
	public Collection<Result> retrieveResults(ResultFilter filter) {
		throw new UnsupportedOperationException("Result querying is not supported yet.");
	}

	@Override
	public ResultPersister createResultPersister(ResultContainerId containerId) {
		return new JSONResultPersister(containerId);
	}

	/**
	 * A persister implementation that serializes the object into JSON.
	 * 
	 * @author darklight
	 */
	private class JSONResultPersister implements ResultPersister {

		private final ResultContainerId containerId;

		/**
		 * Initialize a JSON persister bound to a specific persistence collection.
		 * 
		 * @param containerId
		 */
		public JSONResultPersister(ResultContainerId containerId) {
			this.containerId = containerId;
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
			final ResultCarrier rc = new ResultCarrier();
			rc.setContainerId(containerId);
			rc.setData(serializedResult);
			sendRC(rc);
		}
	}

	private void sendRC(ResultCarrier rc) throws DAOException {
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
