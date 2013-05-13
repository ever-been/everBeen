package cz.cuni.mff.d3s.been.cluster.context;

import static cz.cuni.mff.d3s.been.cluster.Names.BENCHMARKS_CONTEXT_ID;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;
import com.hazelcast.core.Instance;

import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.core.SystemProperties;
import cz.cuni.mff.d3s.been.core.task.*;

/**
 * Created with IntelliJ IDEA. User: Kuba Date: 20.04.13 Time: 13:09 To change
 * this template use File | Settings | File Templates.
 */
public class TaskContexts {

	private static final Logger log = LoggerFactory.getLogger(TaskContexts.class);

	private ClusterContext clusterContext;

	TaskContexts(ClusterContext clusterContext) {
		// package private visibility prevents out-of-package instantiation
		this.clusterContext = clusterContext;
	}

	public IMap<String, TaskContextEntry> getTaskContextsMap() {
		return clusterContext.getMap(Names.TASK_CONTEXTS_MAP_NAME);
	}

	public TaskContextEntry getTaskContext(String id) {
		return getTaskContextsMap().get(id);
	}

	public Collection<TaskContextEntry> getTaskContexts() {
		return getTaskContextsMap().values();
	}

	public TaskContextEntry putTaskContext(TaskContextEntry entry, long ttl, TimeUnit timeUnit) {
		return getTaskContextsMap().put(entry.getId(), entry, ttl, timeUnit);
	}

	public void putContextEntry(TaskContextEntry entry) {
		getTaskContextsMap().put(entry.getId(), entry);
	}

	/**
	 * Submits a context to the cluster.
	 * 
	 * Submit does not create or schedule any tasks. Tasks will be created and
	 * scheduled by the framework some time later.
	 * 
	 * @param descriptor
	 *          descriptor of a context
	 * 
	 * @return id of the submitted context
	 * 
	 */
	public String submit(TaskContextDescriptor descriptor, String benchmarkId) {

		TaskContextEntry contextEntry = new TaskContextEntry();
		contextEntry.setTaskContextDescriptor(descriptor);
		contextEntry.setId(UUID.randomUUID().toString());
		contextEntry.setBenchmarkId(benchmarkId);
		contextEntry.setContextState(TaskContextState.WAITING);

		checkContextBeforeSubmit(contextEntry);

		putContextEntry(contextEntry);
		log.debug("Task context was submitted with ID {}", contextEntry.getId());

		return contextEntry.getId();
	}

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

	private TaskDescriptor cloneTemplateWithName(TaskContextDescriptor descriptor, String templateName) {
		for (Template t : descriptor.getTemplates().getTemplate()) {
			if (t.getName().equals(templateName)) {
				return (TaskDescriptor) t.getTaskDescriptor().clone();
			}
		}

		throw new IllegalArgumentException(String.format("Cannot find template with name '%s'", templateName));
	}

	/**
	 * Destroys allocated cluster-wide instances (checkpoint map, latches) for a
	 * given TaskContextEntry.
	 * 
	 * @param taskContextEntry
	 *          entry to clean up
	 */
	public void cleanupTaskContext(TaskContextEntry taskContextEntry) {
		log.info("Destroying cluster instances for task context {}", taskContextEntry.getId());

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

		int contextTtlSeconds = SystemProperties.getInteger("been.context.ttl", 300);
		int taskTtlSeconds = SystemProperties.getInteger("been.task.ttl", 300);

		log.info("Removing tasks contained in context {} after {} seconds", taskContextEntry.getId(), taskTtlSeconds);

		for (String taskId : taskContextEntry.getContainedTask()) {
			TaskEntry taskEntry = clusterContext.getTasks().getTask(taskId);
			clusterContext.getTasks().putTask(taskEntry, taskTtlSeconds, TimeUnit.SECONDS);
		}

		log.info("Removing task context entry {} after {} seconds", taskContextEntry.getId(), contextTtlSeconds);

		putTaskContext(taskContextEntry, contextTtlSeconds, TimeUnit.SECONDS);
	}

	/**
	 * Removes the task context with the specified ID from Hazelcast map of tasks contexts.
	 * The task context must be in a final state (finished). Also removes all tasks contained
	 * in the context.
	 *
	 * @param taskContextId ID of the task context to remove
	 */
	public void remove(String taskContextId) {
		TaskContextEntry taskContextEntry = getTaskContext(taskContextId);

		TaskContextState state = taskContextEntry.getContextState();
		if (state == TaskContextState.FINISHED) {
			log.info("Removing task context {} from map.", taskContextId);

			for (String taskId : taskContextEntry.getContainedTask()) {
				clusterContext.getTasks().remove(taskId);
			}

			getTaskContextsMap().remove(taskContextId);
		} else {
			throw new IllegalStateException(String.format("Trying to remove task context %s, but it's in state %s.", taskContextId, state));
		}
	}

	public Collection<TaskEntry> getTasksInTaskContext(String taskContextId) {
		TaskContextEntry taskContextEntry = getTaskContext(taskContextId);

		Collection<TaskEntry> result = new ArrayList<>();
		for (String taskId : taskContextEntry.getContainedTask()) {
			TaskEntry entry = clusterContext.getTasks().getTask(taskId);
			result.add(entry);
		}

		return result;
	}
}
