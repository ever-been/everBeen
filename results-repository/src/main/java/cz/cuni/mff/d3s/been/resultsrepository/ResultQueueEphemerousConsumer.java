package cz.cuni.mff.d3s.been.resultsrepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IQueue;

import cz.cuni.mff.d3s.been.results.ResultCarrier;
import cz.cuni.mff.d3s.been.resultsrepository.storage.Storage;

public class ResultQueueEphemerousConsumer extends ResultQueueDigesterThread {

	private static final Logger log = LoggerFactory.getLogger(ResultQueueEphemerousConsumer.class);

	public ResultQueueEphemerousConsumer(
			IQueue<ResultCarrier> resQueue,
			Storage storage) {
		super(resQueue, storage);
	}

	@Override
	public void run() {
		log.debug("Thread starting.");
		while (!Thread.currentThread().isInterrupted()) {
			final ResultCarrier rc = resQueue.poll();
			if (rc == null) {
				// there is nothing to do, end execution (this thread is ephemerous) 
				Thread.currentThread().interrupt();
			}
			log.debug("Taken result {} from the queue.", rc);
			persistAResult(rc);
		}
		log.debug("Thread terminating.");
	}
}
