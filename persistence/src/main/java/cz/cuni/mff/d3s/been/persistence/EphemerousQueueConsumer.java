package cz.cuni.mff.d3s.been.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IQueue;

public class EphemerousQueueConsumer<T> extends QueueConsumer<T> {

	private static final Logger log = LoggerFactory.getLogger(EphemerousQueueConsumer.class);

	public EphemerousQueueConsumer(
			IQueue<T> queue,
			PersistAction<T> pa) {
		super(queue, pa);
	}

	@Override
	public void run() {
		log.debug("Thread starting.");
		while (!Thread.currentThread().isInterrupted()) {
			final T item = queue.poll();
			log.debug("Taken item {} from the queue.", item);
			if (item == null) {
				// there is nothing to do, end execution (this thread is ephemerous)
				Thread.currentThread().interrupt();
			} else {
				persist(item);
			}
		}
		log.debug("Thread terminating.");
	}
}
