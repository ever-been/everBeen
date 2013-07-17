package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;

import java.util.Map;

/**
 * A deletion query
 *
 * @author darklight
 */
class DeleteQuery extends SkeletalQuery {
	DeleteQuery(EntityID entityID, Map<String, String> selectors) {
		super(entityID, selectors);
	}

	@Override
	public QueryType getType() {
		return QueryType.DELETE;
	}
}
