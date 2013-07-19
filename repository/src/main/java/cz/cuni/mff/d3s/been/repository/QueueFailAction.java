package cz.cuni.mff.d3s.been.repository;

import com.hazelcast.core.IQueue;

/**
 * @author darklight
 */
public class QueueFailAction<T> implements FailAction<T> {
	private final IQueue<T> queue;

	QueueFailAction(IQueue<T> queue) {
		this.queue = queue;
	}

	@Override
	public void perform(T on) throws InterruptedException {
		queue.put(on);
	}
}
