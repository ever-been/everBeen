package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.io.Serializable;
import java.util.Map;

/**
 * An object representing a fetch (select) query into the persistence layer.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
class FetchQuery extends SkeletalQuery implements Serializable {

	private FetchQuery() {
	}

	FetchQuery(EntityID entityID, Map<String, String> selectors) {
		super(entityID, selectors);
	}

	@Override
	public QueryType getType() {
		return QueryType.FETCH;
	}
}
