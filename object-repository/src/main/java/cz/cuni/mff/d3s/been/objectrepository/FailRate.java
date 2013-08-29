package cz.cuni.mff.d3s.been.objectrepository;

/**
 * A utility class that helps calculate failure rates of operations. For efficiency's sake, only the last 64 operations are considered.
 *
 * @author darklight
 */
class FailRate {
	/** low bit of the long */
	private static final long HI_BIT = 0x8000000000000000l;
	/** high bit of the long */
	private static final long LO_BIT = 0x01l;
	/** bitmask indicating the success/failure of the last 64 operations */
	long outcomes = 0x00l;

	int successCount = 0;
	int sampleCount = 0;

	/**
	 * Indicate that the action has failed
	 */
	synchronized void fail() {
		rotate();
	}

	/**
	 * Indicate that the action has succeeded
	 */
	synchronized void success() {
		rotate();
		outcomes |= LO_BIT;
		++ successCount;
	}

	/**
	 * Rotate the outcome bit array.
	 */
	private void rotate() {
		if ((outcomes & HI_BIT) != 0) {
			--successCount;
		} else if (sampleCount < 64) {
			++sampleCount; // only increment sampleCount up to 64
		}
		outcomes <<= 1;
	}

	/**
	 * Get the fail rate on the last samples
	 *
	 * @return Fail rate
	 */
	float getFailRate() {
		if (sampleCount == 0) {
			return 0f;
		}
		return 1 - ((float) successCount) / sampleCount;
	}
}
