package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * A serializer that helps marshalling/unmarshalling queries to/from JSON
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
	 * @throws JsonException When there's a problem when mapping the query to JSON
	 */
	public final String serializeQuery(Query query) throws JsonException {
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
	 * @throws JsonException When something goes when mapping the JSON to the query
	 */
	public final Query deserializeQuery(String json) throws JsonException {
		return jsonUtils.deserialize(json, SkeletalQuery.class);
	}
}
