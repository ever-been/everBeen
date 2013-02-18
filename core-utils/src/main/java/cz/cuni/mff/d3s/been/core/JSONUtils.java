package cz.cuni.mff.d3s.been.core;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

public class JSONUtils {

	public static String serialize(Object obj) throws JSONSerializerException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(obj);
		} catch (IOException e) {
			throw new JSONSerializerException(
					"Unable to serialize JAVA object to JSON", e);
		}
	}

	public static <T> T deserialize(String json, Class<T> to)
			throws JSONSerializerException {
		ObjectMapper mapper = new ObjectMapper();
		T deserialized;
		try {
			deserialized = mapper.readValue(json, to);
		} catch (IOException e) {
			throw new JSONSerializerException(
					"Unable to deserialize JSON to JAVA object", e);
		}
		return deserialized;
	}

	@SuppressWarnings("serial")
	public static class JSONSerializerException extends Exception {

		private JSONSerializerException(String message, Throwable cause) {
			super(message, cause);
		}

		private JSONSerializerException(String message) {
			super(message);
		}

	}

}
