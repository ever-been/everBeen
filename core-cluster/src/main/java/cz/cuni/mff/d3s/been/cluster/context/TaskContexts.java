package cz.cuni.mff.d3s.been.cluster.context;

import static cz.cuni.mff.d3s.been.cluster.Names.BENCHMARKS_CONTEXT_ID;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;
import com.hazelcast.core.Instance;

import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.core.persistence.TaskEntity;
import cz.cuni.mff.d3s.been.core.task.*;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.task.PersistentDescriptors;

/**
 * Utility class for operations related to task contexts.
 * 
 * @author Kuba Brecka
 */
public class TaskContexts {

	/** slf4j logger */
	private static final Logger log = LoggerFactory.getLogger(TaskContexts.class);

	/** BEEN cluster connection */
	private ClusterContext clusterContext;

	/**
	 * Package private constructor, creates a new instance that uses the specified
	 * BEEN cluster context.
	 * 
	 * @param clusterContext
	 *          the cluster context to use
	 */
	TaskContexts(ClusterContext clusterContext) {
		// package private visibility prevents out-of-package instantiation
		this.clusterContext = clusterContext;
	}

	/**
	 * Returns the map which holds task context entries.
	 * 
	 * @return the task contexts map
	 */
	public IMap<String, TaskContextEntry> getTaskContextsMap() {
		return clusterContext.getMap(Names.TASK_CONTEXTS_MAP_NAME);
	}

	/**
	 * Returns a task context with the specified ID.
	 * 
	 * @param id
	 *          ID of the task context to retrieve
	 * @return the task context entry with the specified ID or null if no such
	 *         task context exists
	 */
	public TaskContextEntry getTaskContext(String id) {
		return getTaskContextsMap().get(id);
	}

	/**
	 * Returns a collection of all available task contexts in the Hazelcast map.
	 * 
	 * @return all task contexts
	 */
	public Collection<TaskContextEntry> getTaskContexts() {
		return getTaskContextsMap().values();
	}

	/**
	 * Puts/updates the entry about the specified task context in the Hazelcast
	 * map.
	 * 
	 * @param entry
	 *          the task context entry to put
	 */
	public void putContextEntry(TaskContextEntry entry) {
		getTaskContextsMap().put(entry.getId(), entry);
	}

	/**
	 * Submits a context to the cluster.
	 * <p/>
	 * Submit does not create or schedule any tasks. Tasks will be created and
	 * scheduled by the framework some time later.
	 * 
	 * @param descriptor
	 *          descriptor of a context
	 * @param benchmarkId
	 *          ID of the benchmark under which the task context should be created
	 * @return id of the submitted context
	 */
	public String submit(TaskContextDescriptor descriptor, String benchmarkId) {

		TaskContextEntry contextEntry = new TaskContextEntry();
		contextEntry.setTaskContextDescriptor(descriptor);
		contextEntry.setId(UUID.randomUUID().toString());
		contextEntry.setBenchmarkId(benchmarkId);
		contextEntry.setContextState(TaskContextState.WAITING);
		contextEntry.setCreated(new Date().getTime());

		checkContextBeforeSubmit(contextEntry);

		putContextEntry(contextEntry);
		log.debug("Task context was submitted with ID {}", contextEntry.getId());

		persistContextDescriptor(descriptor, benchmarkId, contextEntry);

		return contextEntry.getId();
	}

	/**
	 * Stores the specified context descriptor into the persistence layer.
	 * 
	 * @param descriptor
	 *          the descriptor to persist
	 * @param benchmarkId
	 *          the benchmark ID under which the descriptor was submitted
	 * @param contextEntry
	 *          the task context entry of the submitted context
	 */
	private void persistContextDescriptor(TaskContextDescriptor descriptor, String benchmarkId,
			TaskContextEntry contextEntry) {
		final TaskEntity entity = PersistentDescriptors.wrapContextDescriptor(
				descriptor,
				null,
				contextEntry.getId(),
				benchmarkId);
		try {
			clusterContext.getPersistence().asyncPersist(PersistentDescriptors.CONTEXT_DESCRIPTOR, entity);
		} catch (DAOException e) {
			log.error("Persisting context descriptor failed.", e);
			// continues without rethrowing, because the only reason for a DAOException is when
			// the object cannot be serialized.
		}
	}

