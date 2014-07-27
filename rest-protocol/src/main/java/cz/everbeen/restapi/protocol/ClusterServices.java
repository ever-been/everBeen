package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collection;
import java.util.Collections;

/**
 * The description of available cluster services
 *
 * @author darklight
 * @since 7/27/14.
 */
public class ClusterServices implements ProtocolObject {

	@JsonProperty("services")
	private final Collection<ClusterService> services;

	@JsonCreator
	public ClusterServices(@JsonProperty("services") Collection<ClusterService> services) {
		this.services = Collections.unmodifiableCollection(services);
	}

	public Collection<ClusterService> getServices() {
		return services;
	}
}
