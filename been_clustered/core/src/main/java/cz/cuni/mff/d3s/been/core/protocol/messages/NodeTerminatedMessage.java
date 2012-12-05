package cz.cuni.mff.d3s.been.core.protocol.messages;

/**
 * This message is sent when host runtime has been terminated.
 * 
 * @author donarus
 * 
 */
public final class NodeTerminatedMessage extends BaseMessage {

	/** 
	 * Reason for node termination
	 */
	public String reason;
}
