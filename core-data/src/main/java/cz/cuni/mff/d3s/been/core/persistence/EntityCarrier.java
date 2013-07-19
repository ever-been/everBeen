package cz.cuni.mff.d3s.been.core.persistence;

import java.io.Serializable;

/**
 * A carrier object that wraps serialized entries along with a persistence
 * identifier, so that they get saved to the right place, once they hit a
 * persistence layer.
 * 
 * @author darklight
 * 
 */
public final class EntityCarrier implements Serializable {

	/**
	 * Serailization ID
	 */
	private static final long serialVersionUID = -5032423444972164608L;

	/**
	 * An identifier of the entity (determines storage location)
	 */
	private EntityID entityId;

	/**
	 * Serialized data of the entity.
	 */
	private String entityJSON;

	/**
	 * Get the entity's ID
	 *
	 * @return The {@link EntityID}
	 */
	public EntityID getEntityId() {
		return entityId;
	}

	/**
	 * Set the entity's ID
	 *
	 * @param entityId {@link EntityID} to set
	 */
	public void setEntityId(EntityID entityId) {
		this.entityId = entityId;
	}

	/**
	 * Get the entity's JSON
	 *
	 * @return JSON data
	 */
	public String getEntityJSON() {
		return entityJSON;
	}

	/**
	 * Set the entity's JSON
	 *
	 * @param entityJSON Entity's JSON data to set
	 */
	public void setEntityJSON(String entityJSON) {
		this.entityJSON = entityJSON;
	}

	/**
	 * Fluently set this carrier's entity ID
	 *
	 * @param entityId {@link EntityID} to set to this carrier
	 *
	 * @return Thsi carrier, after having set its entity ID
	 */
	public EntityCarrier withId(EntityID entityId) {
		setEntityId(entityId);
		return this;
	}

	/**
	 * Fluently set this carrier's JSON data
	 *
	 * @param jsonData JSON data to set to this carrier
	 *
	 * @return This carrier, after having set its JSON data
	 */
	public EntityCarrier withData(String jsonData) {
		setEntityJSON(jsonData);
		return this;
	}
}
