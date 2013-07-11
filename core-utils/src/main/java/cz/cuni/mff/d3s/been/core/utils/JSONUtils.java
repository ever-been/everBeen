package cz.cuni.mff.d3s.been.core.utils;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class JSONUtils {

	public static String serialize(Object obj) throws JsonException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(obj);
		} catch (IOException e) {
			throw new JsonException("Unable to serialize JAVA object to JSON", e);
		}
	}

	public static <T> T deserialize(String json, Class<T> to) throws JsonException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(json, to);
		} catch (IOException e) {
			throw new JsonException("Unable to deserialize JSON to JAVA object", e);
		}
	}

	public static <T> T deserialize(String json, TypeReference<T> type) throws JsonException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(json, type);
		} catch (IOException e) {
			throw new JsonException("Unable to deserialize JSON to JAVA object", e);
		}
	}

}
