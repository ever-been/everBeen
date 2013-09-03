package cz.cuni.mff.d3s.been.objectrepository;

import com.hazelcast.core.IQueue;

/**
 * Take action on an {@link IQueue}
 *
 * @param <T> Type of items taken
 *
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
