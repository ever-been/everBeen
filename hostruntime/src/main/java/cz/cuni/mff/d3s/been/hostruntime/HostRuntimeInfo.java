package cz.cuni.mff.d3s.been.hostruntime;

import java.io.Serializable;
import java.net.InetSocketAddress;

/**
 * @author Martin Sixta
 */
public class HostRuntimeInfo implements Serializable {

	private InetSocketAddress inetSocketAddress;
	private String uuid;
	private String name;

	HostRuntimeInfo(String name, InetSocketAddress inetSocketAddress, String uuid) {
		this.inetSocketAddress = inetSocketAddress;
		this.uuid = uuid;
		this.name = name;
	}



	public String getUuid() {
		return uuid;
	}

	public InetSocketAddress getInetSocketAddress() {
		return inetSocketAddress;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "[" + name + ", " + inetSocketAddress + ", " + uuid + "]";
	}
}
