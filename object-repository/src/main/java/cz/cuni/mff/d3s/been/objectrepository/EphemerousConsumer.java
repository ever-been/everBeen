package cz.cuni.mff.d3s.been.objectrepository;

import cz.cuni.mff.d3s.been.persistence.SuccessAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An ephemerous consumer thread (it dies once consumable objects run out)
 *
 * @param <T> Type of consumed objects
 */
public class EphemerousConsumer<T> extends Consumer<T> {

	private static final Logger log = LoggerFactory.getLogger(EphemerousConsumer.class);

	protected final Poll<T> poll;

	/**
	 * Create an ephemerous consumer
	 *
	 * @param poll Poll action that gets new consumable objects
	 * @param successAction The action to perform on consumed objects
	 * @param failAction The action to perform on consumed objects in case the success action fails
	 * @param failRateMonitor Monitor counting the rate of failures in executed actions
	 */
	public EphemerousConsumer(Poll<T> poll, SuccessAction<T> successAction, FailAction<T> failAction, FailRate failRateMonitor) {
		super(successAction, failAction, failRateMonitor);
		this.poll = poll;
	}

	@Override
	public void run() {
		try {
			doRun();
		} catch (Throwable t) {
			log.error("Ephemerous consumer thread died.", t);
		}
	}

	private void doRun() throws Throwable {
		log.debug("Thread starting.");
		while (!Thread.currentThread().isInterrupted()) {
			final T item = poll.perform();
			if (item == null) {
				// there is nothing to do, end execution (this thread is ephemerous)
				break;
			} else {
				if (!act(item)) {
					break;
				}
			}
		}
		log.debug("Thread terminating.");
	}
}
