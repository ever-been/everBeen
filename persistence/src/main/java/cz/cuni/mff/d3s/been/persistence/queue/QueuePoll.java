package cz.cuni.mff.d3s.been.persistence.queue;

import com.hazelcast.core.IQueue;
import cz.cuni.mff.d3s.been.persistence.Poll;

/**
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
