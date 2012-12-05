package cz.cuni.mff.d3s.been.core.protocol.cluster;

/**
 *
 *
 * @author Martin Sixta
 */
public interface Member {
	
	public Messaging getMessaging();
	
	public DataPersistence getDataPersistence();

	public void connect() throws Exception;

	public void disconnect();

	public String getNodeId();

}
