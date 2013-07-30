package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Database-specific native query.
 *
 * @author Kuba Brecka
 *
 * @deprecated The entire principle of native querying needs to be rethought and possibly eliminated. Usage strongle discouraged.
 */
@Deprecated
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
class NativeQuery extends SkeletalQuery {

	private String queryString;

	public NativeQuery(String queryString) {
		super(new EntityID(), new HashMap<String, String>());
	}

	@Override
	public QueryType getType() {
		return QueryType.NATIVE;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
}
