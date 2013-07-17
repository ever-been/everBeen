package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.Query;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * An abstract base for a {@link Query}
 *
 * @author darklight
 */
public abstract class SkeletalQuery implements Query {

	private final String id;
	private final EntityID entityID;
	private final Map<String, String> selectors;

	SkeletalQuery(EntityID entityID, Map<String, String> selectors) {
		super();
		this.id = UUID.randomUUID().toString();
		this.entityID = entityID;
		this.selectors = Collections.unmodifiableMap(selectors);
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
