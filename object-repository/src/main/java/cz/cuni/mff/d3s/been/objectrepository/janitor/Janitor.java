package cz.cuni.mff.d3s.been.objectrepository.janitor;

import static cz.cuni.mff.d3s.been.objectrepository.janitor.CleanupType.*;
import static cz.cuni.mff.d3s.been.objectrepository.janitor.PersistenceJanitorConfiguration.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.util.PropertyReader;
import cz.cuni.mff.d3s.been.persistence.task.PersistentContextState;
import cz.cuni.mff.d3s.been.persistence.task.PersistentTaskState;
import cz.cuni.mff.d3s.been.storage.Storage;

/**
 * A keeper thread that runs persistence cleanup every once in a while
 * 
 * @author darklight
 */
public class Janitor extends Thread {

	private static final Logger log = LoggerFactory.getLogger(Janitor.class);

	private final Storage storage;
	private final TrashSeeker seeker;
	private final TrashProcessor processor;
	private final TrashDumper dumper;
	private final Queue<CleanupType> cleanupRotation;

	private final Long failedLongevity;
	private final Long finishedLongevity;
	private final Long serviceLogLongevity;
	private final Long loadSampleLongevity;
	private final Long wakeUpInterval;

	private Janitor(Storage storage, Long failedLongevity, Long finishedLongevity, Long serviceLogLongevity, Long loadSampleLongevity, Long wakeUpInterval) {
		this.storage = storage;
		this.seeker = new TrashSeeker(storage);
		this.processor = new TrashProcessor();
		this.dumper = new TrashDumper(storage);
		this.cleanupRotation = new LinkedList<CleanupType>();

		this.failedLongevity = failedLongevity;
		this.finishedLongevity = finishedLongevity;
		this.wakeUpInterval = wakeUpInterval;
		this.serviceLogLongevity = serviceLogLongevity;
		this.loadSampleLongevity = loadSampleLongevity;
	}

	/**
	 * Create a persistence janitor over a {@link Storage} instance
	 * 
	 * @param ctx
	 *          Context the janitor runs in
	 * @param storage
	 *          Storage to keep clean
	 * 
	 * @return A new Janitor instance
	 */
	public static Janitor create(ClusterContext ctx, Storage storage) {
		final PropertyReader propertyReader = PropertyReader.on(ctx.getProperties());

		final Long failedLongevity = TimeUnit.HOURS.toMillis(propertyReader.getLong(
				FAILED_LONGEVITY,
				DEFAULT_FAILED_LONGEVITY));
		final Long finishedLongevity = TimeUnit.HOURS.toMillis(propertyReader.getLong(
				FINISHED_LONGEVITY,
				DEFAULT_FINISHED_LONGEVITY));
		final Long serviceLogsLongevity = TimeUnit.HOURS.toMillis(propertyReader.getLong(
				SERVICE_LOGS_LONGEVITY,
				DEFAULT_SERVICE_LOGS_LONGEVITY));
		final Long loadSampleLongevity = TimeUnit.HOURS.toMillis(propertyReader.getLong(
				LOAD_SAMPLE_LONGEVITY, DEFAULT_LOAD_SAMPLE_LONGEVITY));
		final Long wakeUpInterval = TimeUnit.MINUTES.toMillis(propertyReader.getLong(
				WAKEUP_INTERVAL,
				DEFAULT_WAKEUP_INTERVAL));

		return new Janitor(storage, failedLongevity, finishedLongevity, serviceLogsLongevity, loadSampleLongevity, wakeUpInterval);
	}

	@Override
	public void run() {
		try {
			doRun();
		} catch (Throwable t) {
			log.error("Unknown error in janitor service", t);
		}
	}

	private void doRun() throws Throwable {
		log.debug("Starting persistence janitor component");

		cleanupRotation.add(CONTEXT_FAILED);
		cleanupRotation.add(CONTEXT_FINISHED);
		cleanupRotation.add(CONTEXT_ZOMBIE);
		cleanupRotation.add(TASK_FAILED);
		cleanupRotation.add(TASK_FINISHED);
		cleanupRotation.add(TASK_ZOMBIE);
		cleanupRotation.add(SERVICE_LOGS);
		if (loadSampleLongevity > 0l) {
			cleanupRotation.add(LOAD_SAMPLES);
		}

		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(wakeUpInterval);
			} catch (InterruptedException e) {
				// this is the standard way of exiting
				break;
			}

