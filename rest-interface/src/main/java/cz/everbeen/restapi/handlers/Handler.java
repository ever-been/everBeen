package cz.everbeen.restapi.handlers;

import cz.everbeen.restapi.protocol.ErrorObject;
import cz.everbeen.restapi.protocol.ProtocolObject;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * A generic base for a Jersey handler.
 *
 * @author darklight
 */
abstract class Handler {
	private final ObjectMapper omap = new ObjectMapper();

	/**
	 * Serialized a {@link cz.everbeen.restapi.protocol.ProtocolObject} into JSON
	 * @param protocolObject The protocol object
	 * @return The JSON representation of the protocol object
	 */
	protected final String serializeModelObject(ProtocolObject protocolObject) {
		try {
			return omap.writeValueAsString(protocolObject);
		} catch (IOException e) {
			return new ErrorObject(e.getMessage()).toString();
		}
	}
}
