package cz.cuni.mff.d3s.been.task.action;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.task.NoRuntimeFoundException;

/**
 * @author Martin Sixta
 */
interface IRuntimeSelection {
	String select(final TaskEntry taskEntry) throws NoRuntimeFoundException;
}
