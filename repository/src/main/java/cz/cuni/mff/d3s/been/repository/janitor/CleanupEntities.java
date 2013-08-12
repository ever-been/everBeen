package cz.cuni.mff.d3s.been.repository.janitor;

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
	OUTCOME("outcome");

	private final String kind;

	CleanupEntities(String kind) {
		this.kind = kind;
	}

	public EntityID getId() {
		return new EntityID().withKind(kind);
	}
}
