package cz.cuni.mff.d3s.been.cluster.context;

import cz.cuni.mff.d3s.been.BeenServiceConfiguration;

/**
 * @author Kuba Brecka
 */
public class TaskContextsConfiguration extends BeenServiceConfiguration {
	private TaskContextsConfiguration() { }

	/**
	 * The time-to-live in seconds after which a successfully finished task entry is removed from Hazelcast map.
	 * Tasks that failed are not removed automatically. When the value is 0, the entries are never deleted
	 * automatically.
	 */
	public static final String CONTEXT_EVICTION_TTL = "been.cluster.context.eviction-ttl";
	/** The default eviction TTL for tasks is {@value #DEFAULT_CONTEXT_EVICTION_TTL} seconds */
	public static final int DEFAULT_CONTEXT_EVICTION_TTL = 300;

	/**
	 * The time-to-live in seconds after which a successfully finished task context entry is removed
	 * from Hazelcast map. Task contexts that failed are not removed automatically. When the value is 0, the entries
	 * are never deleted automatically.
	 */
	public static final String TASK_EVICTION_TTL = "been.cluster.task.eviction-ttl";
	/** The default eviction TTL for task contexts is {@value #DEFAULT_CONTEXT_EVICTION_TTL} seconds */
	public static final int DEFAULT_TASK_EVICTION_TTL = 300;
}
