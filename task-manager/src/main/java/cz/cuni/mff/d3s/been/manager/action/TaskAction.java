package cz.cuni.mff.d3s.been.manager.action;

/**
 * Action performed by a <em>task</em> on request from <em>Task Manager</em>
 *
 * @author Martin Sixta
 */
public interface TaskAction {

	/**
	 * Execute the action
	 *
	 * @throws TaskActionException When action execution encounters a problem
	 */
	public void execute() throws TaskActionException;

}
