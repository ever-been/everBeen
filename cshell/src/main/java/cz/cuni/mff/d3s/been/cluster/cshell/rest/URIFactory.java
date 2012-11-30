package cz.cuni.mff.d3s.been.cluster.cshell.rest;

import cz.mff.dpp.args.Option;

public class URIFactory {

	@Option(name = "-h", aliases = {"--host"}, defaultValues = {"localhost"})
	private String host = "localhost";

	@Option(name = "-p", aliases = {"--port"}, defaultValues = {"5701"})
	private int port = 5701;


	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	String getBase() {
		return "http://" + host + ":" + Integer.toString(port) + "/hazelcast/rest";
	}



	String getMap(String mapName) {
		return getBase() + "/maps/" + mapName;
	}

	String getMapKey(String mapName, String keyName) {
		return getMap(mapName) + "/" + keyName;
	}

	String getQueue(String queueName) {
		return getBase() + "/queues/" + queueName;
	}

	String getQueuePoll(String queueName, int pollTime) {
		return getQueue(queueName) + "/" + Integer.toString(pollTime);
	}




}
