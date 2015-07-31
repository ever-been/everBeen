package cz.cuni.mff.d3s.been.util;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.*;

/**
 * A utility class for JSON serialization and de-serialization
 */
public class JSONUtils {

	private final ObjectMapper om;

	JSONUtils(ObjectMapper om) {
		this.om = om;
	}

	/**
	 * Create a new instance of {@link JSONUtils}
	 *
	 * @return A new instance of JSON utilities
	 */
	public static JSONUtils newInstance() {
		ObjectMapper om = new ObjectMapper();
		VisibilityChecker.Std checker = VisibilityChecker.Std.defaultInstance().withIsGetterVisibility(JsonAutoDetect.Visibility.NONE);
		om.setSerializationConfig(om.getSerializationConfig().withVisibilityChecker(checker));
		return new JSONUtils(om);
	}

	/**
	 * Create a new instance of {@link JSONUtils} with a predefined {@link ObjectMapper}. This is useful when the serialization/deserialization config of the {@link ObjectMapper} has been overriden to match user requirements
	 *
	 * @param om {@link ObjectMapper} to use
	 *
	 * @return A new instance of JSON utilities with the given {@link ObjectMapper}
	 */
	public static JSONUtils newInstance(ObjectMapper om) {
		return new JSONUtils(om);
	}

	public static JSONUtils newFieldMapperInstance() {
		final ObjectMapper om = new ObjectMapper();
		om.setSerializationConfig(om.getSerializationConfig().without(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS).withVisibilityChecker(
				new FieldVisibilityChecker()));
		om.setDeserializationConfig(om.getDeserializationConfig().withVisibilityChecker(new FieldVisibilityChecker()));
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);

		return JSONUtils.newInstance(om);
	}

	/**
	 * Get the object mapper associated with this utils instance.
	 *
	 * @return The utils instance object mapper
	 */
	public ObjectMapper getOM() {
		return om;
	}

	/**
	 * Serialize an object into JSON
	 *
	 * @param obj Object to serialize
	 *
	 * @return JSON representation of provided object
	 *
	 * @throws JsonException If the object cannot be serialized
	 */
	public String serialize(Object obj) throws JsonException {
		try {
			return om.writeValueAsString(obj);
		} catch (IOException e) {
			throw new JsonException("Unable to serialize JAVA object to JSON", e);
		}
	}

	/**
	 * Deserialize an object from JSON
	 *
	 * @param json JSON to interpret
	 * @param to Class of the deserialized object
	 * @param <T> Type of the deserialized object
	 *
	 * @return The deserialized object
	 *
	 * @throws JsonException If the JSON cannot be mapped to provided class
	 */
	public <T> T deserialize(String json, Class<T> to) throws JsonException {
		try {
			return om.readValue(json, to);
		} catch (IOException e) {
			throw new JsonException("Unable to deserialize JSON to JAVA object", e);
		}
	}

	/**
	 * Deserialize an object from JSON
	 *
	 * @param json JSON to interpret
	 * @param type Reference to deserialized object's type
	 * @param <T> Type of the deserialized object
	 *
	 * @return The deserialized object
	 *
	 * @throws JsonException When the JSON cannot be mapped to provided type reference
	 */
	public <T> T deserialize(String json, TypeReference<T> type) throws JsonException {
		try {
			return om.readValue(json, type);
		} catch (IOException e) {
			throw new JsonException("Unable to deserialize JSON to JAVA object", e);
		}
	}

	/**
	 * Deserialize a collection
	 *
	 * @param data Data to deserialize
	 * @param itemType Class of the deserialized objecs
	 * @param <T> Type of the deserialized objects
	 *
	 * @return Deserialized data
	 *
	 * @throws JsonException When provided data cannot be mapped to provided type
	 */
	public <T> Collection<T> deserialize(Collection<String> data, Class<T> itemType) throws JsonException {
		final ObjectReader itemReader = om.reader(itemType);
		final ArrayList<T> deserializedData = new ArrayList<T>(data.size());
		try {
			for (String item: data) {
				deserializedData.add((T) itemReader.readValue(item));
			}
		} catch (IOException e) {
			throw new JsonException(String.format("Error when unmarshalling collection of %s", itemType.getSimpleName()), e);
		}
		return deserializedData;
	}

	/**
	 * Deserialize a batch of JSON object into typed maps, using types described in parameter.
	 *
	 * @param serializedObjects JSON to deserialize
	 * @param typeMap Types of the object's attributes
	 * @param aliases Field naming aliases
	 * @param ignoreBrokenItems Whether items that make the parsing crash should be omitted, rather than interrupting the entire deserialization
	 *
	 * @return A collection of maps resulting from the objects' deserialization
	 *
	 * @throws JsonException When one of given Strings is not valid JSON or when it doesn't contain correctly typed values
	 */
	public Collection<Map<String, Object>> deserialize(Collection<String> serializedObjects, Map<String, Class<?>> typeMap, Map<String, String> aliases, boolean ignoreBrokenItems) throws JsonException {
		if (aliases == null) aliases = new TreeMap<String, String>();
		final JsonToTypedMap jttm = new JsonToTypedMap(typeMap, aliases);
		final List<Map<String, Object>> res = new ArrayList<Map<String, Object>>(serializedObjects.size());
		for (String so: serializedObjects) {
			try {
				res.add(jttm.convert(so));
			} catch (JsonException e) {
				// only rethrow this if ignore is not set
				if (!ignoreBrokenItems) {
					throw e;
				}
			}
		}
		return res;
	}

	public Collection<Map<String, Object>> deserialize(byte [] dataset, Map<String, Class<?>> typeMap, Map<String, String> aliases, boolean ignoreBrokenItems) throws JsonException {
		final ObjectMapper om = new ObjectMapper();
		final Collection<Map<String, Object>> coll;
		try {
			final JavaType recordType = om.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
			final JavaType collType = om.getTypeFactory().constructCollectionType(List.class, recordType);
			coll = (Collection<Map<String, Object>>) om.readValue(dataset, collType);
		} catch (JsonParseException e) {
			throw new JsonException("Unmarshaling error reading dataset (invalid JSON)", e);
		} catch (IOException e) {
			throw new JsonException("I/O error reading dataset", e);
		}
		return coll;
	}

}
