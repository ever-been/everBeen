package cz.cuni.mff.d3s.been.persistence.task;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.persistence.NamedEntity;
import cz.cuni.mff.d3s.been.core.persistence.TaskEntity;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.QueryAnswer;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Facade for creation of persistent wrappers for configuration objects
 *
 * @author darklight
 */
public final class PersistentDescriptors {

    private static final JSONUtils jsonUtils = JSONUtils.newInstance();

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
	 * Create a persistent {@link cz.cuni.mff.d3s.been.core.task.TaskDescriptor} wrapper. The descriptor needs to be accompanied by runtime IDs of the task for which this descriptor will be submitted.
	 *
	 * @param td {@link cz.cuni.mff.d3s.been.core.task.TaskDescriptor} to wrap
	 * @param taskId ID of the task which is submitting this context descriptor
	 * @param contextId ID of the context in which the submitting task is running
	 * @param benchmarkId ID of the benchmark in which the submitting task is running
	 *
	 * @return A persistable {@link cz.cuni.mff.d3s.been.core.persistence.TaskEntity} wrapping provided {@link cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor}
	 */
	public static TaskEntity wrapTaskDescriptor(TaskDescriptor td, String taskId, String contextId, String benchmarkId) {
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
     * @param bpkId ID of the BPK to associate this descriptor with
	 *
	 * @return A persistable {@link NamedEntity} wrapping provided {@link cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor}
	 */
	public static NamedEntity wrapNamedTaskDescriptor(TaskDescriptor td, String name, BpkIdentifier bpkId) {
		final NamedPersistentTaskDescriptor wrap = new NamedPersistentTaskDescriptor();
		wrap.setDescriptor(td);
		wrap.setName(name);
        wrap.setBpkId(bpkId);
		return wrap;
	}

	/**
	 * Create a persistent {@link cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor} wrapper. The descriptor needs to be accompanied by runtime IDs of the task for which this descriptor will be submitted.
	 *
	 * @param tcd {@link cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor} to wrap
	 * @param taskId ID of the task which is submitting this context descriptor
	 * @param contextId ID of the context in which the submitting task is running
	 * @param benchmarkId ID of the benchmark in which the submitting task is running
	 *
	 * @return A persistable {@link cz.cuni.mff.d3s.been.core.persistence.TaskEntity} wrapping provided {@link cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor}
	 */
	public static TaskEntity wrapContextDescriptor(TaskContextDescriptor tcd, String taskId, String contextId, String benchmarkId) {
		final PersistentContextDescriptor wrap = new PersistentContextDescriptor();
		wrap.setDescriptor(tcd);
		setRuntimeIDs(wrap, taskId, contextId, benchmarkId);
		return wrap;
	}

	/**
	 * Create a persistent {@link TaskContextDescriptor} wrapper. The descriptor needs to be accompanied by runtime IDs of the task for which this descriptor will be submitted.
	 *
	 * @param tcd {@link TaskContextDescriptor} to wrap
	 * @param name Name with which to save the descriptor
     * @param bpkId Identifier of the BPK this named configuration should be associated with
	 *
	 * @return A persistable {@link NamedEntity} wrapping provided {@link TaskContextDescriptor}
	 */
	public static NamedEntity wrapNamedContextDescriptor(TaskContextDescriptor tcd, String name, BpkIdentifier bpkId) {
		final NamedPersistentContextDescriptor wrap = new NamedPersistentContextDescriptor();
		wrap.setDescriptor(tcd);
		wrap.setName(name);
        wrap.setBpkId(bpkId);
		return wrap;
	}

    /**
     * Unpack a data query answer into a {@link TaskDescriptor}
     *
     * @param answer Persistence answer
     *
     * @return The task descriptor
     *
     * @throws DAOException On persistence access failure or unmarshalling error
     */
    public static TaskDescriptor unpackTaskDescriptor(QueryAnswer answer) throws DAOException {
        assertAnswerIsData(answer);
        return extractExactlyOneElement(answer.getData(), PersistentTaskDescriptor.class).getDescriptor();
    }

    /**
     * Unpack a data query answer into a {@link TaskContextDescriptor}
     *
     * @param answer Persistence answer
     *
     * @return The context descriptor
     *
     * @throws DAOException On persistence access failure or unmarshalling error
     */
    public static TaskContextDescriptor unpackContextDescriptor(QueryAnswer answer) throws DAOException {
        assertAnswerIsData(answer);
        return extractExactlyOneElement(answer.getData(), PersistentContextDescriptor.class).getDescriptor();
    }

    /**
     * Unpack binding map for named task descriptors from a query answer.
     *
     * @param answer Answer to unpack
     *
     * @return A map of (name, descriptor) entries describing the bindings of named descriptors for given BPK
     *
     * @throws DAOException On persistence access failure or unmarshalling error
     */
    public static Map<String, TaskDescriptor> unpackNamedTaskDescriptors(QueryAnswer answer) throws DAOException {
        assertAnswerIsData(answer);
        final Map<String, TaskDescriptor> map = new HashMap<String, TaskDescriptor>();
        try {
	        for (NamedPersistentTaskDescriptor td: jsonUtils.deserialize(answer.getData(), NamedPersistentTaskDescriptor.class)) {
                map.put(td.getName(), td.getDescriptor());
	        }
            return map;
        } catch (JsonException e) {
            throw new DAOException("Deserialization failed", e);
        }
    }

    /**
     * Unpack binding map for named context descriptors from a query answer.
     *
     * @param answer Answer to unpack
     *
     * @return A map of (name, descriptor) entries describing the bindings of named descriptors for given BPK
     *
     * @throws DAOException On persistence access failure or unmarshalling error
     */
    public static Map<String, TaskContextDescriptor> unpackNamedContextDescriptors(QueryAnswer answer) throws DAOException {
        assertAnswerIsData(answer);
        final Map<String, TaskContextDescriptor> map = new HashMap<String, TaskContextDescriptor>();
        try {
            for (NamedPersistentContextDescriptor td: jsonUtils.deserialize(answer.getData(), NamedPersistentContextDescriptor.class)) {
                map.put(td.getName(), td.getDescriptor());
            }
            return map;
        } catch (JsonException e) {
            throw new DAOException("Deserialization failed", e);
        }
    }

    /**
     * Serialize a BPK identifier into JSON.
     *
     * @param bpkIdentifier BPK identifier to serialize
     *
     * @return A JSON representation of the provided BPK identifier
     */
    public static String serializeBpkId(BpkIdentifier bpkIdentifier) throws DAOException {
        try {
            synchronized (jsonUtils) {
                return jsonUtils.serialize(bpkIdentifier);
            }
        } catch (JsonException e) {
            throw new DAOException("Cannot serialize BPK identifier", e);
        }
    }

    private static <T> Collection<T> extractAllElements(Collection<String> jsonData, Class<T> unmarshalType) throws DAOException {
        if (jsonData.size() != 1) {
            throw new DAOException("Response doesn't contain exactly one element");
        }
        try {
            synchronized (jsonUtils) {
                return jsonUtils.deserialize(jsonData, unmarshalType);
            }
        } catch (JsonException e) {
            throw new DAOException("Descriptor unmarshalling failed", e);
        }
    }

    private static <T> T extractExactlyOneElement(Collection<String> jsonData, Class<T> unmarshalType) throws DAOException {
        if (jsonData.size() != 1) {
            throw new DAOException("Response doesn't contain exactly one element");
        }
        try {
            synchronized (jsonUtils) {
                final Collection<T> unmarshalled = jsonUtils.deserialize(jsonData, unmarshalType);
                return unmarshalled.iterator().next();
            }
        } catch (JsonException e) {
            throw new DAOException("Descriptor unmarshalling failed", e);
        }
    }

    private static void assertAnswerIsData(QueryAnswer answer) throws DAOException {
        if (!answer.isCarryingData()) {
            throw new DAOException(String.format("No data in answer: %s", answer.getStatus().getDescription()));
        }
    }

	private static void setRuntimeIDs(TaskEntity to, String taskId, String contextId, String benchmarkId) {
		to.setTaskId(taskId);
		to.setContextId(contextId);
		to.setBenchmarkId(benchmarkId);
	}
}
