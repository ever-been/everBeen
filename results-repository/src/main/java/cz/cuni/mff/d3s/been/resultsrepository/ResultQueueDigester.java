package cz.cuni.mff.d3s.been.resultsrepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IQueue;

import cz.cuni.mff.d3s.been.results.ResultCarrier;
import cz.cuni.mff.d3s.been.resultsrepository.storage.Storage;

class ResultQueueDigester {

	private static final long POOL_SHUTDOWN_TIMEOUT_MILLIS = 5000;
	private static final Logger log = LoggerFactory.getLogger(ResultQueueDigester.class);

	private final ExecutorService pool;
	private final IQueue<ResultCarrier> resQueue;
	private final Storage storage;

	ResultQueueDigester(IQueue<ResultCarrier> resQueue, Storage storage) {
		this.resQueue = resQueue;
		this.storage = storage;
		pool = Executors.newCachedThreadPool();
	}

	public void addNewWorkingThread() {
		pool.execute(new ResultQueueDigesterEphemerousThread(resQueue, storage));
	}

	public void start() {
		pool.execute(new ResultQueueDigesterLingeringThread(resQueue, storage));
	}

	public void stop() {
		pool.shutdown();
		try {
			pool.awaitTermination(POOL_SHUTDOWN_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.error("Result queue worker pool forced to terminate. Results may have been lost.");
		}
	}

}
