package cz.cuni.mff.d3s.been.cluster.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.core.task.Task;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.Template;

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

	public void submit(TaskContextDescriptor descriptor) {

		TaskContextEntry taskContextEntry = new TaskContextEntry();
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

			// TODO properties

			TaskEntry taskEntry = TaskEntries.create(td, taskContextEntry.getId());
			taskEntry.setTaskContextId(taskContextEntry.getId());

			taskContextEntry.getContainedTask().add(taskEntry.getId());
			entriesToSubmit.add(taskEntry);
		}

		getTaskContextsMap().put(taskContextEntry.getId(), taskContextEntry);

		for (TaskEntry taskEntry : entriesToSubmit) {
			clusterContext.getTasksUtils().submit(taskEntry);
		}

		log.info("Task context was submitted with ID {}", taskContextEntry.getId());
	}

	private TaskDescriptor cloneTemplateWithName(
			TaskContextDescriptor descriptor, String templateName) {
		for (Template t : descriptor.getTemplates().getTemplate()) {
			if (t.getName().equals(templateName)) {
				return (TaskDescriptor) t.getTaskDescriptor().createCopy();
			}
		}

		throw new IllegalArgumentException(String.format(
				"Cannot find template with name '%s'",
				templateName));
	}

}