	/**
	 * Performs various sanity checks of the task context entry and throws an
	 * exception when an invalid state is found.
	 * 
	 * @param contextEntry
	 *          the task context entry to check
	 */
	private void checkContextBeforeSubmit(TaskContextEntry contextEntry) {
		TaskContextDescriptor descriptor = contextEntry.getTaskContextDescriptor();

		Collection<TaskEntry> entriesToSubmit = getTaskEntries(contextEntry);
		Map<String, TaskEntry> entries = new HashMap<>();

		for (TaskEntry e : entriesToSubmit) {
			if (e.getTaskDescriptor().getType() != TaskType.TASK) {
				throw new IllegalArgumentException("Task context contains a TaskDescriptor with a type that is not task.");
			}

			TaskDescriptor taskDescriptor = e.getTaskDescriptor();
			entries.put(taskDescriptor.getName(), e);
		}

		for (Task task : descriptor.getTask()) {
			if (task.isSetRunAfterTask()) {
				String runAfter = task.getRunAfterTask();
				String taskName = task.getName();

				if (taskName.equals(runAfter)) {
					String msg = String.format("Cannot wait for itself to finish: %s", taskName);
					throw new IllegalArgumentException();
				}

				if (entries.get(runAfter) == null) {
					String msg = String.format("No such task to wait for %s", runAfter);
					throw new IllegalArgumentException(msg);
				}
			}
		}

		// TODO check for cycles

	}

	/**
	 * Submits a single task descriptor in a new task context. Since all tasks
	 * must belong to some task context, this is the preferred way to submit
	 * single tasks.
	 * 
	 * @param taskDescriptor
	 *          the task descriptor to submit
	 * @return the ID of the newly created task context
	 */
	public String submitTaskInNewContext(TaskDescriptor taskDescriptor) {
		if (taskDescriptor.getType() != TaskType.TASK) {
			throw new IllegalArgumentException("TaskDescriptor's type is not task.");
		}

		TaskContextDescriptor contextDescriptor = new TaskContextDescriptor();
		Task taskInTaskContext = new Task();
		taskInTaskContext.setName(taskDescriptor.getName());
		Descriptor descriptorInTaskContext = new Descriptor();
		descriptorInTaskContext.setTaskDescriptor(taskDescriptor);
		taskInTaskContext.setDescriptor(descriptorInTaskContext);
		contextDescriptor.getTask().add(taskInTaskContext);

		return submit(contextDescriptor, null);
	}

	/**
	 * Submits a benchmark with the specified task descriptor describing the
	 * benchmark generator task. This method can also be used for resubmitting a
	 * generator task. When resubmitting, specify a 'benchmarkId' parameter and
	 * set it to the ID of the benchmark for which the generator is being
	 * resubmitted. When submitting a new benchmark, set the 'benchmarkId' to the
	 * newly created benchmark entry.
	 * 
	 * @param benchmarkTaskDescriptor
	 *          generator task descriptor
	 * @param benchmarkId
	 *          the ID of the benchmark
	 * @return the ID of the newly submitted task
	 */
	public String submitBenchmarkTask(TaskDescriptor benchmarkTaskDescriptor, String benchmarkId) {
		if (benchmarkTaskDescriptor.getType() != TaskType.BENCHMARK) {
			throw new IllegalArgumentException("TaskDescriptor's type is not benchmark.");
		}

		TaskEntry taskEntry = TaskEntries.create(benchmarkTaskDescriptor, BENCHMARKS_CONTEXT_ID);
		taskEntry.setBenchmarkId(benchmarkId);
		String taskId = clusterContext.getTasks().submit(taskEntry);
		clusterContext.getBenchmarks().addBenchmarkToBenchmarksContext(taskEntry);

		return taskEntry.getId();
	}

