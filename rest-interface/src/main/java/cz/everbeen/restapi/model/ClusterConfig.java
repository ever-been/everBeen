package cz.everbeen.restapi.model;

import org.apache.http.annotation.Immutable;

import java.util.Map;

/**
 * A description the configuration of the everBeen cluster client.
 *
 * @author darklight
 */
@Immutable
public class ClusterConfig {

	public static final String JNDI_NAME = "everBeen/clusterConfig";

	private static final String HOST = "host";
	private static final String PORT = "port";
	private static final String GROUP = "group";
	private static final String PASS = "pass";

	private String host;
	private Integer port;
	private String group;
	private String pass;

	private ClusterConfig(String host, Integer port, String group, String pass) {
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

	@Override
	public String toString() {
		return String.format("{host: %s, port: %d, group: %s, pass: %s}", host, port, group, pass);
	}
}
