package cz.cuni.mff.d3s.been.taskapi;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import java.text.ParseException;
import java.util.*;

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

	public static ResultMapping empty() {
		return new ResultMapping();
	}

	private ResultMapping() {
		this.typeMapping = new TreeMap<String, Class<?>>();
		this.aliases = new TreeMap<String, String>();
	}

	/**
	 * Get type mappings of result fields
	 *
	 * @return Type mapping
	 */
	public Map<String, Class<?>> getTypeMapping() {
		return typeMapping;
	}

	/**
	 * Get class mapping for a field.
	 * Works for aliases, too.
	 *
	 * @param name Name of the field
	 *
	 * @return The field's type mapping
	 */
	public Class<?> typeForName(String name) {
		Class<?> type = typeMapping.get(name);
		if (type == null) {
			final String aliasedKey = aliases.get(name);
			if (aliasedKey != null) type = typeMapping.get(aliasedKey);
		}
		return type;
	}

	/**
	 * Get aliases for type mapping keys
	 *
	 * @return Alias map
	 */
	public Map<String, String> getAliases() {
		return aliases;
	}

	/**
	 * Get type mapping keys with no aliases
	 *
	 * @return The unaliased key set
	 */
	public Set<String> getNonAliasedKeys() {
		final Set<String> nonAliasedKeys = new TreeSet<String>(typeMapping.keySet());
		nonAliasedKeys.removeAll(aliases.values());
		return nonAliasedKeys;
	}
}
