package cz.cuni.mff.d3s.been.util;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import java.io.IOException;
import java.util.*;

/**
 * A utility class handling the conversion of JSON to a typed map of Objects.
 *
 * @author darklight
 */
public class JsonToTypedMap {
	final Map<String, Class<?>> typeMap;
	final Map<String, Collection<String>> reverseAliasMapping;
	final JsonFactory jf;

	/**
	 * Create a conversion, providing classes for desired properties.
	 *
	 * @param typeMap Class definition for recognized properties; only primitive types are supported
	 */
	public JsonToTypedMap(Map<String, Class<?>> typeMap, Map<String, String> aliases) {
		this.typeMap = typeMap;
		this.reverseAliasMapping = CollectionUtils.reverseMapping(aliases);
		this.jf = new JsonFactory();
	}

	/**
	 * Convert a single object (in JSON) to a map of objects, typed according to this conversion's {@link #typeMap}.
	 *
	 * @param serializedObject Object JSON to convert
	 *
	 * @return A map of objects, with requested runtime types
	 *
	 * @throws JsonException When a desired property cannot be cast to the desired type, or when provided serialization is not valid JSON, or when provided JSON contains unsupported value types
	 */
	public Map<String, Object> convert(String serializedObject) throws JsonException {
		final Deque<String> nq = new ArrayDeque<String>();
		try {
			final JsonParser jp = jf.createJsonParser(serializedObject);
			final Map<String, Object> objectMap = new TreeMap<String, Object>();

			String currentName = null;
			Class<?> currentType = null;
			int immersion = -1; // bracket immersion; -1 = outside root-level brackets; 0 = inside root-level brackets
			for (JsonToken tok = jp.nextToken(); jp.hasCurrentToken(); tok = jp.nextToken()) {
				switch(tok) {
					case START_OBJECT:
						++immersion;
						if (immersion > -1 && immersion != nq.size())
							throw new JsonException(String.format("Unsupported nested object in JSON (%s)", serializedObject));
						break;
					case END_OBJECT:
						--immersion;
						if (immersion > -1) nq.removeLast();
						if (immersion > -1 && immersion != nq.size()) throw new JsonException(String.format("Unsupported nested object in JSON (%s): termination clause expected queue size to be %d, but was %d", serializedObject, immersion, nq.size()));
						break;
					case START_ARRAY:
						throw new JsonException(String.format("Unsupported array value in JSON (%s)", serializedObject));
					case END_ARRAY:
						throw new JsonException(String.format("Unsupported array value in JSON (%s)", serializedObject));
					case VALUE_EMBEDDED_OBJECT:
						throw new JsonException(String.format("Unsupported nested object in JSON (%s)", serializedObject));
					case FIELD_NAME:
						nq.add(jp.getCurrentName());
						currentName = nameQueueToName(nq);
						currentType = typeMap.get(currentName);
						break;
					default:
						// only value tokens left for this case
						if (currentType != null) {
							final Object value = parseTypedValue(serializedObject, jp, currentType);
							final Collection<String> aliases = reverseAliasMapping.get(currentName);
							if (aliases == null || aliases.isEmpty()) {
								objectMap.put(currentName, value);
							} else {
								for (String alias: aliases) objectMap.put(alias, value);
							}
						}
						nq.removeLast();
						break;
				}
			}
			return objectMap;
		} catch (JsonParseException e) {
			throw new JsonException(String.format("Could not parse entry %s", serializedObject), e);
		} catch (IOException e) {
			throw new JsonException(String.format("Could not read entry %s", serializedObject), e);
		}
	}

	private String nameQueueToName(Queue<String> nameQueue) {
		final StringBuilder name = new StringBuilder();
		boolean separ = false;
		final Iterator<String> ni = nameQueue.iterator();
		while(ni.hasNext()) {
			if (separ) name.append('.');
			name.append(ni.next());
			separ = true;
		}
		return name.toString();
	}

	/**
	 * Parse a value to expected type from the current state of the JSON parser.
	 *
	 * @param json JSON object; no work done with it here, just present for logging
	 * @param jp The JSON parser
	 * @param expectedClass Expected type of the returned object
	 *
	 * @return The parsed object
	 *
	 * @throws JsonParseException When the provided JSON is invalid, but this should be detected sooner
	 * @throws IOException When the JSON parser's stream is corrupted
	 * @throws JsonException When an unexpected token type is met
	 */
	private Object parseTypedValue(String json, JsonParser jp, Class<?> expectedClass) throws JsonParseException, IOException, JsonException {
		switch(jp.getCurrentToken()) {
			case VALUE_NULL:
				throwUnexpectedType(json, jp.getCurrentName(), expectedClass, null);
			case VALUE_FALSE:
				if (Boolean.class.equals(expectedClass)) {
					return Boolean.FALSE;
				} else {
					throwUnexpectedType(json, jp.getCurrentName(), expectedClass, Boolean.class);
				}
				break;
			case VALUE_TRUE:
				if (Boolean.class.equals(expectedClass)) {
					return Boolean.TRUE;
				} else {
					throwUnexpectedType(json, jp.getCurrentName(), expectedClass, Boolean.class);
				}
			case VALUE_NUMBER_FLOAT:
				if (Float.class.equals(expectedClass)) {
					return jp.getFloatValue();
				} else if (Double.class.equals(expectedClass)) {
					return jp.getDoubleValue();
				} else {
					throwUnexpectedType(json, jp.getCurrentName(), expectedClass, Float.class);
				}
			case VALUE_NUMBER_INT:
				if (Integer.class.equals(expectedClass)) {
					return jp.getIntValue();
				} else if (Long.class.equals(expectedClass)) {
					return jp.getLongValue();
				} else {
					throwUnexpectedType(json, jp.getCurrentName(), expectedClass, Integer.class);
				}
			case VALUE_STRING:
				if (String.class.equals(expectedClass)) {
					return jp.getText();
				} else {
					throwUnexpectedType(json, jp.getCurrentName(), expectedClass, String.class);
				}
			default:
				return null;
		}
		return null;
	}

	private void throwUnexpectedType(String json, String property, Class<?> expected, Class<?> actual) throws JsonException {
		throw new JsonException(String.format("Unexpected token type in JSON '%s': Expected property '%s' to be of type '%s', but was '%s'", json, property, expected.getName(), actual == null ? "null" : actual.getName()));
	}
}
