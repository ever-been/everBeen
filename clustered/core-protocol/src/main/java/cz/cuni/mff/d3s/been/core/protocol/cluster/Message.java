package cz.cuni.mff.d3s.been.core.protocol.cluster;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Message implements Serializable {

	private String type;

	private String text;

	public Message(String type, String text) {
		this.type = type;
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public String getText() {
		return text;
	}

}
