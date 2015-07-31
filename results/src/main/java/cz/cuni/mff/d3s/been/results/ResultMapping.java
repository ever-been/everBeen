package cz.cuni.mff.d3s.been.results;

import java.util.*;

/**
 * Mapping of a result query result to a typed collection.
 *
 * @author darklight
 */
public class ResultMapping {
	/** The mapping of queried paths to primitive types */
	private Map<String, String> typeMapping;
	/** Aliases for the resulting document */
	private Map<String, String> aliases;

	public ResultMapping(
		Map<String, String> typeMapping,
		Map<String, String> aliases
	) {
		this.typeMapping = typeMapping;
		this.aliases = aliases;
	}

	/**
	 * Create a pure mapping (no aliases)
	 *
	 * @param typeMapping Type mapping
	 */
	private ResultMapping(
			Map<String, String> typeMapping
	) {
		this.typeMapping = typeMapping;
		this.aliases = new TreeMap<String, String>();
	}

	public static ResultMapping empty() {
		return new ResultMapping();
	}

	/**
	 * Deserialize a result mapping
	 *
	 * @param serializableResultMapping Serializable version of a result mapping
	 *
	 * @return The result mapping
	 */
	public static ResultMapping deserialize(SerializableResultMapping serializableResultMapping) {
		final TreeMap<String, String> typeMappings = new TreeMap<String, String>();
		final TreeMap<String, String> aliases = new TreeMap<String, String>();

		for (SerializableResultMappingKeyPair kp: serializableResultMapping.getTypeMappings()) {
			typeMappings.put(kp.getKey(), kp.getValue());
		}

		for (SerializableResultMappingKeyPair kp: serializableResultMapping.getAliases()) {
			aliases.put(kp.getKey(), kp.getValue());
		}

		return new ResultMapping(typeMappings, aliases);
	}

	/**
	 * Preserialize this mapping into a Jackson-friendly form
	 *
	 * @return Preserialized mapping
	 */
	public SerializableResultMapping preSerialize() {
		final List<SerializableResultMappingKeyPair> tm = new LinkedList<SerializableResultMappingKeyPair>();
		final List<SerializableResultMappingKeyPair> al = new LinkedList<SerializableResultMappingKeyPair>();

		for (Map.Entry<String, String> mapping: typeMapping.entrySet()) {
			tm.add(new SerializableResultMappingKeyPair(mapping.getKey(), mapping.getValue()));
		}

		for (Map.Entry<String, String> alias: aliases.entrySet()) {
			al.add(new SerializableResultMappingKeyPair(alias.getKey(), alias.getValue()));
		}

		return new SerializableResultMapping(tm, al);
	}

	/**
	 * Create a result mapping with no aliases from a {@link cz.cuni.mff.d3s.been.results.PrimitiveType} mapping.
	 *
	 * @param pTypes Primitive types to map
	 *
	 * @return Result mapping
	 */
	public static ResultMapping fromPtypes(Map<String, PrimitiveType> pTypes) {
		final Map<String, String> typeMap = new TreeMap<String, String>();
		for (Map.Entry<String, PrimitiveType> pType: pTypes.entrySet()) {
			typeMap.put(pType.getKey(), pType.getValue().getTypeAlias());
		}
		return new ResultMapping(typeMap);
	}

	private ResultMapping() {
		this.typeMapping = new TreeMap<String, String>();
		this.aliases = new TreeMap<String, String>();
	}

	/**
	 * Get type mappings of result fields
	 *
	 * @return Type mapping
	 */
	public Map<String, String> getTypeMapping() {
		return typeMapping;
	}

	/**
	 * Get type mapping for a field.
	 * Works for aliases, too.
	 *
	 * @param name Name of the field
	 *
	 * @return The field's type mapping
	 */
	public String typeForName(String name) {
		String type = typeMapping.get(name);
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
