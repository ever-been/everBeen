package cz.cuni.mff.d3s.been.core.task;

/**
 * @author Martin Sixta
 */
public final class StateChangeEntry {

	public final String reason;
	public final TaskState state;

	protected StateChangeEntry(TaskState state, String reason) {
		this.state = state;
		this.reason = reason;
	}



}
