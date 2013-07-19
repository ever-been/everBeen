package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.Map;

/**
 * A deletion query
 *
 * @author darklight
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
class DeleteQuery extends SkeletalQuery {

	/**
	 * Don't use this - this is just for Jackson
	 */
	private DeleteQuery() {
	}

	DeleteQuery(EntityID entityID, Map<String, String> selectors) {
		super(entityID, selectors);
	}

	@Override
	public QueryType getType() {
		return QueryType.DELETE;
	}
}
