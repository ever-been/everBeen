package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Current runtime status of the cluster.
 *
 * @author darklight
 */
public class ClusterStatus implements ProtocolObject {

	@JsonProperty("connected")
	private final boolean connected;
	@JsonProperty("error")
	private final String error;

	@JsonCreator
	public ClusterStatus(@JsonProperty("connected") boolean connected) {
		this.connected = connected;
		this.error = null;
	}

	@JsonCreator
	public ClusterStatus(
		@JsonProperty("connected") boolean connected,
		@JsonProperty("error") String error) {
		this.connected = connected;
		this.error = error;
	}

	public static ClusterStatus withFlags(boolean connected) {
		return new ClusterStatus(connected);
	}

	public static ClusterStatus withError(Throwable error) {
		return new ClusterStatus(false, error.getMessage());
	}
}
