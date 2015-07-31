package cz.cuni.mff.d3s.been.results;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * @author darklight
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class SerializableResultMappingKeyPair {
	@JsonProperty("key")
	private final String key;
	@JsonProperty("value")
	private final String value;

	public SerializableResultMappingKeyPair(
			@JsonProperty("key") String key,
			@JsonProperty("value") String value
	) {
		this.key = key;
		this.value = value;
	}

	@JsonCreator
	public static SerializableResultMappingKeyPair parseJSON(String json) throws IOException {
		return new ObjectMapper().readValue(json, SerializableResultMappingKeyPair.class);
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
