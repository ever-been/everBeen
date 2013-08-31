package cz.cuni.mff.d3s.been.objectrepository.janitor;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;

/**
 * Partial {@link EntityID} creators. Only works with delete queries!
 *
 * @author darklight
 */
enum CleanupEntities {
	LOG("log"),
	CONFIGURATION("configuration"),
	RESULT("result"),
	EVALUATION("evaluation"),
	OUTCOME("outcome"),
	LOAD_SAMPLE("log", "monitoring"),
	SERVICE_LOG("log", "service");

	private final String kind;
	private final String group;

	private CleanupEntities(String kind) {
		this.kind = kind;
		this.group = null;
	}

	private CleanupEntities(String kind, String group) {
		this.kind = kind;
		this.group = group;
	}

	public EntityID getId() {
		if (this.group == null) {
			return new EntityID().withKind(kind);
		} else {
			return new EntityID().withKind(kind).withGroup(group);
		}
	}
}
