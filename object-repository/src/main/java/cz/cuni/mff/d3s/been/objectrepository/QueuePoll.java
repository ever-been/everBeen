package cz.cuni.mff.d3s.been.objectrepository;

import com.hazelcast.core.IQueue;

/**
 * Poll action on an {@link IQueue}
 *
 * @param <T> Type of items polled
 *
 * @author darklight
 */
public class QueuePoll<T> implements Poll<T> {
	private final IQueue<T> queue;

	QueuePoll(IQueue<T> queue) {
		this.queue = queue;
	}

	@Override
	public T perform() {
		return queue.poll();
	}
}
