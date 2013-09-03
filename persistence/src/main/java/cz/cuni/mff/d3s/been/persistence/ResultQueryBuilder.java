package cz.cuni.mff.d3s.been.persistence;

/**
 * A builder for result queries
 *
 * @author darklight
 */
public class ResultQueryBuilder extends QueryBuilderBase {

	/**
	 * Target the query on a result group
	 *
	 * @param group Group to target
	 *
	 * @return This {@link ResultQueryBuilder}, with altered targeting
	 */
	public ResultQueryBuilder on(String group) {
		this.entityID.setGroup(group);
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
	public ResultQueryBuilder with(String attribute, Object value) {
		if (attribute == null || value == null) {
			throw new NullPointerException(String.format("Invalid attribute specification '(key, value) == (%s, %s)': both key and value must be non-null", attribute, value));
		}
		selectors.put(attribute, new EqAttributeFilter(value));
		return this;
	}

	/**
	 * Remove a criteria from the query
	 *
	 * @param attribute Attribute whose criteria should be cleaned
	 *
	 * @return The same query, with criteria removed (if they were part of the query)
	 */
	public ResultQueryBuilder without(String attribute) {
		if (attribute == null) {
			throw new NullPointerException("Attribute name was null, but only non-null values are accepted");
		}
		if (selectors.containsKey(attribute)) {
			selectors.remove(attribute);
		}
		return this;
	}

	/**
	 * Add a criteria to the query
	 *
	 * @param attribute Attribute to target
	 *
	 * @return The criteria builder
	 */
	public AttributeFilterBuilder<ResultQueryBuilder> with(String attribute) {
		return new AttributeFilterBuilder<ResultQueryBuilder>(this, attribute);
	}

	/**
	 * Set attributes to fetch. Other attributes will be omitted from the persistence layer query, and will not be set. This will probably result in <code>null</code> fields in targeted deserialization object.
	 *
	 * @param attributes Attributes to fetch
	 *
	 * @return The same query, with attribute mapping specified
	 */
	public ResultQueryBuilder retrieving(String... attributes) {
		for (String attribute: attributes) {
			mappings.add(attribute);
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
		if (mappings.isEmpty()) {
			return new FetchQuery(entityID, selectors);
		} else {
			return new FetchQuery(entityID, selectors, mappings);
		}
	}

}
