package cz.cuni.mff.d3s.been.core.protocol;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

public class JSONSerializer {

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

	public static class JSONSerializerException extends Exception {

		/**
		 * SERIAL VERSION UID
		 */
		private static final long serialVersionUID = 1L;

		private JSONSerializerException(String message, Throwable cause) {
			super(message, cause);
		}

		private JSONSerializerException(String message) {
			super(message);
		}

	}

}