package cz.cuni.mff.d3s.been.core.persistence;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A builder for {@link Query} objects
 *
 * @author darklight
 */
public class QueryBuilder {
	private String id;
	private EntityID entityID;
	private Map<String, String> selectors = new HashMap<String, String>();

	public QueryBuilder on(EntityID entityID) {
		this.entityID = entityID;
		return this;
	}

	/**
	 * Add a criteria to the query
	 *
	 * @param attribute Attribute to target
	 * @param value Expected value of the attribute
	 *
	 * @return The same query, with added criteria
	 */
	public QueryBuilder with(String attribute, String value) {
		selectors.put(attribute, value);
		return this;
	}

	/**
	 * Remove a criteria from the query
	 *
	 * @param attribute Attribute whose criteria should be cleaned
	 *
	 * @return The same query, with criteria removed (if they were part of the query)
	 */
	public QueryBuilder without(String attribute) {
		if (selectors.containsKey(attribute)) {
			selectors.remove(attribute);
		}
		return this;
	}

	/**
	 * Build a query with using this builder's actual configuration.
	 *
	 * @return A new {@link Query}
	 *
	 * @throws IllegalStateException When some mandatory parameters are missing (Full entity ID is required)
	 */
	public Query build() throws IllegalStateException {
		if (entityID == null || entityID.getGroup() == null || entityID.getKind() == null) {
			throw new IllegalStateException("Entity ID or some of its fields are null.");
		}
		return new Query(entityID, selectors);
	}
}
