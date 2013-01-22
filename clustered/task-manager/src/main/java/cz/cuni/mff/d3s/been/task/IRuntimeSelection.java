package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;


/**
 * @author Martin Sixta
 */
interface IRuntimeSelection {
	String select(final TaskEntry taskEntry) throws NoRuntimeFoundException;
}
