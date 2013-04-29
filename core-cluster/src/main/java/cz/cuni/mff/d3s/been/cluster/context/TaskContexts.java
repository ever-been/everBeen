package cz.cuni.mff.d3s.been.cluster.context;

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

	public TaskContextEntry putTaskContext(TaskContextEntry entry, long ttl,
			TimeUnit timeUnit) {
		return getTaskContextsMap().put(entry.getId(), entry, ttl, timeUnit);
	}

	public TaskContextEntry submit(TaskContextDescriptor descriptor) {

		TaskContextEntry taskContextEntry = new TaskContextEntry();
		taskContextEntry.setTaskContextDescriptor(descriptor);
		taskContextEntry.setId(UUID.randomUUID().toString());

		Collection<TaskEntry> entriesToSubmit = new ArrayList<>();

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

			TaskEntry taskEntry = TaskEntries.create(td, taskContextEntry.getId());
			taskEntry.setTaskContextId(taskContextEntry.getId());

			taskContextEntry.getContainedTask().add(taskEntry.getId());
			entriesToSubmit.add(taskEntry);
		}

		taskContextEntry.setContextState(TaskContextState.RUNNING);
		getTaskContextsMap().put(taskContextEntry.getId(), taskContextEntry);

		for (TaskEntry taskEntry : entriesToSubmit) {
			clusterContext.getTasks().submit(taskEntry);
			log.info("Task was submitted with ID {}", taskEntry.getId());
		}

		log.info("Task context was submitted with ID {}", taskContextEntry.getId());

		return taskContextEntry;
	}

	private void setTaskProperties(TaskContextDescriptor descriptor, Task task,
			TaskDescriptor td) {
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

	private TaskDescriptor cloneTemplateWithName(
			TaskContextDescriptor descriptor, String templateName) {
		for (Template t : descriptor.getTemplates().getTemplate()) {
			if (t.getName().equals(templateName)) {
				return (TaskDescriptor) t.getTaskDescriptor().clone();
			}
		}

		throw new IllegalArgumentException(String.format(
				"Cannot find template with name '%s'",
				templateName));
	}

	/**
	 * Destroys allocated cluster-wide instances (checkpoint map, latches) for a
	 * given TaskContextEntry.
	 * 
	 * @param taskContextEntry
	 *          entry to clean up
	 */
	public void cleanupTaskContext(TaskContextEntry taskContextEntry) {
		log.info(
				"Destroying cluster instances for task context {}",
				taskContextEntry.getId());

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

		log.info(
				"Removing tasks contained in context {} after {} seconds",
				taskContextEntry.getId(),
				taskTtlSeconds);

		for (String taskId : taskContextEntry.getContainedTask()) {
			TaskEntry taskEntry = clusterContext.getTasks().getTask(taskId);
			clusterContext.getTasks().putTask(
					taskEntry,
					taskTtlSeconds,
					TimeUnit.SECONDS);
		}

		log.info(
				"Removing task context entry {} after {} seconds",
				taskContextEntry.getId(),
				contextTtlSeconds);

		putTaskContext(taskContextEntry, contextTtlSeconds, TimeUnit.SECONDS);
	}
}
