package cz.cuni.mff.d3s.been.task.checkpoints;

import cz.cuni.mff.d3s.been.core.TaskPropertyNames;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;
import cz.cuni.mff.d3s.been.socketworks.twoway.Request;

/**
 * @author Martin Sixta
 */
public final class CheckpointRequest extends Request {
	private CheckpointRequestType type;
	private String taskId;
	private String taskContextId;

	public CheckpointRequest() {
		super();
	}

	public CheckpointRequest(CheckpointRequestType type, String selector) {
		super(selector);
		this.type = type;
	}

	public CheckpointRequest(CheckpointRequestType type, String selector, String value) {
		super(selector, value);
		this.type = type;
	}

	public CheckpointRequest(CheckpointRequestType type, String selector, long timeout) {
		super(selector, timeout);
		this.type = type;
	}

	public CheckpointRequest(CheckpointRequestType type, String selector, String value, long timeout) {
		super(selector, value, timeout);
		this.type = type;
	}

	public void fillInTaskAndContextId() {
		this.taskId = System.getenv(TaskPropertyNames.TASK_ID);
		this.taskContextId = System.getenv(TaskPropertyNames.CONTEXT_ID);
	}

	public static CheckpointRequest fromJson(String json) throws JsonException {
		return JSONUtils.newInstance().deserialize(json, CheckpointRequest.class);
	}

	public CheckpointRequestType getType() {
		return type;
	}

	public void setType(CheckpointRequestType type) {
		this.type = type;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskContextId() {
		return taskContextId;
	}

	public void setTaskContextId(String taskContextId) {
		this.taskContextId = taskContextId;
	}
}
