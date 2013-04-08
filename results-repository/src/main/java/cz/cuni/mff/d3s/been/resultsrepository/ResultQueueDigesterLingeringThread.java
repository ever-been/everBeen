package cz.cuni.mff.d3s.been.resultsrepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IQueue;

import cz.cuni.mff.d3s.been.results.ResultCarrier;
import cz.cuni.mff.d3s.been.resultsrepository.storage.Storage;

public class ResultQueueDigesterLingeringThread extends ResultQueueDigesterThread {

	private static final Logger log = LoggerFactory.getLogger(ResultQueueDigesterLingeringThread.class);

	ResultQueueDigesterLingeringThread(
			IQueue<ResultCarrier> resQueue,
			Storage storage) {
		super(resQueue, storage);
	}

	@Override
	public void run() {
		super.run();
		log.debug("Thread starting.");
		while (!this.isInterrupted()) {
			try {
				final ResultCarrier rc = resQueue.take();
				if (rc == null) {
					// The documentation doesn't say this, but Hazelcast queue yields null
					// rather than throwing an exception when interrupted
					// This throw is not entirely correct, as internally, Hazelcast
					// only uses a big timeout for take(). However, the timeout being
					// Long.MAX_VALUE, this implementation is unlikely to return null in any
					// other circumstances than during interruption. 
					throw new InterruptedException("Queue take yielded null result.");
				}
				log.debug("Taken result {} from queue", rc);
				persistAResult(rc);
			} catch (InterruptedException e) {
				// the take has been interrupted - a signal that this repository is being terminated
				log.debug("Queue take interrupted..");
				this.interrupt();
			}
		}
		log.debug("Thread terminating.");
	}
}
