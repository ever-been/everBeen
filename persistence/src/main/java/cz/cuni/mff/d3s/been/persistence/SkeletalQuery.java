package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.*;

/**
 * An abstract base for a {@link Query}
 *
 * @author darklight
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({
		@JsonSubTypes.Type(value = FetchQuery.class),
		@JsonSubTypes.Type(value = DeleteQuery.class),
})
@JsonIgnoreProperties({"type", "selectorNames"})
abstract class SkeletalQuery implements Query {

	private String id;
	private EntityID entityID;
	protected Map<String, SkeletalAttributeFilter> selectors;

	SkeletalQuery() {
		this.id = null;
		this.entityID = null;
		this.selectors = null;
	}

	SkeletalQuery(EntityID entityID, Map<String, SkeletalAttributeFilter> selectors) {
		this.id = UUID.randomUUID().toString();
		this.entityID = entityID;
		this.selectors = Collections.unmodifiableMap(selectors);
	}

	public Map<String, SkeletalAttributeFilter> getSelectors() {
		return selectors;
	}

	/**
	 * Setter for Jackson mapping
	 *
	 * @param id Query ID to set
	 */
	void setId(String id) {
		this.id = id;
	}

	/**
	 * Setter for Jackson mapping
	 *
	 * @param entityID Entity ID to set
	 */
	void setEntityID(EntityID entityID) {
		this.entityID = entityID;
	}

	/**
	 * Setter for Jackson mapping
	 *
	 * @param selectors Selectors to set
	 */
	void setSelectors(Map<String, SkeletalAttributeFilter> selectors) {
		this.selectors = selectors;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public EntityID getEntityID() {
		return entityID;
	}

	@Override
	public Set<String> getSelectorNames() {
		return selectors.keySet();
	}

	@Override
	public AttributeFilter getSelector(String selectorName) {
		return selectors.get(selectorName);
	}

	@Override
	public Set<String> getMappings() {
		return null;
	}
}
