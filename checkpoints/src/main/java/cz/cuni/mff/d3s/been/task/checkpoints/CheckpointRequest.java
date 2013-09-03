package cz.cuni.mff.d3s.been.task.checkpoints;

import cz.cuni.mff.d3s.been.core.TaskPropertyNames;
import cz.cuni.mff.d3s.been.socketworks.twoway.Request;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * Class representing a simple checkpoint request (wait, get, etc.)
 * 
 * @author Martin Sixta
 */
public final class CheckpointRequest extends Request {

	/** type of the request */
	private CheckpointRequestType type;

	/** ID of the requesting task */
	private String taskId;

	/** ID of the requesting task context */
	private String taskContextId;

	/**
	 * Constructs a new empty request.
	 */
	public CheckpointRequest() {
		super();
	}

	/**
	 * Constructs a new request with the specified type and selector.
	 * 
	 * @param type
	 *          the type of the checkpoint request
	 * @param selector
	 *          the selector of the request
	 */
	public CheckpointRequest(CheckpointRequestType type, String selector) {
		super(selector);
		this.type = type;
	}

	/**
	 * Constructs a new request with the specified type, selector and value.
	 * 
	 * @param type
	 *          the type of the checkpoint request
	 * @param selector
	 *          the selector of the request
	 * @param value
	 *          the value of the request
	 */
	public CheckpointRequest(CheckpointRequestType type, String selector, String value) {
		super(selector, value);
		this.type = type;
	}

	/**
	 * Constructs a new request with the specified type, selector and timeout
	 * 
	 * @param type
	 *          the type of the checkpoint request
	 * @param selector
	 *          the selector of the request
	 * @param timeout
	 *          timeout of the request
	 */
	public CheckpointRequest(CheckpointRequestType type, String selector, long timeout) {
		super(selector, timeout);
		this.type = type;
	}

	/**
	 * Constructs a new request with the specified type, selector, value and
	 * timeout.
	 * 
	 * @param type
	 *          the type of the checkpoint request
	 * @param selector
	 *          the selector of the request
	 * @param value
	 *          the value of the request
	 * @param timeout
	 *          timeout of the request
	 */
	public CheckpointRequest(CheckpointRequestType type, String selector, String value, long timeout) {
		super(selector, value, timeout);
		this.type = type;
	}

	/**
	 * Sets the task ID and task context ID of this request from the current
	 * system environment properties.
	 */
	public void fillInTaskAndContextId() {
		this.taskId = System.getenv(TaskPropertyNames.TASK_ID);
		this.taskContextId = System.getenv(TaskPropertyNames.CONTEXT_ID);
	}

	/**
	 * Deserializes a JSON string into an instance of .
	 * 
	 * @param json
	 *          JSON representation of the request
	 * @return deserialized request
	 * @throws JsonException
	 *           when the string cannot be deserialized
	 */
	public static CheckpointRequest fromJson(String json) throws JsonException {
		return JSONUtils.newInstance().deserialize(json, CheckpointRequest.class);
	}

	/**
	 * Gets the type of the request.
	 * 
	 * @return the type of the request
	 */
	public CheckpointRequestType getType() {
		return type;
	}

	/**
	 * Sets the type of the request.
	 * 
	 * @param type
	 *          the type of the request
	 */
	public void setType(CheckpointRequestType type) {
		this.type = type;
	}

	/**
	 * Gets the task ID of the request.
	 * 
	 * @return the task ID of the request
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * Sets the task ID of the request.
	 * 
	 * @param taskId
	 *          the task ID of the request
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * Gets the task context ID of the request.
	 * 
	 * @return the task context ID of the request
	 */
	public String getTaskContextId() {
		return taskContextId;
	}

	/**
	 * Sets the task context ID of the request.
	 * 
	 * @param taskContextId
	 *          the task context ID of the request
	 */
	public void setTaskContextId(String taskContextId) {
		this.taskContextId = taskContextId;
	}

}
