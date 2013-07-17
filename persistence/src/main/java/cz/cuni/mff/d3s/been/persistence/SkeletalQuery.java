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
		@JsonSubTypes.Type(value = DeleteQuery.class)
})
@JsonIgnoreProperties({"type", "selectorNames"})
abstract class SkeletalQuery implements Query {

	private String id;
	private EntityID entityID;
	private Map<String, String> selectors;

	SkeletalQuery() {
		this.id = null;
		this.entityID = null;
		this.selectors = null;
	}

	SkeletalQuery(EntityID entityID, Map<String, String> selectors) {
		this.id = UUID.randomUUID().toString();
		this.entityID = entityID;
		this.selectors = Collections.unmodifiableMap(selectors);
	}

	public Map<String, String> getSelectors() {
		return selectors;
	}

	void setId(String id) {
		this.id = id;
	}

	void setEntityID(EntityID entityID) {
		this.entityID = entityID;
	}

	void setSelectors(Map<String, String> selectors) {
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
	public String getSelector(String selectorName) {
		return selectors.get(selectorName);
	}
}
