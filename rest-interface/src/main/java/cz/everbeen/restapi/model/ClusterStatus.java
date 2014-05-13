package cz.everbeen.restapi.model;

/**
 * @author darklight
 */
public class ClusterStatus {

	private final boolean connected;

	private ClusterStatus(boolean connected) {
		this.connected = connected;
	}

	public static ClusterStatus withFlags(boolean connected) {
		return new ClusterStatus(connected);
	}

	@Override
	public String toString() {
		return connected ? "{connected: true}" : "{connected: false}";
	}
}
