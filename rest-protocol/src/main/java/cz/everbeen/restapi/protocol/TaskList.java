package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;

import java.util.Collection;
import java.util.Collections;

/**
 * Protocol object representing the status of multiple tasks in the cluster
 *
 * @author darklight
 */
public class TaskList implements ProtocolObject {
	private final Collection<TaskStatus> tasks;

	@JsonCreator
	public TaskList(Collection<TaskStatus> tasks) {
		this.tasks = Collections.unmodifiableCollection(tasks);
	}

	public Collection<TaskStatus> getTasks() {
		return tasks;
	}
}
