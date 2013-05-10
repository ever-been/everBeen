package cz.cuni.mff.d3s.been.task.action;

/**
 * @author Martin Sixta
 */
public class Actions {

	/** Creates action which does nothing */
	TaskAction createNullAction() {
		return new NullAction();
	}
}
