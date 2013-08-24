package cz.cuni.mff.d3s.been.web.components.task;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import cz.cuni.mff.d3s.been.core.task.TaskContextState;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * @author donarus
 */
public class StateIcon {

	@Parameter
	private TaskState taskState;

	@Parameter
	private TaskContextState taskContextState;

	@Inject
	private Block stateRunningBlock;

	@Inject
	private Block stateWaitingBlock;

	@Inject
	private Block stateFinishedBlock;

	@Inject
	private Block stateWarningBlock;

	@Inject
	private Block stateNotAvailableBlock;

	@SetupRender
	void setupRender() throws Exception {
		if (taskState != null && taskContextState != null) {
			throw new Exception("Defined both taskState and taskContextState. Only one of them allowed.");
		}
	}

	public Block getSelected() {
		if (taskState != null) {
			return getCorrectTaskBlock();
		} else if (taskContextState != null) {
			return getCorrectTaskContextBlock();
		} else {
			// both taskState and taskContextState are null
			return stateNotAvailableBlock;
		}
	}

	public Block getCorrectTaskBlock() {
		switch (taskState) {
			case RUNNING:
				return stateRunningBlock;
			case FINISHED:
				return stateFinishedBlock;
			case WAITING:
				return stateWaitingBlock;
			default:
				return stateWarningBlock;
		}
	}

	public Block getCorrectTaskContextBlock() {
		switch (taskContextState) {
			case RUNNING:
				return stateRunningBlock;
			case FINISHED:
				return stateFinishedBlock;
			case WAITING:
				return stateWaitingBlock;
			default:
				return stateWarningBlock;
		}
	}

}
