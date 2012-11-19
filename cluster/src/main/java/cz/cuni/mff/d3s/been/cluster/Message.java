package cz.cuni.mff.d3s.been.cluster;


import java.io.Serializable;

public class Message implements Serializable {

	private int type;

	private String text;


	public Message(int type, String text) {
		this.type = type;
		this.text = text;
	}

	public int getType() {
		return type;
	}

	public String getText() {
		return text;
	}

}
