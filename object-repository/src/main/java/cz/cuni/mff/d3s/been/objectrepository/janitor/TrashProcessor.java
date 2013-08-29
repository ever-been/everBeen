package cz.cuni.mff.d3s.been.objectrepository.janitor;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import cz.cuni.mff.d3s.been.persistence.task.PersistentContextState;
import cz.cuni.mff.d3s.been.persistence.task.PersistentTaskState;

/**
 * Persistence trash cleanup planner
 * 
 * @author darklight
 */
class TrashProcessor {
	private final Map<String, TotalOutcome<PersistentTaskState>> oldTasks = new TreeMap<String, TotalOutcome<PersistentTaskState>>();
	private final Map<String, TotalOutcome<PersistentContextState>> oldContexts = new TreeMap<String, TotalOutcome<PersistentContextState>>();

	/**
	 * Add newly retrieved task outcomes from the persistence layer
	 * 
	 * @param states
	 *          States to add
	 */
	public void addTaskStates(Collection<PersistentTaskState> states) {
		for (PersistentTaskState state : states) {
			TotalOutcome<PersistentTaskState> outcome = oldTasks.get(state.getTaskId());
			if (outcome == null) {
				outcome = new TotalTaskOutcome();
				oldTasks.put(state.getTaskId(), outcome);
			}
			outcome.addStateEntry(state);
		}
	}

	/**
	 * Add newly retrieved context outcomes from the persistence layer
	 * 
	 * @param states
	 *          States to add
	 */
	public void addContextStates(Collection<PersistentContextState> states) {
		for (PersistentContextState state : states) {
			TotalOutcome<PersistentContextState> outcome = oldContexts.get(state.getContextId());
			if (outcome == null) {
				outcome = new TotalContextOutcome();
				oldContexts.put(state.getContextId(), outcome);
			}
			outcome.addStateEntry(state);
		}
	}

	/**
	 * Get a task outcome for processing
	 * 
	 * @return A task outcome to be processed
	 */
	public synchronized TotalOutcome<PersistentTaskState> getNextTask() {
		if (oldTasks.isEmpty()) {
			return null;
		}
		return oldTasks.remove(oldTasks.keySet().iterator().next());
	}

	/**
	 * Get a context outcome for processing
	 * 
	 * @return A context outcome to be processed
	 */
	public synchronized TotalOutcome<PersistentContextState> getNextContext() {
		if (oldContexts.isEmpty()) {
			return null;
		}
		return oldContexts.remove(oldContexts.keySet().iterator().next());
	}
}
