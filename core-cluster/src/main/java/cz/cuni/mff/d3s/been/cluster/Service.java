package cz.cuni.mff.d3s.been.cluster;

public interface Service {

	/**
	 * Commands the service to start listening for events.
	 * 
	 * @throws ServiceException
	 *           When service bootstrap fails
	 */
	public void start() throws ServiceException;

	/**
	 * Commands the service to stop listening for events.
	 */
	public void stop();
}
