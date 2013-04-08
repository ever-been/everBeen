package cz.cuni.mff.d3s.been.resultsrepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IQueue;

import cz.cuni.mff.d3s.been.results.ResultCarrier;
import cz.cuni.mff.d3s.been.resultsrepository.storage.Storage;

public class ResultQueueDigesterEphemerousThread extends ResultQueueDigesterThread {

	private static final Logger log = LoggerFactory.getLogger(ResultQueueDigesterEphemerousThread.class);

	public ResultQueueDigesterEphemerousThread(
			IQueue<ResultCarrier> resQueue,
			Storage storage) {
		super(resQueue, storage);
	}

	@Override
	public void run() {
		super.run();
		log.debug("Thread starting.");
		while (!interrupted()) {
			final ResultCarrier rc = resQueue.poll();
			if (rc == null) {
				// there is nothing to do, end execution (this thread is ephemerous) 
				this.interrupt();
			}
			log.debug("Taken result {} from the queue.", rc);
			persistAResult(rc);
		}
		log.debug("Thread terminating.");
	}
}
