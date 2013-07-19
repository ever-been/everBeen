package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;

import java.util.HashMap;
import java.util.Map;

/**
 * A builder for {@link FetchQuery} objects
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
	 * Build a fetch query intended for data retrieval
	 *
	 * @return A fetch query with this builder's current setup
	 *
	 * @throws IllegalStateException When some mandatory parameters are missing (Full entity ID is required)
	 */
	public Query fetch() throws IllegalStateException {
		if (entityID == null || entityID.getGroup() == null || entityID.getKind() == null) {
			throw new IllegalStateException("Entity ID or some of its fields are null.");
		}
		return new FetchQuery(entityID, selectors);
	}

	/**
	 * Build a query intended for data removal.
	 *
	 * @return A delete query with this builder's current setup
	 *
	 * @throws IllegalStateException When the entity id is only filled partially
	 */
	public Query delete() throws IllegalStateException {
		if (entityID != null && (entityID.getKind() == null || entityID.getGroup() == null)) {
			throw new IllegalStateException("Entity ID is filled partially");
		}
		return new DeleteQuery(entityID, selectors);
	}
}
