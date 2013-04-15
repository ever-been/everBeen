package cz.cuni.mff.d3s.been.resultsrepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IQueue;

import cz.cuni.mff.d3s.been.results.DAOException;
import cz.cuni.mff.d3s.been.results.ResultCarrier;
import cz.cuni.mff.d3s.been.resultsrepository.storage.Storage;

abstract class ResultQueueDigesterThread implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(ResultQueueDigesterThread.class);

	/** Result queue to digest */
	protected final IQueue<ResultCarrier> resQueue;
	/** Result persistence layer */
	protected final Storage storage;

	ResultQueueDigesterThread(IQueue<ResultCarrier> resQueue, Storage storage) {
		this.resQueue = resQueue;
		this.storage = storage;
	}

	protected void persistAResult(ResultCarrier result) {
		try {
			storage.storeResult(result.getContainerId(), result.getData());
		} catch (DAOException e) {
			log.error(
					"Cannot store result {} - {}",
					result.toString(),
					e.getMessage());
			requeueResult(result);
		}
	}

	protected void requeueResult(ResultCarrier result) {
		while (true) {
			try {
				resQueue.put(result);
				break;
			} catch (InterruptedException e) {
				log.warn("Worker thread interrupted when attempting to requeue a result {}. Will be reattempting that indefinitely to prevent data loss.");
				continue;
			}
		}
	}
}
