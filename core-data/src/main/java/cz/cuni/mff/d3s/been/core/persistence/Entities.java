package cz.cuni.mff.d3s.been.core.persistence;

/**
 * This class holds {@link EntityID} constants associated with storage destinations
 *
 * @author darklight
 */
public enum Entities {
	LOG_SERVICE("log", "service"),

	LOG_TASK("log", "task"),

	RESULT_EVALUATOR("result", "evaluation"),

	OUTCOME_TASK("outcome", "task"),

	OUTCOME_CONTEXT("outcome", "task"),

	LOG_MONITORING("log", "monitoring"),
	;

	private final String kind;
	private final String group;

	private Entities(String kind, String group) {
		this.kind = kind;
		this.group = group;
	}

	/**
	 * Get a new {@link EntityID} for this type
	 *
	 * @return The Entity ID
	 */
	public EntityID getId() {
		return new EntityID().withKind(kind).withGroup(group);
	}

}
