package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The protocol object resulting of the submission of a {@link cz.cuni.mff.d3s.been.core.task.TaskDescriptor}
 *
 * @author darklight
 */
public class TaskSubmit implements ProtocolObject {
	@JsonProperty("taskId")
	private final String taskId;

	@JsonCreator
	public TaskSubmit(@JsonProperty("taskId") String taskId) {
		this.taskId = taskId;
	}

	public static TaskSubmit fromId(String taskId) {
		return new TaskSubmit(taskId);
	}

	public String getTaskId() {
		return taskId;
	}
}
