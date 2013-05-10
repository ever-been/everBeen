package cz.cuni.mff.d3s.been.core.persistence;


import java.io.Serializable;

/**
 * A carrier object that helps with serialization fo
 * 
 * @author darklight
 * 
 */
public final class EntityCarrier implements Serializable {
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
