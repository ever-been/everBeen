package cz.cuni.mff.d3s.been.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class Consumer<T> implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(Consumer.class);

	/** Result persistence layer */
	protected final SuccessAction<T> successAction;
    protected final FailAction<T> failAction;

	Consumer(SuccessAction<T> successAction, FailAction<T> failAction) {
		this.successAction = successAction;
        this.failAction = failAction;
	}

	protected void persist(T what) {
		try {
			successAction.perform(what);
		} catch (DAOException e) {
			log.error(
					"Cannot perform {} - {}",
					what.toString(),
					e.getMessage());
			requeue(what);
		}
	}

	protected void requeue(T what) {
		while (true) {
			try {
				failAction.perform(what);
				break;
			} catch (InterruptedException e) {
				log.warn("Worker thread interrupted when attempting to requeue {}. Will be reattempting that indefinitely to prevent data loss.");
				continue;
			}
		}
	}
}
