package cz.cuni.mff.d3s.been.repository.janitor;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.PropertyReader;
import cz.cuni.mff.d3s.been.persistence.task.PersistentContextState;
import cz.cuni.mff.d3s.been.persistence.task.PersistentTaskState;
import cz.cuni.mff.d3s.been.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static cz.cuni.mff.d3s.been.repository.janitor.PersistenceJanitorConfiguration.*;

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
	private final TrashDumper executor;

	private final Long failedLongevity;
	private final Long finishedLongevity;
	private final Long wakeUpInterval;

	private Janitor(Storage storage, Long failedLongevity, Long finishedLongevity, Long wakeUpInterval) {
		this.storage = storage;
		this.seeker = new TrashSeeker(storage);
		this.processor = new TrashProcessor();
		this.executor = new TrashDumper(storage);

		this.failedLongevity = failedLongevity;
		this.finishedLongevity = finishedLongevity;
		this.wakeUpInterval = wakeUpInterval;
	}

	/**
	 * Create a persistence janitor over a {@link Storage} instance
	 *
	 * @param ctx Context the janitor runs in
	 * @param storage Storage to keep clean
	 *
	 * @return A new Janitor instance
	 */
	public static Janitor create(ClusterContext ctx, Storage storage) {
		final PropertyReader propertyReader = PropertyReader.on(ctx.getProperties());

		final Long failedLongevity = TimeUnit.HOURS.toMillis(propertyReader.getLong(FAILED_LONGEVITY, DEFAULT_FAILED_LONGEVITY));
		final Long finishedLongevity = TimeUnit.HOURS.toMillis(propertyReader.getLong(FINISHED_LONGEVITY, DEFAULT_FINISHED_LONGEVITY));
		final Long wakeUpInterval = TimeUnit.SECONDS.toMillis(propertyReader.getLong(WAKEUP_INTERVAL, DEFAULT_WAKEUP_INTERVAL));

		return new Janitor(storage, failedLongevity, finishedLongevity, wakeUpInterval);
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
		log.info("Starting persistence janitor component");
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(wakeUpInterval);
			} catch (InterruptedException e) {
				// this is the standard way of exiting
				break;
			}

			if (!storage.isIdle() || !storage.isConnected()) {
				log.debug("Storage is busy or disconnected, will try again in {} seconds", wakeUpInterval);
				continue;
			}

			log.info("Commencing janitor sweep");
			doSweep();
			log.info("Janitor sweep done");
		}
		log.info("Exiting persistence janitor component");
	}

	private void doSweep() {
		final Long failedDeathDay = System.currentTimeMillis() - failedLongevity;
		final Long finishedDeathDay = System.currentTimeMillis() - finishedLongevity;
		if (tryCleanContexts(failedDeathDay, finishedDeathDay)) {
			return;
		}
		tryCleanTasks(failedDeathDay, finishedDeathDay);
	}

	private boolean tryCleanContexts(Long failedDeathDay, Long finishedDeathDay) {
		TotalOutcome<PersistentContextState> outcome;

		outcome = processor.getNextContext();
		if (outcome != null) {
			processCtxOutcome(outcome);
			return true;
		}

		processor.addContextStates(seeker.getFailedContextsPastDue(failedDeathDay));
		outcome = processor.getNextContext();
		if (outcome != null) {
			processCtxOutcome(outcome);
			return true;
		}

		processor.addContextStates(seeker.getFinishedContextsPastDue(finishedDeathDay));
		outcome = processor.getNextContext();
		if (outcome != null) {
			processCtxOutcome(outcome);
			return true;
		}

		processor.addContextStates(seeker.getStartedContextsPastDue(failedDeathDay));
		outcome = processor.getNextContext();
		if (outcome != null) {
			processCtxOutcome(outcome);
			return true;
		}

		return false;
	}

	private void processCtxOutcome(TotalOutcome<PersistentContextState> outcome) {
		if (outcome.isZombie() || outcome.isFailed()) {
			executor.cleanupAfterFailedContext(outcome.getEventId());
		} else {
			executor.cleanupAfterFinishedContext(outcome.getEventId());
		}
	}

	private boolean tryCleanTasks(Long failedDeathDay, Long finishedDeathDay) {
		TotalOutcome<PersistentTaskState> outcome;

		outcome = processor.getNextTask();
		if (outcome != null) {
			processTaskOutcome(outcome);
			return true;
		}

		processor.addTaskStates(seeker.getFailedTasksPastDue(failedDeathDay));
		outcome = processor.getNextTask();
		if (outcome != null) {
			processTaskOutcome(outcome);
			return true;
		}

		processor.addTaskStates(seeker.getFinishedTasksPastDue(finishedDeathDay));
		outcome = processor.getNextTask();
		if (outcome != null) {
			processTaskOutcome(outcome);
			return true;
		}

		processor.addTaskStates(seeker.getStartedTasksPastDue(failedDeathDay));
		outcome = processor.getNextTask();
		if (outcome != null) {
			processTaskOutcome(outcome);
			return true;
		}

		processor.addTaskStates(seeker.getFailedBenchmarksPastDue(failedDeathDay));
		outcome = processor.getNextTask();
		if (outcome != null) {
			processTaskOutcome(outcome);
			return true;
		}

		processor.addTaskStates(seeker.getFinishedBenchmarksPastDue(finishedDeathDay));
		outcome = processor.getNextTask();
		if (outcome != null) {
			processTaskOutcome(outcome);
			return true;
		}

		processor.addTaskStates(seeker.getStartedBenchmarksPastDue(failedDeathDay));
		outcome = processor.getNextTask();
		if (outcome != null) {
			processTaskOutcome(outcome);
			return true;
		}

		return false;
	}

	private void processTaskOutcome(TotalOutcome<PersistentTaskState> outcome) {
		if (outcome.isZombie() || outcome.isFailed()) {
			executor.cleanupAfterFailedTask(outcome.getEventId());
		} else {
			executor.cleanupAfterFinishedTask(outcome.getEventId());
		}
	}
}
