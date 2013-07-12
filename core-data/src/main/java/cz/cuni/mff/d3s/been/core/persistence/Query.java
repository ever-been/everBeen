package cz.cuni.mff.d3s.been.core.persistence;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * An object representing someone's query into the persistence layer.
 */
public class Query implements Serializable {
	private final String id;
	private final EntityID entityID;
	private final Map<String, Serializable> selectors;

	private Query(EntityID entityID) {
		this.id = UUID.randomUUID().toString();
		this.entityID = entityID;
		this.selectors = new HashMap<String,Serializable>();
	}

	/**
	 * Create a query on a target entity type
	 *
	 * @param entityId Type (ID) of the entity
	 *
	 * @return A query to that given entity, with no criteria
	 */
	public static final Query on(EntityID entityId) {
		return new Query(entityId);
	}

	/**
	 * Add a criteria to the query
	 *
	 * @param attribute Attribute to target
	 * @param value Expected value of the attribute
	 *
	 * @return The same query, with added criteria
	 */
	public Query with(String attribute, Serializable value) {
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
	public Query without(String attribute) {
		if (selectors.containsKey(attribute)) {
			selectors.remove(attribute);
		}
		return this;
	}

	/**
	 * @return The ID of the queried entity
	 */
	public EntityID getEntityID() {
		return entityID;
	}

	/**
	 * @return The ID of this query
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get the key/value mappings between attribute and expected value
	 *
	 * @return A map of expected value associations
	 */
	public Map<String, Serializable> getKV() {
		return selectors;
	}
}
