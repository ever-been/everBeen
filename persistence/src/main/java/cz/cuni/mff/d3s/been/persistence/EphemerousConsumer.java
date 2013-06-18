package cz.cuni.mff.d3s.been.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EphemerousConsumer<T> extends Consumer<T> {

	private static final Logger log = LoggerFactory.getLogger(EphemerousConsumer.class);

    protected final Poll<T> poll;

	public EphemerousConsumer(
            Poll<T> poll,
            PersistAction<T> persistAction, FailAction<T> failAction) {
		super(persistAction, failAction);
        this.poll = poll;
	}

	@Override
	public void run() {
		log.debug("Thread starting.");
		while (!Thread.currentThread().isInterrupted()) {
			final T item = poll.perform();
			if (item == null) {
				// there is nothing to do, end execution (this thread is ephemerous) 
				Thread.currentThread().interrupt();
			}
			log.debug("Taken item {} from the queue.", item);
			persist(item);
		}
		log.debug("Thread terminating.");
	}
}
