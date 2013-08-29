package cz.cuni.mff.d3s.been.mq;

/**
 * A range of port numbers
 *
 * @author darklight
 */
class PortRange {
	private final int from;
	private final int to;

	PortRange(int from, int to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Get the lower bound of the range
	 *
	 * @return Lower bound
	 */
	int getFrom() {
		return from;
	}

	/**
	 * Get the higher bound of the range
	 *
	 * @return Upper bound
	 */
	int getTo() {
		return to;
	}
}
