package cz.cuni.mff.d3s.been.core.task;


import cz.cuni.mff.d3s.been.core.td.TaskDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import cz.cuni.mff.d3s.been.core.entry.IEntry;

/**
 * BEEN entry describing a task.
 *
 * TODO:
 * JAXB backing ... if we figure out how to reference TD
 * Consider moving all logic to a utility class (__^__)
 *
 * @author Martin Sixta
 */
public final class TaskEntry implements IEntry {

	private TaskState state;
	private final String id;
	private String ownerId;
	private String runtimeId;
	private final TaskDescriptor descriptor;


	private final LinkedList<StateChangeEntry> stateChangeLog;



	protected TaskEntry(String id, TaskDescriptor descriptor) {
		this.id = id;
		this.descriptor = descriptor;
		this.state = TaskState.CREATED;
		stateChangeLog = new LinkedList<>();
	}


	public TaskState getState() {
		return state;
	}

	public void setState(TaskState newState, String message) throws IllegalStateException {
		if (!state.canChangeTo(newState)) {
			throw new IllegalStateException("Cannot change state from " + state + " to " + state);
		}

		stateChangeLog.add(new StateChangeEntry(newState, message));

		this.state = newState;
	}

	public Collection<StateChangeEntry> getStateChangeLog() {
		// return defense copy
		// stored objects are immutable, so we need to return just different collection
		return new ArrayList<>(stateChangeLog);
	}

	public String getStateChangeReason() {
		return stateChangeLog.getLast().reason;
	}


	public String getId() {
		return id;
	}


	public TaskDescriptor getDescriptor() {
		return descriptor;
	}



	@Override
	public String toString() {
		return id;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getRuntimeId() {
		return runtimeId;
	}

	public void setRuntimeId(String runtimeId) {
		this.runtimeId = runtimeId;
	}
}