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

	public EntityID getEntityId() {
		return entityId;
	}
	public void setEntityId(EntityID entityId) {
		this.entityId = entityId;
	}
	public String getEntityJSON() {
		return entityJSON;
	}
	public void setEntityJSON(String entityJSON) {
		this.entityJSON = entityJSON;
	}
}
