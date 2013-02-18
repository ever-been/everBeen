package cz.cuni.mff.d3s.been.cluster;

/**
 * @author Martin Sixta
 */
public interface IClusterService {

	/**
	 * Commands the service to start listening for events.
	 */
	public void start();

	/**
	 * Commands the service to stop listening for events.
	 */
	public void stop();
}