			if (!storage.isIdle() || !storage.isConnected()) {
				log.debug("Storage is busy or disconnected, will try again in {} minutes", wakeUpInterval);
				continue;
			}

			doSweep();
		}
		log.debug("Exiting persistence janitor component");
	}

	private void doSweep() {
		final Long now = System.currentTimeMillis();
		final Long failedDeathDay = now - failedLongevity;
		final Long finishedDeathDay = now - finishedLongevity;
		final Long serviceLogDeathDay = now - serviceLogLongevity;
		final Long loadSampleDeathDay = now - loadSampleLongevity;
		log.debug("Commencing janitor sweep, will attempt to clean failed items past {} and service info of finished items past {}", new Date(failedDeathDay).toString(), new Date(finishedDeathDay).toString());

		final boolean didSomething = processCtxOutcome(processor.getNextContext()) ||
				processTaskOutcome(processor.getNextTask()) ||
				cleanServiceLogs(serviceLogDeathDay) ||
				cleanLoadSamples(loadSampleDeathDay);
		if (!didSomething) {
			loadMore(failedDeathDay, finishedDeathDay);
		}

		log.debug("Janitor sweep done");
	}

	private void loadMore(Long failedDeathDay, Long finishedDeathDay) {

		// get the next thing to cleanup and put it back at the end of the queue
		final CleanupType cleanupType = cleanupRotation.poll();
		cleanupRotation.add(cleanupType);

		switch (cleanupType) {
			case CONTEXT_FAILED:
				processor.addContextStates(seeker.getFailedContextsPastDue(failedDeathDay));
				break;
			case CONTEXT_FINISHED:
				processor.addContextStates(seeker.getFinishedContextsPastDue(finishedDeathDay));
				break;
			case CONTEXT_ZOMBIE:
				processor.addContextStates(seeker.getStartedContextsPastDue(failedDeathDay));
				break;
			case TASK_FAILED:
				processor.addTaskStates(seeker.getFailedTasksPastDue(failedDeathDay));
				processor.addTaskStates(seeker.getFailedBenchmarksPastDue(failedDeathDay));
				break;
			case TASK_FINISHED:
				processor.addTaskStates(seeker.getFinishedTasksPastDue(finishedDeathDay));
				processor.addTaskStates(seeker.getFinishedBenchmarksPastDue(finishedDeathDay));
				break;
			case TASK_ZOMBIE:
				processor.addTaskStates(seeker.getStartedTasksPastDue(failedDeathDay));
				processor.addTaskStates(seeker.getStartedBenchmarksPastDue(failedDeathDay));
				break;
			default:
				log.warn("Unsupported cleanup type {} - won't do anything on this cycle", cleanupType.name());
		}
	}

	private boolean processCtxOutcome(TotalOutcome<PersistentContextState> outcome) {
		if (outcome == null) {
			return false;
		}

		if (outcome.isZombie() || outcome.isFailed()) {
			dumper.cleanupAfterFailedContext(outcome.getEventId());
		} else {
			dumper.cleanupAfterFinishedContext(outcome.getEventId());
		}

		return true;
	}

	private boolean processTaskOutcome(TotalOutcome<PersistentTaskState> outcome) {
		if (outcome == null) {
			return false;
		}

		if (outcome.isZombie() || outcome.isFailed()) {
			dumper.cleanupAfterFailedTask(outcome.getEventId());
		} else {
			dumper.cleanupAfterFinishedTask(outcome.getEventId());
		}

		return true;
	}

	private boolean cleanServiceLogs(Long olderThan) {
		if (SERVICE_LOGS.equals(cleanupRotation.peek())) {
			dumper.cleanupServiceLogs(olderThan);
			cleanupRotation.add(cleanupRotation.poll());
			return true;
		} else {
			return false;
		}
	}

	private boolean cleanLoadSamples(Long olderThan) {
		if (LOAD_SAMPLES.equals(cleanupRotation.peek())) {
			dumper.cleanupLoadSamples(olderThan);
			cleanupRotation.add(cleanupRotation.poll());
			return true;
		} else {
			return false;
		}
	}
}
