package cz.everbeen.restapi;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiFactory;
import cz.everbeen.restapi.model.ClusterConfig;
import cz.everbeen.restapi.model.ClusterStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * A factory capable of instantiating cluster connection and returning a {@link cz.cuni.mff.d3s.been.cluster.context.ClusterContext}
 *
 * @author darklight
 */
public class ClusterApiConnection {
	private static final Logger log = LoggerFactory.getLogger(ClusterApiConnection.class);

	private final ClusterConfig config;

	private static ClusterApiConnection instance;
	private BeenApi api;

	private ClusterApiConnection(ClusterConfig config) {
		this.config = config;
	}

	/**
	 * Get the {@link cz.cuni.mff.d3s.been.api.BeenApi} instance. Allocate it if necessary.
	 * @return The {@link cz.cuni.mff.d3s.been.api.BeenApi} instance
	 */
	public synchronized BeenApi getApi() {
		if (api == null) api = BeenApiFactory.connect(config.getHost(), Integer.valueOf(config.getPort()), config.getGroup(), config.getPass());
		return api;
	}

	/**
	 * Get the instance of this factory from the JNDI context
	 * @return The factory instance
	 */
	public static synchronized ClusterApiConnection getInstance() {
		if (instance == null) {
			try {
				final Context ic = new InitialContext();
				final Context appc = (Context) ic.lookup("java:comp/env");
				final ClusterConfig cconf = (ClusterConfig) appc.lookup(ClusterConfig.JNDI_NAME);
				instance = new ClusterApiConnection(cconf);
			} catch (NamingException e) {
					throw new RestApiContextInitializationException("Failed to lookup the cluster api factory from the JNDI context.", e);
			}
		}
		return instance;
	}

	/**
	 * Get the cluster connection configuration
	 * @return The config object
	 */
	public ClusterConfig getConfig() {
		return config;
	}

	public ClusterStatus getStatus() {
		try {
			return ClusterStatus.withFlags(getApi().isConnected());
		} catch (IllegalStateException ise) {
			log.info("Failed to connect to cluster", ise);
			return ClusterStatus.withError(ise);
		}
	}
}
