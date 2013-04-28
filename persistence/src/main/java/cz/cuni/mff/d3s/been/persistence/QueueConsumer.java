package cz.cuni.mff.d3s.been.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IQueue;

import cz.cuni.mff.d3s.been.results.DAOException;

abstract class QueueConsumer<T> implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(QueueConsumer.class);

	/** Result queue to digest */
	protected final IQueue<T> queue;
	/** Result persistence layer */
	protected final PersistAction<T> pa;

	QueueConsumer(IQueue<T> queue, PersistAction<T> pa) {
		this.queue = queue;
		this.pa = pa;
	}

	protected void persist(T what) {
		try {
			pa.persist(what);
		} catch (DAOException e) {
			log.error(
					"Cannot persist {} - {}",
					what.toString(),
					e.getMessage());
			requeue(what);
		}
	}

	protected void requeue(T what) {
		while (true) {
			try {
				queue.put(what);
				break;
			} catch (InterruptedException e) {
				log.warn("Worker thread interrupted when attempting to requeue {}. Will be reattempting that indefinitely to prevent data loss.");
				continue;
			}
		}
	}
}
