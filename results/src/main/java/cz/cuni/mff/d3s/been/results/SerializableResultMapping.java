package cz.cuni.mff.d3s.been.results;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A Jackson-friendly version of the {@link cz.cuni.mff.d3s.been.results.ResultMapping}.
 * Created because Jackson had trouble deserializing maps.
 *
 * @author darklight
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class SerializableResultMapping {

	@JsonProperty("typeMappings")
	private final SerializableResultMappingKeyPair [] typeMappings;
	@JsonProperty("aliases")
	private final SerializableResultMappingKeyPair [] aliases;

	public SerializableResultMapping(
			@JsonProperty("typeMappings") Collection<SerializableResultMappingKeyPair> typeMappings,
			@JsonProperty("aliases") Collection<SerializableResultMappingKeyPair> aliases
	) {
		this.typeMappings = new ArrayList<>(typeMappings)
				.toArray(new SerializableResultMappingKeyPair[typeMappings.size()]);
		this.aliases = new ArrayList<>(aliases)
				.toArray(new SerializableResultMappingKeyPair[aliases.size()]);
	}

	@JsonCreator
	public static SerializableResultMapping parseJson(String json) throws IOException {
		return new ObjectMapper().readValue(json, SerializableResultMapping.class);
	}

	public SerializableResultMappingKeyPair [] getTypeMappings() {
		return typeMappings;
	}

	public SerializableResultMappingKeyPair [] getAliases() {
		return aliases;
	}
}
