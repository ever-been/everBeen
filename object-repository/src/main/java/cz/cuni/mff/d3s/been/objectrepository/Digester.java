package cz.cuni.mff.d3s.been.objectrepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cz.cuni.mff.d3s.been.persistence.SuccessAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.Reapable;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.Service;

/**
 * A general shared structure digestion service
 *
 * @param <T> Type of digested items
 */
public class Digester<T> implements Service, Reapable {

	private static final long POOL_SHUTDOWN_TIMEOUT_MILLIS = 3000;
	private static final Logger log = LoggerFactory.getLogger(Digester.class);

	private final Float failRateThreshold;
	private final Long suspendTimeOnHighFailRate;
	private final ExecutorService pool;
	private final SuccessAction<T> successAction;
	private final FailAction<T> failAction;
	private final Take<T> take;
	private final Poll<T> poll;
	private final FailRate failRateMonitor;

	Digester(Take<T> take, Poll<T> poll, SuccessAction<T> successAction, FailAction<T> failAction, Float failRateThreshold, Long suspendTimeOnHighFailRate) {
		this.failRateThreshold = failRateThreshold;
		this.suspendTimeOnHighFailRate = suspendTimeOnHighFailRate;
		this.take = take;
		this.poll = poll;
		this.successAction = successAction;
		this.failAction = failAction;
		this.pool = Executors.newCachedThreadPool();
		this.failRateMonitor = new FailRate();
	}

	/**
	 * Create a digester
	 *
	 * @param take Blocking take action on targeted shared data structure
	 * @param poll Non blocking poll action on targeted shared data structure
	 * @param successAction Action to perform on digested actions
	 * @param failAction Action to perform on digested actions if success action fails
	 * @param failRateThreshold Rate of failure at which this digester suspends its digestion attempts temporarily
	 * @param suspendTimeOnHighFailRate Time (in milliseconds) this digester should suspend for if the fail rate surpasses fail rate threshold
	 * @param <T> Type of digested items
	 *
	 * @return A new digester instance
	 */
	public static <T> Digester<T> create(Take<T> take, Poll<T> poll, SuccessAction<T> successAction,
			FailAction<T> failAction, Float failRateThreshold, Long suspendTimeOnHighFailRate) {
		return new Digester<T>(take, poll, successAction, failAction, failRateThreshold, suspendTimeOnHighFailRate);
	}

	/**
	 * Suggest adding a new ephemerous consumer to this digester's internal thread pool. This doesn't necessarily result in a new thread, because the digester may deem itself unreliable and refuse thread creation.
	 */
	public void addNewWorkingThread() {
		if(failRateMonitor.getFailRate() < failRateThreshold) {
			pool.execute(new EphemerousConsumer<T>(poll, successAction, failAction, failRateMonitor));
		}
	}

	@Override
	public void start() {
		pool.execute(new LingeringConsumer<T>(take, successAction, failAction, failRateMonitor, failRateThreshold, suspendTimeOnHighFailRate));
	}

	@Override
	public void stop() {
		pool.shutdownNow();
		try {
			pool.awaitTermination(POOL_SHUTDOWN_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.error("Queue worker pool forced to terminate. Results may have been lost.");
		}
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
