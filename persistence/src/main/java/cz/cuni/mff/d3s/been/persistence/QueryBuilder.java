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
	private EntityID entityID;
	Map<String, SkeletalAttributeFilter> selectors = new HashMap<String, SkeletalAttributeFilter>();

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
	 *
	 * @throws NullPointerException When any of the two parameters are null
	 */
	public QueryBuilder with(String attribute, String value) {
		if (attribute == null || value == null) {
			throw new NullPointerException(String.format("Invalid attribute specification '(key, value) == (%s, %s)': both key and value must be non-null", attribute, value));
		}
		selectors.put(attribute, new EqAttributeFilter(value));
		return this;
	}

	public AttributeFilterBuilder with(String attribute) {
		return new AttributeFilterBuilder(this, attribute);
	}

	/**
	 * Remove a criteria from the query
	 *
	 * @param attribute Attribute whose criteria should be cleaned
	 *
	 * @return The same query, with criteria removed (if they were part of the query)
	 */
	public QueryBuilder without(String attribute) {
		if (attribute == null) {
			throw new NullPointerException("Attribute name was null, but only non-null values are accepted");
		}
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
