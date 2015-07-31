package cz.everbeen.restapi.handlers;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;
import cz.everbeen.restapi.BeenApiOperation;
import cz.everbeen.restapi.ClusterApiConnection;
import cz.everbeen.restapi.ClusterConnectionException;
import cz.everbeen.restapi.RestApiContextInitializationException;
import cz.everbeen.restapi.protocol.ErrorObject;
import cz.everbeen.restapi.protocol.ProtocolObject;
import cz.everbeen.restapi.protocol.ProtocolObjectSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * A generic base for a Jersey handler.
 *
 * @author darklight
 */
abstract class Handler {
	/** Media type used by for protocol object serialization **/
	protected static final MediaType PROTOCOL_OBJECT_MEDIA_TYPE = MediaType.APPLICATION_JSON_TYPE;
	/** Name of the media type used for protocol object serialization **/
	protected static final String PROTOCOL_OBJECT_MEDIA = MediaType.APPLICATION_JSON;

	private final ProtocolObjectSerializer protocolObjectSerializer = new ProtocolObjectSerializer();
	protected final JSONUtils jsonUtils = JSONUtils.newInstance();

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
				try {
					beenApi = ClusterApiConnection.getInstance().getApi();
				} catch (RestApiContextInitializationException e) {
					throw new ClusterConnectionException("Failed to initialize connection factory", e);
				}
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
			log.info("Executing operation {}", operation.name());
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
	 * Serialize a protocol object. If impossible, preSerialize an error message stating why.
	 * @param protocolObject The protocol object to preSerialize
	 * @return The serialized protocol object, or a serialized error object
	 */
	protected final String serializeProtocolObject(ProtocolObject protocolObject) {
		try {
			return protocolObjectSerializer.serialize(protocolObject);
		} catch (IOException e) {
			return new ErrorObject(e.getMessage()).toString();
		}
	}
}
