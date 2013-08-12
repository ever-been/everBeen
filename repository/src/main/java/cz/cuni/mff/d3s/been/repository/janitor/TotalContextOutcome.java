package cz.cuni.mff.d3s.been.repository.janitor;

import cz.cuni.mff.d3s.been.core.task.TaskContextState;
import cz.cuni.mff.d3s.been.persistence.task.PersistentContextState;

/**
 * Helper class for a terminal decision on what the outcome of a task context was
 *
 * @author darklight
 */
public class TotalContextOutcome implements TotalOutcome<PersistentContextState> {

	private PersistentContextState startingState;
	private PersistentContextState terminalState;

	@Override
	public boolean isZombie() {
		return startingState != null && terminalState == null;
	}

	@Override
	public boolean isFailed() {
		return !isZombie() && TaskContextState.FAILED.equals(terminalState.getContextState());
	}

	@Override
	public void addStateEntry(PersistentContextState stateEntry) {
		switch (stateEntry.getContextState()) {
			case RUNNING:
				startingState = stateEntry;
				break;
			case FINISHED:
			case FAILED:
				terminalState = stateEntry;
				break;
			default:
				// something odd
				break;
		}
	}

	@Override
	public String getEventId() {
		return startingState == null ? terminalState.getContextId() : startingState.getContextId();
	}
}
