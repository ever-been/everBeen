package cz.cuni.mff.d3s.been.resultsrepository;

import com.hazelcast.core.IQueue;

import cz.cuni.mff.d3s.been.results.ResultCarrier;
import cz.cuni.mff.d3s.been.resultsrepository.storage.Storage;

public class ResultQueueDigesterEphemerousThread extends ResultQueueDigesterThread {

	public ResultQueueDigesterEphemerousThread(
			IQueue<ResultCarrier> resQueue,
			Storage storage) {
		super(resQueue, storage);
	}

	@Override
	public void run() {
		super.run();
		while (!interrupted()) {
			final ResultCarrier res = resQueue.poll();
			if (res == null) {
				// there is nothing to do, end execution (this thread is ephemerous) 
				this.interrupt();
			}
			persistAResult(res);
		}
	}
}
