package cz.cuni.mff.d3s.been.taskapi.mq;

/**
 * 
 * Interface for messaging system used by tasks to communicate with Host
 * Runtimes.
 * 
 * 
 * 
 * @author Martin Sixta
 */
public interface Messaging {

	/**
	 * Sends a String message to the Host Runtime.
	 * 
	 * @param msg
	 */
	public void send(String msg);

}
