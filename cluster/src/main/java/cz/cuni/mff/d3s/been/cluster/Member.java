package cz.cuni.mff.d3s.been.cluster;

/**
 *
 *
 * @author Martin Sixta
 */
public interface Member {
	public Messaging getMessaging();
	public DataPersistence getDataPersistence();

	public void connect();

	public void disconnect();

}
