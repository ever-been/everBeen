package cz.cuni.mff.d3s.been.cluster.cshell;

/**
 * @author Martin Sixta
 */

import cz.mff.dpp.args.Option;

import java.net.InetSocketAddress;

class HostInfo {
	@Option(name = "-h", aliases = {"--host"}, defaultValues = {"localhost"}, required = true)
	private String host = "localhost";

	@Option(name = "-p", aliases = {"--port"}, defaultValues = {"5701"}, required = true)
	private int port = 5701;

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public InetSocketAddress getInetSocketAddress() {
		return new InetSocketAddress(getHost(), getPort());
	}
}
