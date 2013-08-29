package cz.cuni.mff.d3s.been.objectrepository.janitor;

import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.persistence.task.PersistentTaskState;

/**
 * Helper class for a terminal decision on what the outcome of a task was
 *
 * @author darklight
 */
class TotalTaskOutcome implements TotalOutcome<PersistentTaskState> {
	private PersistentTaskState startingState;
	private PersistentTaskState terminalState;

	@Override
	public void addStateEntry(PersistentTaskState stateEntry) {
		switch (stateEntry.getTaskState()) {
			case RUNNING:
				startingState = stateEntry;
				break;
			case ABORTED:
			case FINISHED:
				terminalState = stateEntry;
				break;
			default:
				// something odd
				break;
		}
	}

	@Override
	public boolean isFailed() {
		return (! isZombie()) && TaskState.ABORTED.equals(terminalState.getTaskState());
	}

	@Override
	public boolean isZombie() {
		return startingState != null && terminalState == null;
	}

	@Override
	public String getEventId() {
		return startingState == null ? terminalState.getTaskId() : startingState.getTaskId();
	}
}
