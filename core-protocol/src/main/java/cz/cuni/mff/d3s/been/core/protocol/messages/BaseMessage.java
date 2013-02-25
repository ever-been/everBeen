package cz.cuni.mff.d3s.been.core.protocol.messages;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class BaseMessage implements Serializable {

	public String senderId;

	public String recieverId;

	public BaseMessage(String senderId, String recieverId) {
		this.recieverId = senderId;
		this.recieverId = recieverId;
	}

}
