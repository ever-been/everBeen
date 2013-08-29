package cz.cuni.mff.d3s.been.objectrepository;

import cz.cuni.mff.d3s.been.persistence.SuccessAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LingeringConsumer<T> extends Consumer<T> {

	private static final Logger log = LoggerFactory.getLogger(LingeringConsumer.class);

	protected final Take<T> take;
	protected final Float failRateThreshold;
	protected final Long suspendTimeOnHighFailRate;

	LingeringConsumer(Take<T> take, SuccessAction<T> successAction, FailAction<T> failAction, FailRate failRateMonitor, Float failRateThreshold, Long suspendTimeOnHighFailRate) {
		super(successAction, failAction, failRateMonitor);
		this.take = take;
		this.failRateThreshold = failRateThreshold;
		this.suspendTimeOnHighFailRate = suspendTimeOnHighFailRate;
	}

	@Override
	public void run() {
		try {
			innerRun();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}

	private void innerRun() {
		log.debug("Thread starting.");
		while (!Thread.currentThread().isInterrupted()) {
			try {
				final T item = take.perform();
				if (item == null) {
					// The documentation doesn't say this, but Hazelcast queue yields null
					// rather than throwing an exception when interrupted
					// This throw is not entirely correct, as internally, Hazelcast
					// only uses a big timeout for take(). However, the timeout being
					// Long.MAX_VALUE, this implementation is unlikely to return null in any
					// other circumstances than during interruption.
					throw new InterruptedException("Queue take yielded null item.");
				}
				if (!act(item) && (failRateMonitor.getFailRate() >= failRateThreshold)) {
					Thread.sleep(suspendTimeOnHighFailRate);
				}
			} catch (InterruptedException e) {
				// the take has been interrupted - a signal that this objectrepository is being terminated
				log.warn("Lingering queue consumer interrupted.");
				Thread.currentThread().interrupt();
			}
		}
		log.debug("Thread terminating.");
	}
}