	/**
	 * Creates a collection of task entries from the descriptor in the specified
	 * task context entry. This method will parse the context descriptor and
	 * create tasks from the templates and put these tasks into the Hazelcast map.
	 * 
	 * @param taskContextEntry
	 *          the task context entry from which the task entries should be
	 *          created
	 * @return a collection of the newly creates tasks
	 */
	private Collection<TaskEntry> getTaskEntries(TaskContextEntry taskContextEntry) {
		TaskContextDescriptor descriptor = taskContextEntry.getTaskContextDescriptor();
		String contextId = taskContextEntry.getId();

		Collection<TaskEntry> entries = new ArrayList<>();
		Map<String, String> nameToId = new HashMap<>();

		for (Task t : descriptor.getTask()) {

			TaskDescriptor td;
			if (t.getDescriptor().isSetTaskDescriptor()) {
				td = t.getDescriptor().getTaskDescriptor();
			} else {
				String templateName = t.getDescriptor().getFromTemplate();
				td = cloneTemplateWithName(descriptor, templateName);
			}

			td.setName(t.getName());
			setTaskProperties(descriptor, t, td);

			TaskEntry taskEntry = TaskEntries.create(td, contextId);

			if (t.isSetRunAfterTask()) {
				taskEntry.setTaskDependency(t.getRunAfterTask());
			}
			nameToId.put(t.getName(), taskEntry.getId());
			entries.add(taskEntry);
		}

		// map names to ids
		for (TaskEntry entry : entries) {
			if (entry.isSetTaskDependency()) {
				final String dependsOn = entry.getTaskDependency();
				entry.setTaskDependency(nameToId.get(dependsOn));
			}
		}

		return entries;

	}

	/**
	 * Runs the specified context. This will submit all the contained tasks and
	 * set the state of the task context to running.
	 * 
	 * @param contextId
	 *          the ID of the context to run
	 */
	public void runContext(String contextId) {
		TaskContextEntry contextEntry = getTaskContext(contextId);
		if (contextEntry == null) {
			// TODO
			return;
		}

		Collection<TaskEntry> entriesToSubmit = getTaskEntries(contextEntry);

		// TODO consider transactions
		for (TaskEntry taskEntry : entriesToSubmit) {
			taskEntry.setBenchmarkId(contextEntry.getBenchmarkId());
			String taskId = clusterContext.getTasks().submit(taskEntry);
			contextEntry.getContainedTask().add(taskId);
			log.debug("Task was submitted with ID {}", taskId);
		}

		contextEntry.setContextState(TaskContextState.RUNNING);
		putContextEntry(contextEntry);

	}

	/**
	 * Propagates properties set in the context descriptor to the individual task.
	 * 
	 * @param descriptor
	 *          the context descriptor from which the properties should be
	 *          extracted
	 * @param task
	 *          the task entry in the context descriptor
	 * @param td
	 *          the task descriptor into which the properties should be set
	 */
	private void setTaskProperties(TaskContextDescriptor descriptor, Task task, TaskDescriptor td) {
		HashMap<String, String> properties = new HashMap<>();

		if (descriptor.isSetProperties()) {
			for (Property property : descriptor.getProperties().getProperty()) {
				properties.put(property.getName(), property.getValue());
			}
		}

		if (td.isSetProperties()) {
			for (TaskProperty property : td.getProperties().getProperty()) {
				properties.put(property.getName(), property.getValue());
			}
		}

		if (task.isSetProperties()) {
			for (Property property : task.getProperties().getProperty()) {
				properties.put(property.getName(), property.getValue());
			}
		}

		if (!td.isSetProperties()) {
			td.setProperties(new TaskProperties());
		}

		td.getProperties().getProperty().clear();
		for (Map.Entry<String, String> property : properties.entrySet()) {
			TaskProperty taskProperty = new TaskProperty();
			taskProperty.setName(property.getKey());
			taskProperty.setValue(property.getValue());
			td.getProperties().getProperty().add(taskProperty);
		}
	}

