package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The description of a single cluster member
 *
 * @author darklight
 * @since 7/20/14.
 */
public class ClusterMember implements ProtocolObject {

	@JsonProperty("uuid")
	private final String uuid;

	@JsonProperty("sockAddr")
	private final String socketAddress;

	@JsonProperty("lite")
	private final boolean liteMember;

	@JsonCreator
	public ClusterMember(
		@JsonProperty("uuid") String uuid,
		@JsonProperty("sockAddr") String socketAddress,
		@JsonProperty("lite") boolean liteMember
	) {
		this.uuid = uuid;
		this.socketAddress = socketAddress;
		this.liteMember = liteMember;
	}

	public String getUuid() {
		return uuid;
	}

	public String getSocketAddress() {
		return socketAddress;
	}

	public boolean isLiteMember() {
		return liteMember;
	}
}
