package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

/**
 * A description the configuration of the everBeen cluster client.
 *
 * @author darklight
 */
public class ClusterConfig implements ProtocolObject {

	public static final String JNDI_NAME = "everBeen/clusterConfig";

	private static final String HOST = "host";
	private static final String PORT = "port";
	private static final String GROUP = "group";
	private static final String PASS = "pass";

	@JsonProperty("host")
	private String host;
	@JsonProperty("port")
	private Integer port;
	@JsonProperty("group")
	private String group;
	@JsonProperty("pass")
	private String pass;

	@JsonCreator
	public ClusterConfig(
		@JsonProperty("host") String host,
		@JsonProperty("port") Integer port,
		@JsonProperty("group") String group,
		@JsonProperty("pass") String pass)
	{
		this.host = host;
		this.port = port;
		this.group = group;
		this.pass = pass;
	}

	/**
	 * Load the properties of the cluster config from a String map.
	 * @param bindings Properties (key/value)
	 * @return The configuration object, initialized with values from the map
	 */
	public static ClusterConfig load(Map<String, String> bindings) {
		return new ClusterConfig(bindings.get(HOST), (bindings.get(PORT) == null) ? null : Integer.valueOf(bindings.get(PORT)), bindings.get(GROUP), bindings.get(PASS));
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getGroup() {
		return group;
	}

	public String getPass() {
		return pass;
	}

}
