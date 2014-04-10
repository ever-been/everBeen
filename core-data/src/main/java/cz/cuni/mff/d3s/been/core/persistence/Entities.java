package cz.cuni.mff.d3s.been.core.persistence;

/**
 * This class holds {@link EntityID} constants associated with storage
 * destinations
 * 
 * @author darklight
 */
public enum Entities {

	/**
	 * Service logs.
	 */
	LOG_SERVICE("log", "service"),

	/**
	 * Task logs.
	 */
	LOG_TASK("log", "task"),

	/**
	 * Evaluator results.
	 */
	RESULT_EVALUATOR("result", "evaluation"),

	/**
	 * Final states of tasks.
	 */
	OUTCOME_TASK("outcome", "task"),

	/**
	 * Final state of contexts.
	 */
	OUTCOME_CONTEXT("outcome", "context"),

	/**
	 * Monitoring logs.
	 */
	LOG_MONITORING("log", "monitoring");

	private final String kind;
	private final String group;

	/**
	 * Creates new entity.
	 * 
	 * @param kind
	 *          kind of the entity
	 * @param group
	 *          group of the entity
	 */
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
