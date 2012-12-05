package cz.cuni.mff.d3s.been.core.protocol.messages;

import java.io.Serializable;

/**
 * Parent for all types of messages.
 * @author donarus
 */
@SuppressWarnings("serial")
public abstract class BaseMessage implements Serializable {
	
	/**
	 *  Cluster-unique id of sender node 
	 */
	public String nodeId;
	
}
