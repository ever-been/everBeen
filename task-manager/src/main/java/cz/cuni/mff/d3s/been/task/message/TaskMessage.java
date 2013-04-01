package cz.cuni.mff.d3s.been.task.message;

import java.io.Serializable;

import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * @author Martin Sixta
 */

public interface TaskMessage extends Serializable {
	public TaskEntry getEntry();
}
