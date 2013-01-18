package cz.cuni.mff.d3s.been.task;

/**
 * @author Martin Sixta
 */
public interface IManager {
	/**
	 * Commands the task manager to start listening for events.
	 */
	public void start();

	/**
	 * Commands the task manager to stop listening for events.
	 */
	public void stop();
}
