package cz.cuni.mff.d3s.been.objectrepository;

import com.hazelcast.core.IQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fail action on an {@link IQueue}
 *
 * @param <T> Type of queue items
 *
 * @author darklight
 */
public class QueueFailAction<T> implements FailAction<T> {

	private static final Logger log = LoggerFactory.getLogger(QueueFailAction.class);

	private final IQueue<T> queue;

	QueueFailAction(IQueue<T> queue) {
		this.queue = queue;
	}

	@Override
	public void perform(T on) {
		try {
			queue.put(on);
		} catch (InterruptedException e) {
			log.error("Requeue action interrupted. Data lost: {}", on);
		}
	}
}
