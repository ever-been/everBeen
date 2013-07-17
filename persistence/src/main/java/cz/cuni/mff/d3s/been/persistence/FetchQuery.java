package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;

import java.io.Serializable;
import java.util.Map;

/**
 * An object representing a fetch (select) query into the persistence layer.
 */
class FetchQuery extends SkeletalQuery implements Serializable {
	FetchQuery(EntityID entityID, Map<String, String> selectors) {
		super(entityID, selectors);
	}

	@Override
	public QueryType getType() {
		return QueryType.FETCH;
	}
}
