package cz.cuni.mff.d3s.been.persistence;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.Reapable;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.Service;

public class Digester<T> implements Service, Reapable {

	private static final long POOL_SHUTDOWN_TIMEOUT_MILLIS = 3000;
	private static final Logger log = LoggerFactory.getLogger(Digester.class);

	private final ExecutorService pool;
	private final SuccessAction<T> successAction;
    private final FailAction<T> failAction;
    private final Take<T> take;
    private final Poll<T> poll;

	Digester(Take<T> take, Poll<T> poll, SuccessAction<T> successAction, FailAction<T> failAction) {
        this.take = take;
        this.poll = poll;
		this.successAction = successAction;
        this.failAction = failAction;
		pool = Executors.newCachedThreadPool();
	}

	public static <T> Digester<T> create(Take<T> take, Poll<T> poll, SuccessAction<T> successAction, FailAction failAction) {
		return new Digester<T>(take, poll, successAction, failAction);
	}

	public void addNewWorkingThread() {
		pool.execute(new EphemerousConsumer<T>(poll, successAction, failAction));
	}

	@Override
	public void start() {
		pool.execute(new LingeringConsumer<T>(take, successAction, failAction));
	}

	@Override
	public void stop() {
		log.debug("Result queue digester stopping...");
		pool.shutdownNow();
		try {
			pool.awaitTermination(POOL_SHUTDOWN_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.error("Result queue worker pool forced to terminate. Results may have been lost.");
		}
		log.debug("Result queue digester stopped.");
	}

	@Override
	public Reaper createReaper() {
		return new Reaper() {
			@Override
			protected void reap() throws InterruptedException {
				Digester.this.stop();
			}
		};
	}

}
