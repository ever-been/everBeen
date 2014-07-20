package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collection;
import java.util.Collections;

/**
 * List of BEEN cluster members
 * @author darklight
 * @since 7/20/14.
 */
public class ClusterMembers implements ProtocolObject {

	@JsonProperty("members")
	private final Collection<ClusterMember> members;

	@JsonCreator
	public ClusterMembers(
		@JsonProperty("members") Collection<ClusterMember> members
	) {
		this.members = Collections.unmodifiableCollection(members);
	}

	public Collection<ClusterMember> getMembers() {
		return members;
	}
}
