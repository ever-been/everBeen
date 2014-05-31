package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Status of a submitted everBeen task.
 *
 * @author darklight
 */
public class TaskStatus implements ProtocolObject {
	@JsonProperty("benchmarkId")
	private final String benchmarkId;
	@JsonProperty("contextId")
	private final String contextId;
	@JsonProperty("taskId")
	private final String taskId;
	@JsonProperty("taskState")
	private final String taskState;

	@JsonCreator
	public TaskStatus(
		@JsonProperty("benchmarkId") String benchmarkId,
		@JsonProperty("contextId") String contextId,
		@JsonProperty("taskId") String taskId,
		@JsonProperty("taskState") String taskState
	) {
		this.benchmarkId = benchmarkId;
		this.contextId = contextId;
		this.taskId = taskId;
		this.taskState = taskState;
	}

	public String getBenchmarkId() {
		return benchmarkId;
	}

	public String getContextId() {
		return contextId;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getTaskState() {
		return taskState;
	}
}
