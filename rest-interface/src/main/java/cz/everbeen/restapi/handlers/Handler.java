package cz.everbeen.restapi.handlers;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.everbeen.restapi.BeenApiOperation;
import cz.everbeen.restapi.ClusterApiConnection;
import cz.everbeen.restapi.ClusterConnectionException;
import cz.everbeen.restapi.protocol.ErrorObject;
import cz.everbeen.restapi.protocol.ProtocolObject;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A generic base for a Jersey handler.
 *
 * @author darklight
 */
abstract class Handler {
	private final ObjectMapper omap = new ObjectMapper();

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private Object apiLock = new Object();
	private BeenApi beenApi;

	/**
	 * Load the everBeen API interface
	 * @return The everBeen API
	 */
	private BeenApi getBeenApi() throws ClusterConnectionException {
		synchronized (apiLock) {
			if (beenApi == null) {
				beenApi = ClusterApiConnection.getInstance().getApi();
			}
			if (!beenApi.isConnected()) {
				beenApi = null;
				throw new ClusterConnectionException("Connection to cluster lost");
			}
		}
		return beenApi;
	}

	/**
	 * Perform a BEEN API operation, return yielded outcome. If operation fails, use default value.
	 * @param operation The operation to use
	 * @param <T> The type of the operation's outcome
	 * @return The operation's outcome, if it succeeds, or its fallback value, if it fails.
	 */
	protected final <T> T perform(BeenApiOperation<T> operation) {
		try {
			return operation.perform(getBeenApi());
		} catch (BeenApiException e) {
			log.error("Failed to perform operation '{}'", operation.name(), e);
			return operation.fallbackValue(e);
		} catch (ClusterConnectionException e) {
			log.error("Failed to connect to everBeen cluster", e);
			return operation.fallbackValue(e);
		} catch (Throwable t) {
			log.error("Unknown error", t);
			return operation.fallbackValue(t);
		}
	}

	/**
	 * Perform a BEEN API operation, return the JSON representation of the outcome protocol object. If operation fails, use serialized default value.
	 * @param operation The operation to use
	 * @param <PO> The type of the operation's outcome
	 * @return The operation's outcome, if it succeeds, or its fallback value, if it fails.
	 */
	protected final <PO extends ProtocolObject> String performAndAnswer(BeenApiOperation<PO> operation) {
		return serializeProtocolObject(perform(operation));
	}

	/**
	 * Serialized a {@link cz.everbeen.restapi.protocol.ProtocolObject} into JSON
	 * @param protocolObject The protocol object
	 * @return The JSON representation of the protocol object
	 */
	private final String serializeProtocolObject(ProtocolObject protocolObject) {
		try {
			return omap.writeValueAsString(protocolObject);
		} catch (IOException e) {
			return new ErrorObject(e.getMessage()).toString();
		}
	}
}
