package cz.everbeen.restapi.protocol;


import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Serializer for {@link cz.everbeen.restapi.protocol.ProtocolObject} instances.
 *
 * @author darklight
 */
public class ProtocolObjectSerializer {
	public static String MIME_TYPE = "application/json";

	private final ObjectMapper om;

	public ProtocolObjectSerializer() {
		this.om = new ObjectMapper();
	}

	/**
	 * Serialize a protocol object
	 * @param object Object to serialize
	 * @return The serialized object
	 * @throws IOException When serialization fails
	 */
	public String serialize(ProtocolObject object) throws IOException {
		return om.writeValueAsString(object);
	}

	/**
	 * Deserialize a protocol object
	 * @param serializedObject The serialized object to unmarshall
	 * @param objectClass Class to unmarshall into
	 * @param <T> Runtime type of the unmarshalled object
	 * @return The deserialized object
	 * @throws IOException When serialized object doesn't match the specified runtime type
	 */
	public <T extends ProtocolObject> T deserialize(String serializedObject, Class<T> objectClass) throws IOException {
		return om.readValue(serializedObject, objectClass);
	}
}
