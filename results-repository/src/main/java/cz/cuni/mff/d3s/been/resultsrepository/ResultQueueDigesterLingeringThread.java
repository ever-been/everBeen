package cz.cuni.mff.d3s.been.resultsrepository;

import com.hazelcast.core.IQueue;

import cz.cuni.mff.d3s.been.results.ResultCarrier;
import cz.cuni.mff.d3s.been.resultsrepository.storage.Storage;

public class ResultQueueDigesterLingeringThread extends ResultQueueDigesterThread {

	ResultQueueDigesterLingeringThread(
			IQueue<ResultCarrier> resQueue,
			Storage storage) {
		super(resQueue, storage);
	}

	@Override
	public void run() {
		while (!interrupted()) {
			try {
				persistAResult(resQueue.take());
			} catch (InterruptedException e) {
				// the take has been interrupted - a signal that this repository is being terminated
				this.interrupt();
			}
		}
	}

}
