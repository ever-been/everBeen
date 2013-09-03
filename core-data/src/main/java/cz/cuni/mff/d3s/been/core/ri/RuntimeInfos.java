package cz.cuni.mff.d3s.been.core.ri;

/**
 * 
 * Utility functions for {@link RuntimeInfo}
 * 
 * @author Martin Sixta
 */
public class RuntimeInfos {
	/**
	 * 
	 * Returns total system memory in bytes.
	 * 
	 * @param info
	 *          the RuntimeInfo to get the value from
	 * @return total system memory in bytes
	 */
	public static long getTotalMemory(final RuntimeInfo info) {
		return info.getHardware().getMemory().getRam();
	}

	/**
	 * Returns free system memory.
	 * 
	 * @param info
	 *          the RuntimeInfo to get the value from
	 * 
	 * @return free system memory bytes or 0 if free memory cannot be detected
	 */
	public static long getFreeMemory(final RuntimeInfo info) {
		MonitorSample sample = info.getMonitorSample();
		if (sample == null) {
			return 0;
		}
		return sample.getFreeMemory();
	}

	/**
	 * Whether the memory threshold was reached.
	 * 
	 * @param info
	 *          the RuntimeInfo to get the value from
	 * 
	 * @return Whether the memory threshold was reached.
	 */
	public static boolean isMemoryThresholdReached(final RuntimeInfo info) {
		long freeMemory = getFreeMemory(info);

		if (freeMemory == 0) {
			return false;
		}

		long totalMemory = getTotalMemory(info);

		if (totalMemory == 0) {
			return false;
		}

		return (freeMemory / totalMemory) * 100 >= info.getMemoryThreshold();

	}

	/**
	 * Returns whether maximum tasks count was reached.
	 * 
	 * @param info
	 *          the RuntimeInfo to get the value from
	 * @return true if maximum tasks count was reached.
	 */
	public static boolean isMaxTasksReached(final RuntimeInfo info) {
		return (info.getTaskCount() >= info.getMaxTasks());
	}

}
