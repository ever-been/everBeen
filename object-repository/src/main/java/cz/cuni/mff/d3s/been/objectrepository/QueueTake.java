package cz.cuni.mff.d3s.been.objectrepository;

import com.hazelcast.core.IQueue;

/**
 * @author darklight
 */
public class QueueTake<T> implements Take<T> {
	private final IQueue<T> queue;

	QueueTake(IQueue<T> queue) {
		this.queue = queue;
	}

	@Override
	public T perform() throws InterruptedException {
		return queue.take();
	}
}
