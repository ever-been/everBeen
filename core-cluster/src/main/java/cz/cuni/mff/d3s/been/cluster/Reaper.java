package cz.cuni.mff.d3s.been.cluster;

import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A special thread whose purpose is to release the resources of an interrupted
 * {@link IClusterService}.
 * 
 * @author darklight
 * 
 */
public abstract class Reaper extends Thread {

	private final Logger log;

	/**
	 * A list of sub-services that need to be reaped in case the node is
	 * terminated.
	 */
	private final Stack<Reapable> subServices;

	public Reaper() {
		super();
		subServices = new Stack<Reapable>();
		log = LoggerFactory.getLogger(getClass());
	}

	@Override
	public final void run() {
		super.run();
		try {
			log.debug("Reaper is on the move.");
			reap();
			log.debug("Reaper has taken his toll.");
		} catch (InterruptedException e) {
			log.warn("Reaper was interrupted during his reaping.", e);
		}
		while (!subServices.isEmpty()) {
			reap(subServices.pop());
		}
		try {
			shutdown();
		} catch (InterruptedException e) {
			log.warn("{} was interrupted during shutdown.", e);
		}
	}

	/**
	 * Execute the reaping steps. The body of this method gets executed before all
	 * pushed targets are reaped.
	 * 
	 * @throws InterruptedException
	 *           When the reaping thread is interrupted
	 */
	protected abstract void reap() throws InterruptedException;

	/**
	 * Execute shutdown steps. The body of this method gets executed after the
	 * reaping is done and all pushed targets are reaped.
	 * 
	 * @throws InterruptedException
	 *           When the reaping thread is interrupted
	 */
	protected void shutdown() throws InterruptedException {};

	private final void reap(Reapable target) {
		final Reaper reaper = target.createReaper();
		reaper.start();
		try {
			reaper.join();
		} catch (InterruptedException e) {}
	}

	public void pushTarget(Reapable target) {
		subServices.push(target);
	}

	@Override
	public String toString() {
		return getClass().toString();
	}
}
