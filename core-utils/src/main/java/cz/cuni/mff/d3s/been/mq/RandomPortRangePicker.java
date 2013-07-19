package cz.cuni.mff.d3s.been.mq;

import java.util.Random;

/**
 * Provides a randomized port range to select from.
 *
 * The utility of this class resides in repeated bindings of the same port. At times, a port gets bound, unbound, and then bound again. It may occur that 0MQ already knows that the port it's been using is free, but re-binding fails. It would seem that the kernel is not yet aware of the port being freed and therefore denies access. Picking a random port range to let 0MQ select from should minimize the probability of this anomaly.
 *
 * @author darklight
 */
class RandomPortRangePicker {
	private static final int PORT_SCATTER = 200;
	private static final int MIN_PORT = 1025;
	private static final int MAX_PORT = 65535;

	private static final Random random = new Random();

	/**
	 * Get a random port range to choose from. Not all of these ports are necessarily free - use this as a randomization helper, not allocation heuristics
	 *
	 * @return A random port range
	 */
	static PortRange getRange() {
		final int lo = random.nextInt(MAX_PORT - MIN_PORT - PORT_SCATTER) + MIN_PORT;
		final int hi = lo + PORT_SCATTER;
		return new PortRange(lo, hi);
	}

}
