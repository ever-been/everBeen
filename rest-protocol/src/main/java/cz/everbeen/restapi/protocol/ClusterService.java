package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

/**
 * The description of a cluster service
 *
 * @author darklight
 * @since 7/27/14.
 */
public class ClusterService implements ProtocolObject {

	@JsonProperty("name")
	private final String name;
	@JsonProperty("state")
	private final String state;
	@JsonProperty("stateReason")
	private final String stateReason;
	@JsonProperty("info")
	private final String info;
	@JsonProperty("params")
	private final Map<String, Object> params;

	@JsonCreator
	public ClusterService(
		@JsonProperty("name") String name,
		@JsonProperty("state") String state,
		@JsonProperty("stateReason") String stateReason,
		@JsonProperty("info") String info,
		@JsonProperty("params") Map<String, Object> params
	) {
		this.name = name;
		this.state = state;
		this.stateReason = stateReason;
		this.info = info;
		this.params = params;
	}

	public String getName() {
		return name;
	}

	public String getState() {
		return state;
	}

	public String getStateReason() {
		return stateReason;
	}

	public String getInfo() {
		return info;
	}

	public Map<String, Object> getParams() {
		return params;
	}
}
