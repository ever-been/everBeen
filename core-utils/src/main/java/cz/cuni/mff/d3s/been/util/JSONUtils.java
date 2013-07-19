package cz.cuni.mff.d3s.been.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.type.TypeReference;

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
		final ObjectMapper om = new ObjectMapper();
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

	public String serialize(Object obj) throws JsonException {
		try {
			return om.writeValueAsString(obj);
		} catch (IOException e) {
			throw new JsonException("Unable to serialize JAVA object to JSON", e);
		}
	}

	public <T> T deserialize(String json, Class<T> to) throws JsonException {
		try {
			return om.readValue(json, to);
		} catch (IOException e) {
			throw new JsonException("Unable to deserialize JSON to JAVA object", e);
		}
	}

	public <T> T deserialize(String json, TypeReference<T> type) throws JsonException {
		try {
			return om.readValue(json, type);
		} catch (IOException e) {
			throw new JsonException("Unable to deserialize JSON to JAVA object", e);
		}
	}

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

}
