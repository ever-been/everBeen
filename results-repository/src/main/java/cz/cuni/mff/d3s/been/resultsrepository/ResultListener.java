package cz.cuni.mff.d3s.been.resultsrepository;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;

import cz.cuni.mff.d3s.been.results.DAOException;
import cz.cuni.mff.d3s.been.results.ResultCarrier;
import cz.cuni.mff.d3s.been.resultsrepository.storage.Storage;

public class ResultListener implements ItemListener<ResultCarrier> {

	private static final Logger log = LoggerFactory.getLogger(ResultListener.class);

	/** The storage this listener usest to persist data */
	private final Storage resultStorage;
	private final ObjectMapper om;

	public ResultListener(Storage resultStorage) {
		this.resultStorage = resultStorage;
		this.om = new ObjectMapper();
	}

	@Override
	public void itemAdded(ItemEvent<ResultCarrier> evt) {
		ResultCarrier rc = evt.getItem();

		try {
			resultStorage.storeResult(
					rc.getContainerId(),
					om.writeValueAsString(evt.getItem()));
		} catch (DAOException | IOException e) {
			log.error(
					"Failed to serialize Result {} to JSON - {}",
					evt.getItem(),
					e.getMessage());
			log.debug("Reasons for failed Result serialization:", e);
		}
	}

	@Override
	public void itemRemoved(ItemEvent<ResultCarrier> evt) {
		// TODO remove item from persistence layer
	}

}
