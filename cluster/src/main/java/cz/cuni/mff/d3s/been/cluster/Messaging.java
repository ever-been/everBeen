package cz.cuni.mff.d3s.been.cluster;


public interface Messaging {

	public void send(String destination, Message message);

	public void addMessageListener(String source, MessageListener listener);

	public void removeMessageListener(String source, MessageListener listener);


}
