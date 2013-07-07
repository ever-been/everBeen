package cz.cuni.mff.d3s.been.core.utils;

import static org.codehaus.jackson.JsonToken.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.annotation.NotThreadSafe;

/**
 * The JsonStreamer is rather limited streaming JSON streaming parser.
 * 
 * It visits every "key": value pair and calls a handler if such a handler is
 * registered for the key . The key point is that it does not handle sub-object
 * or array, it simple ignores them.
 * 
 * @author Martin Sixta
 */
@NotThreadSafe
public class JsonStreamer {

	/** logging */
	private static final Logger log = LoggerFactory.getLogger(JsonStreamer.class);

	/** factory */
	private final JsonFactory jsonFactory;

	/** Registered handlers */
	private final Map<String, JsonKeyHandler> handlers;
	public JsonStreamer() {
		this.jsonFactory = new JsonFactory();
		this.handlers = new HashMap<>();
	}

	/**
	 * Adds a handler for a key.
	 * 
	 * If a handler for the given key exits it will be overridden.
	 * 
	 * @param key
	 *          key on which to call the handler
	 * @param handler
	 *          code to call
	 */
	public void addHandler(String key, JsonKeyHandler handler) {
		handlers.put(key, handler);
	}

	/**
	 * Removes a handler fot a key.
	 * 
	 * @param key
	 *          associated key
	 */
	public void removeHandler(String key) {
		handlers.remove(key);
	}

	/**
	 * 
	 * Parses a JSON String and calls registered handlers.
	 * 
	 * @param json
	 * @throws JsonException
	 */
	public void process(final String json) throws JsonException {
		if (handlers.size() == 0) {
			return;
		}

		JsonParser jp;

		try {
			jp = jsonFactory.createJsonParser(json);
		} catch (Throwable t) {
			String msg = String.format("Cannot parse JSON: %s", json);
			throw new JsonException(msg, t);
		}

		JsonToken currentToken;

		try {
			jp.nextToken();

			while (true) {
				currentToken = jp.nextToken();

				if (currentToken == null) {
					break;
				}

				if (currentToken != FIELD_NAME) {
					continue;
				}

				final String key = jp.getCurrentName();

				currentToken = jp.nextToken();

				if (currentToken == START_ARRAY || currentToken == START_OBJECT) {
					continue;
				}

				if (handlers.containsKey(key)) {
					callHandler(key, jp.getText(), json);
				}

			}
		} catch (Throwable t) {
			String msg = String.format("Cannot process JSON token '%s'", jp.getCurrentToken());
			throw new JsonException(msg, t);
		}

	}

	private void callHandler(final String key, final String value, final String json) throws IOException {
		try {
			handlers.get(key).handle(key, value, json);
		} catch (Exception e) {
			log.error("Handler caused an exception", e);

		}
	}

}
