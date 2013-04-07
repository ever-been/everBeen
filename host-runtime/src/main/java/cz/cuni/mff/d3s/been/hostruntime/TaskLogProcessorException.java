package cz.cuni.mff.d3s.been.hostruntime;

/**
 * This exception is thrown only by methods of {@link TaskMessageDispatcher}.
 * 
 * @author Tadeáš Palusga
 * 
 */
public class TaskLogProcessorException extends Exception {
	/**
	 * SERIAL VERSION UID
	 */
	private static final long serialVersionUID = 1L;

	private TaskLogProcessorException(String message) {
		//cctor is private because we don't want to allow external instantiation
		super(message);
	}
}