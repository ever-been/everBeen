package cz.cuni.mff.d3s.been.core.protocol.messages;

/**
 * This message is sent when some error occurs somewhere in BEEN cluster (!NOT
 * IN TASK!).
 * 
 * @author donarus
 * 
 */
@SuppressWarnings("serial")
public final class ErrorMessage extends BaseMessage {

	/**
	 * Error message
	 */
	public String message;

}
