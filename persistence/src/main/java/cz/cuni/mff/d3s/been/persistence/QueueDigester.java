package cz.cuni.mff.d3s.been.persistence;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IQueue;

import cz.cuni.mff.d3s.been.cluster.Reapable;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.Service;

public class QueueDigester<T> implements Service, Reapable {

	private static final long POOL_SHUTDOWN_TIMEOUT_MILLIS = 3000;
	private static final Logger log = LoggerFactory.getLogger(QueueDigester.class);

	private final ExecutorService pool;
	private final IQueue<T> resQueue;
	private final PersistAction<T> pa;
	private final QueueItemCounterListener<T> payloadListener;

	QueueDigester(IQueue<T> resQueue, PersistAction<T> pa) {
		this.resQueue = resQueue;
		this.pa = pa;
		this.payloadListener = new QueueItemCounterListener<T>(this);
		pool = Executors.newCachedThreadPool();
	}

	public static
			<T>
			QueueDigester<T>
			create(IQueue<T> queue, PersistAction<T> pa) {
		return new QueueDigester<>(queue, pa);
	}

	public void addNewWorkingThread() {
		pool.execute(new EphemerousQueueConsumer<T>(resQueue, pa));
	}

	@Override
	public void start() {
		resQueue.addItemListener(payloadListener, false);
		pool.execute(new LingeringQueueConsumer<T>(resQueue, pa));
	}

	@Override
	public void stop() {
		log.debug("Result queue digester stopping...");
		resQueue.removeItemListener(payloadListener);
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
				QueueDigester.this.stop();
			}
		};
	}

}
