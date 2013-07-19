package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * A serializer that helps marshalling/unmarshalling queries to/from JSON.
 *
 * This serializer works with default BEEN implementations of {@link Query}. It will not work with user {@link Query} implementations.
 *
 * @author darklight
 */
public final class QuerySerializer {

	private final JSONUtils jsonUtils = JSONUtils.newInstance();

	/**
	 * Marshall a query to JSON
	 *
	 * @param query Query to marshall
	 *
	 * @return The serialized query
	 *
	 * @throws JsonException When the query is of wring subtype or when there's a problem when mapping the query to JSON
	 */
	public final String serializeQuery(Query query) throws JsonException {
		if (! ( query instanceof SkeletalQuery)) {
			throw new JsonException(String.format("Provided query is not a %s instance", SkeletalQuery.class.getSimpleName()));
		}
		final SkeletalQuery skeleton = (SkeletalQuery) query;
		return jsonUtils.serialize(skeleton);
	}

	/**
	 * Unmarshall a query from JSON
	 *
	 * @param json Serialized query to unmarshall
	 *
	 * @return The deserialized query
	 *
	 * @throws JsonException When something goes wrong when mapping the JSON to the query
	 */
	public final Query deserializeQuery(String json) throws JsonException {
		return jsonUtils.deserialize(json, SkeletalQuery.class);
	}

	/**
	 * Marshall an answer to JSON
	 *
	 * @param answer Answer to marshall
	 *
	 * @return Serialized answer
	 *
	 * @throws JsonException If the answer is of wrong subtype or serialization goes awry
	 */
	public final String serializeAnswer(QueryAnswer answer) throws JsonException {
		if (! ( answer instanceof SkeletalQueryAnswer)) {
			throw new JsonException(String.format("Provided answer is not a %s instance", SimpleQueryAnswer.class.getSimpleName()));
		}
		final SkeletalQueryAnswer skeleton = (SkeletalQueryAnswer) answer;
		return jsonUtils.serialize(skeleton);
	}

	/**
	 * Unmarshall an answer from JSON
	 *
	 * @param json Serialized answer to unmarshall
	 *
	 * @return The deserialized answer
	 *
	 * @throws JsonException When something goes wrong when mapping the JSON to the answer
	 */
	public final QueryAnswer deserializeAnswer(String json) throws JsonException {
		return jsonUtils.deserialize(json, SkeletalQueryAnswer.class);
	}
}
