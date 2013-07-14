package cz.cuni.mff.d3s.been.core.persistence;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * An object representing someone's query into the persistence layer.
 */
public class Query implements Serializable {
	private final String id;
	private EntityID entityID;
	private Map<String, String> selectors;

	private Query() {
		this.id = UUID.randomUUID().toString();
	}

	Query(EntityID entityID, Map<String, String> selectors) {
		this.id = UUID.randomUUID().toString();
		this.entityID = entityID;
		this.selectors = Collections.unmodifiableMap(selectors);
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
	public Map<String, String> getSelectors() {
		return selectors;
	}
}
