package cz.cuni.mff.d3s.been.core.task;

import cz.cuni.mff.d3s.been.core.persistence.Entity;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;

/**
 * Facade for creation of persistent wrappers for configuration objects
 *
 * @author darklight
 */
public final class PersistentDescriptors {

	/*
	 * Entity ID for task descriptors
	 */
	public static final EntityID TASK_DESCRIPTOR = new EntityID().withKind("descriptor").withGroup("task");

	/*
	 * Entity ID for named task descriptors
	 */
	public static final EntityID NAMED_TASK_DESCRIPTOR = new EntityID().withKind("named-descriptor").withGroup("task");

	/**
	 * EntityID for context descriptors
	 */
	public static final EntityID CONTEXT_DESCRIPTOR = new EntityID().withKind("descriptor").withGroup("context");

	/**
	 * EntityID for context descriptors
	 */
	public static final EntityID NAMED_CONTEXT_DESCRIPTOR = new EntityID().withKind("named-descriptor").withGroup("context");

	/**
	 * Create a persistent {@link TaskDescriptor} wrapper. The descriptor needs to be accompanied by runtime IDs of the task for which this descriptor will be submitted.
	 *
	 * @param td {@link TaskDescriptor} to wrap
	 * @param taskId ID of the task which is submitting this context descriptor
	 * @param contextId ID of the context in which the submitting task is running
	 * @param benchmarkId ID of the benchmark in which the submitting task is running
	 *
	 * @return A persistable {@link Entity} wrapping provided {@link TaskContextDescriptor}
	 */
	public static Entity wrapTaskDescriptor(TaskDescriptor td, String taskId, String contextId, String benchmarkId) {
		final PersistentTaskDescriptor wrap = new PersistentTaskDescriptor();
		wrap.setDescriptor(td);
		setRuntimeIDs(wrap, taskId, contextId, benchmarkId);
		return wrap;
	}

	/**
	 * Create a named persistent {@link TaskDescriptor} wrapper. The descriptor needs to be accompanied by runtime IDs of the task for which this descriptor will be submitted.
	 *
	 * @param td {@link TaskDescriptor} to wrap
	 * @param name Name with which to save the descriptor
	 * @param taskId ID of the task which is submitting this context descriptor
	 * @param contextId ID of the context in which the submitting task is running
	 * @param benchmarkId ID of the benchmark in which the submitting task is running
	 *
	 * @return A persistable {@link Entity} wrapping provided {@link TaskContextDescriptor}
	 */
	public static Entity wrapNamedTaskDescriptor(TaskDescriptor td, String name, String taskId, String contextId, String benchmarkId) {
		final NamedPersistentTaskDescriptor wrap = new NamedPersistentTaskDescriptor();
		wrap.setDescriptor(td);
		wrap.setName(name);
		setRuntimeIDs(wrap, taskId, contextId, benchmarkId);
		return wrap;
	}

	/**
	 * Create a persistent {@link TaskContextDescriptor} wrapper. The descriptor needs to be accompanied by runtime IDs of the task for which this descriptor will be submitted.
	 *
	 * @param tcd {@link TaskContextDescriptor} to wrap
	 * @param taskId ID of the task which is submitting this context descriptor
	 * @param contextId ID of the context in which the submitting task is running
	 * @param benchmarkId ID of the benchmark in which the submitting task is running
	 *
	 * @return A persistable {@link Entity} wrapping provided {@link TaskContextDescriptor}
	 */
	public static Entity wrapContextDescriptor(TaskContextDescriptor tcd, String taskId, String contextId, String benchmarkId) {
		final PersistentContextDescriptor wrap = new PersistentContextDescriptor();
		wrap.setContextDescriptor(tcd);
		setRuntimeIDs(wrap, taskId, contextId, benchmarkId);
		return wrap;
	}

	/**
	 * Create a persistent {@link TaskContextDescriptor} wrapper. The descriptor needs to be accompanied by runtime IDs of the task for which this descriptor will be submitted.
	 *
	 * @param tcd {@link TaskContextDescriptor} to wrap
	 * @param name Name with which to save the descriptor
	 * @param taskId ID of the task which is submitting this context descriptor
	 * @param contextId ID of the context in which the submitting task is running
	 * @param benchmarkId ID of the benchmark in which the submitting task is running
	 *
	 * @return A persistable {@link Entity} wrapping provided {@link TaskContextDescriptor}
	 */
	public static Entity wrapNamedContextDescriptor(TaskContextDescriptor tcd, String name, String taskId, String contextId, String benchmarkId) {
		final NamedPersistentContextDescriptor wrap = new NamedPersistentContextDescriptor();
		wrap.setContextDescriptor(tcd);
		wrap.setName(name);
		setRuntimeIDs(wrap, taskId, contextId, benchmarkId);
		return wrap;
	}

	private static void setRuntimeIDs(Entity to, String taskId, String contextId, String benchmarkId) {
		to.setTaskId(taskId);
		to.setContextId(contextId);
		to.setBenchmarkId(benchmarkId);
	}
}
