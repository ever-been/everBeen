package cz.cuni.mff.d3s.been.taskapi;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import java.text.ParseException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Mapping of a result query result to a typed collection.
 *
 * @author darklight
 */
public class ResultMapping {
	/** The mapping of queried paths to primitive types */
	@JsonProperty("typeMapping")
	private Map<String, Class<?>> typeMapping;
	/** Aliases for the resulting document */
	@JsonProperty("aliases")
	private Map<String, String> aliases;

	@JsonCreator
	public ResultMapping(
		@JsonProperty("typeMapping") Map<String, String> typeMapping,
		@JsonProperty("aliases") Map<String, String> aliases
	) throws ClassNotFoundException {
		this.aliases = Collections.unmodifiableMap(aliases);
		this.typeMapping = new TreeMap<String, Class<?>>();
		for (Map.Entry<String, String> binding: typeMapping.entrySet()) {
			this.typeMapping.put(binding.getKey(), Class.forName(binding.getValue()));
		}
	}

	public Map<String, Class<?>> getTypeMapping() {
		return typeMapping;
	}

	public Map<String, String> getAliases() {
		return aliases;
	}
}
