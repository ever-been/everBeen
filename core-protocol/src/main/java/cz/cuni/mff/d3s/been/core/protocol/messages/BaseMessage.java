package cz.cuni.mff.d3s.been.core.protocol.messages;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class BaseMessage implements Serializable {

	public String senderId;

	public String recieverId;

}
