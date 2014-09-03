package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The protocol object resulting of the submission of a Task Descriptor.
 *
 * @author darklight
 */
public class TaskSubmit implements ProtocolObject {
	@JsonProperty("taskId")
	private final String taskId;
	@JsonProperty("contextId")
	private final String contextId;

	@JsonCreator
	public TaskSubmit(
		@JsonProperty("taskId") String taskId,
		@JsonProperty("contextId") String contextId
	) {
		this.taskId = taskId;
		this.contextId = contextId;
	}

	public static TaskSubmit fromContextId(String contextId) {
		return new TaskSubmit(null, contextId);
	}

	public String getTaskId() {
		return taskId;
	}

	public String getContextId() {
		return contextId;
	}
}
