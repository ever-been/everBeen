package cz.cuni.mff.d3s.been.objectrepository;

import cz.cuni.mff.d3s.been.persistence.SuccessAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class Consumer<T> implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(Consumer.class);

	/** Result persistence layer */
	protected final SuccessAction<T> successAction;
	protected final FailAction<T> failAction;
	protected final FailRate failRateMonitor;

	Consumer(SuccessAction<T> successAction, FailAction<T> failAction, FailRate failRateMonitor) {
		this.successAction = successAction;
		this.failAction = failAction;
		this.failRateMonitor = failRateMonitor;
	}

	/**
	 * Perform actions on the retrieved item
	 *
	 * @param what Retrieved item
	 *
	 * @return <code>true</code> if the action was successful, <code>false</code> if not
	 */
	protected boolean act(T what) {
		try {
			successAction.perform(what);
			failRateMonitor.success();
			return true;
		} catch (Exception e) {
			actOnFailure(what);
			failRateMonitor.fail();
			return false;
		}
	}

	protected void actOnFailure(T what) {
		failAction.perform(what);
	}
}