	/**
	 * Clones a task template from a context descriptor.
	 * 
	 * @param descriptor
	 *          the context descriptor
	 * @param templateName
	 *          the name of the template that should be cloned
	 * @return the cloned task descriptor
	 */
	private TaskDescriptor cloneTemplateWithName(TaskContextDescriptor descriptor, String templateName) {
		for (Template t : descriptor.getTemplates().getTemplate()) {
			if (t.getName().equals(templateName)) {
				return (TaskDescriptor) t.getTaskDescriptor().clone();
			}
		}

		throw new IllegalArgumentException(String.format("Cannot find template with name '%s'", templateName));
	}

	/**
	 * Destroys allocated cluster-wide instances (checkpoint map and latches) for
	 * a given TaskContextEntry.
	 * 
	 * @param taskContextEntry
	 *          entry to clean up
	 */
	public void cleanupTaskContext(TaskContextEntry taskContextEntry) {
		log.debug("Destroying cluster instances for task context {}", taskContextEntry.getId());

		// destroy the checkpoint map
		clusterContext.getMap("checkpointmap_" + taskContextEntry.getId()).destroy();

		// destroy latches
		Collection<Instance> latches = clusterContext.getInstances(Instance.InstanceType.COUNT_DOWN_LATCH);
		for (Instance instance : latches) {
			String latchName = instance.getId().toString();
			if (latchName.startsWith("d:latch_" + taskContextEntry.getId() + "_")) {
				instance.destroy();
			}
		}

		putContextEntry(taskContextEntry);
	}

	/**
	 * Removes the task context with the specified ID from Hazelcast map of tasks
	 * contexts. The task context must be in a final state (finished). Also
	 * removes all tasks contained in the context.
	 * 
	 * @param taskContextId
	 *          ID of the task context to remove
	 */
	public void remove(String taskContextId) {
		TaskContextEntry taskContextEntry = getTaskContext(taskContextId);

		TaskContextState state = taskContextEntry.getContextState();
		if (state == TaskContextState.FINISHED || state == TaskContextState.FAILED) {
			log.info("Removing task context {} from map.", taskContextId);

			for (String taskId : taskContextEntry.getContainedTask()) {
				clusterContext.getTasks().remove(taskId);
			}

			getTaskContextsMap().remove(taskContextId);
		} else {
			throw new IllegalStateException(String.format(
					"Trying to remove task context %s, but it's in state %s.",
					taskContextId,
					state));
		}
	}

	/**
	 * Returns a list of all available tasks in the specified task context.
	 * 
	 * @param taskContextId
	 *          the ID of the task context
	 * @return collection of task entries within the context
	 */
	public Collection<TaskEntry> getTasksInTaskContext(String taskContextId) {
		TaskContextEntry taskContextEntry = getTaskContext(taskContextId);

		Collection<TaskEntry> result = new ArrayList<>();
		for (String taskId : taskContextEntry.getContainedTask()) {
			TaskEntry entry = clusterContext.getTasks().getTask(taskId);
			if (entry != null) {
				result.add(entry);
			}
		}

		return result;
	}

	/**
	 * Kills the specified context (i.e. kills all the contained tasks). This
	 * method only submits the "killing" operation and returns immediately. There
	 * is no guarantee about when the tasks or the context will actually be
	 * killed.
	 * 
	 * @param taskContextId
	 *          the ID of the task context to kill
	 */
	public void kill(String taskContextId) {
		TaskContextEntry taskContextEntry = getTaskContext(taskContextId);
		TaskContextState state = taskContextEntry.getContextState();

		if (state == TaskContextState.FAILED || state == TaskContextState.FINISHED) {
			throw new IllegalStateException(String.format(
					"Trying to kill task context %s, but it's in state %s.",
					taskContextId,
					state));
		}

		List<String> containedTasks = taskContextEntry.getContainedTask();

		for (String taskId : containedTasks) {
			clusterContext.getTasks().kill(taskId);
		}
	}

}
