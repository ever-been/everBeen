package cz.cuni.mff.d3s.been.taskapi.results;

import java.io.IOException;
import java.util.Collection;

import org.codehaus.jackson.map.ObjectMapper;

import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.results.DAOException;
import cz.cuni.mff.d3s.been.results.Result;
import cz.cuni.mff.d3s.been.results.ResultCarrier;
import cz.cuni.mff.d3s.been.results.ResultContainerId;
import cz.cuni.mff.d3s.been.results.ResultFilter;

final class JSONResultFacade implements ResultFacade {

	private final ObjectMapper om;
	private final IMessageSender<String> sender;

	public JSONResultFacade(IMessageSender<String> sender) {
		this.sender = sender;
		this.om = new ObjectMapper();
	}

	@Override
	public void persistResult(Result result, ResultContainerId containerId) throws DAOException {
		if (result == null) {
			throw new DAOException("Cannot serialize a null object.");
		}
		ResultCarrier rc = null;
		try {
			rc = new ResultCarrier();
			rc.setContainerId(containerId);
			rc.setData(om.writeValueAsString(result));
		} catch (IOException e) {
			throw new DAOException(String.format(
					"Unable to serialize Result %s to JSON.",
					result.toString()), e);
		}

		try {
			sender.send(om.writeValueAsString(rc));
		} catch (IOException e) {
			throw new DAOException("Unable to serialize result carrier to JSON.", e);
		} catch (MessagingException e) {
			throw new DAOException("Unable to send result carrier to host runtime.", e);
		}
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
			try {
				final ResultCarrier rc = new ResultCarrier();
				rc.setContainerId(containerId);
				rc.setData(serializedResult);
				sender.send(om.writeValueAsString(rc));
			} catch (IOException e) {
				throw new DAOException("Unable to serialize result carrier to JSON", e);
			} catch (MessagingException e) {
				e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
			}
		}

	}

}
