package cz.cuni.mff.d3s.been.repository;

import cz.cuni.mff.d3s.been.persistence.SuccessAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EphemerousConsumer<T> extends Consumer<T> {

	private static final Logger log = LoggerFactory.getLogger(EphemerousConsumer.class);

	protected final Poll<T> poll;

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
