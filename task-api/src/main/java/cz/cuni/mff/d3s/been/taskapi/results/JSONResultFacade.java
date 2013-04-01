package cz.cuni.mff.d3s.been.taskapi.results;

import java.io.IOException;
import java.util.Collection;

import org.codehaus.jackson.map.ObjectMapper;

import cz.cuni.mff.d3s.been.results.DAOException;
import cz.cuni.mff.d3s.been.results.Result;
import cz.cuni.mff.d3s.been.results.ResultCarrier;
import cz.cuni.mff.d3s.been.results.ResultContainerId;
import cz.cuni.mff.d3s.been.results.ResultFilter;
import cz.cuni.mff.d3s.been.taskapi.mq.Messaging;

final class JSONResultFacade implements ResultFacade {

	private final ObjectMapper om;
	private final Messaging msg;

	public JSONResultFacade(Messaging msg) {
		this.om = new ObjectMapper();
		this.msg = msg;
	}

	@Override
	public void persistResult(Result result, ResultContainerId containerId) throws DAOException {
		if (result == null) {
			throw new DAOException("Cannot serialize a null object.");
		}
		ResultCarrier rc = null;
		try {
			rc = new ResultCarrier(containerId, om.writeValueAsString(result));
		} catch (IOException e) {
			throw new DAOException(String.format(
					"Unable to serialize Result %s to JSON.",
					result.toString()), e);
		}

		try {
			msg.send(om.writeValueAsString(rc));
		} catch (IOException e) {
			throw new DAOException("Unable to serialize result carrier to JSON", e);
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
				msg.send(om.writeValueAsString(new ResultCarrier(containerId, serializedResult)));
			} catch (IOException e) {
				throw new DAOException("Unable to serialize result carrier to JSON", e);
			}
		}

	}

}
