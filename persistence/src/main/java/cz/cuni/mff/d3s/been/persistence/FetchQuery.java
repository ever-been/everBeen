package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * An object representing a fetch (select) query into the persistence layer.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
class FetchQuery extends SkeletalQuery implements Serializable {

	private Set<String> mappings;

	private FetchQuery() {
	}

	FetchQuery(EntityID entityID, Map<String, SkeletalAttributeFilter> selectors) {
		super(entityID, selectors);
		this.mappings = null;
	}

	FetchQuery(EntityID entityID, Map<String, SkeletalAttributeFilter> selectors, Set<String> mappings) {
		super(entityID, selectors);
		this.mappings = mappings;
	}

	@Override
	public QueryType getType() {
		return QueryType.FETCH;
	}

	@Override
	public Set<String> getMappings() {
		return mappings;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(mappings == null ? "*" : mappings.toString());
		sb.append(" from ");
		sb.append(getEntityID().toString());
		sb.append(" where ");
		sb.append(getSelectors().toString());
		return sb.toString();
	}
}
