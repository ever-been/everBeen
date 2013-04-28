package cz.cuni.mff.d3s.been.persistence;

import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;

/**
 * This listener counts the number of items in the queue to estimate whether the
 * current number of worker threads is capable of handling the amount of results
 * currently within the cluster.
 * 
 * If the number of messages seems too high, this listener allocates new
 * threads. In case the queue is being drained, the listener deallocates some
 * worker threads.
 * 
 * @author darklight
 * 
 */
class QueueItemCounterListener<T> implements ItemListener<T> {

	private static final long LOAD_BALANCE_INTERVAL_MILLIS = 10000;

	/** The pool of digester threads */
	private final QueueDigester<T> digester;
	/** Counter of available jobs (results to persist) */
	private int jobCount = 0;
	/** State of the job counter from the last time a load balance was performed */
	private int lastJobCount = 0;
	/** Time when the last load balance was performed */
	private long lastLoadBalanceTimestamp = System.currentTimeMillis();

	QueueItemCounterListener(QueueDigester<T> digester) {
		this.digester = digester;
	}

	@Override
	public void itemAdded(ItemEvent<T> item) {
		++jobCount;
		balanceLoad();
	}

	@Override
	public void itemRemoved(ItemEvent<T> item) {
		--jobCount;
	}

	private void balanceLoad() {
		final long now = System.currentTimeMillis();
		if (now > lastLoadBalanceTimestamp + LOAD_BALANCE_INTERVAL_MILLIS) {
			if (jobCount > lastJobCount) {
				digester.addNewWorkingThread();
			}
			lastLoadBalanceTimestamp = now;
			lastJobCount = jobCount;
		}
	}
}
