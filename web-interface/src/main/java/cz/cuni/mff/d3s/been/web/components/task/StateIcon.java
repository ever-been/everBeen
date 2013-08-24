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

	@Parameter(required = false)
	private boolean hideStateDescription = false;

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
	private Block stateScheduledBlock;

	@Inject
	private Block stateAbortedBlock;

	@Inject
	private Block stateOtherBlock;

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
			return stateOtherBlock;
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
			case SCHEDULED:
				return stateScheduledBlock;
			case ABORTED:
				return stateAbortedBlock;
			default:
				return stateOtherBlock;
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
			case FAILED:
				return stateAbortedBlock;
			default:
				return stateOtherBlock;
		}
	}

	public String getStateName() {
		if (hideStateDescription) {
			return "";
		} else {
			if (taskState == null && taskContextState == null) {
				return "&nbsp;N/A&nbsp;&nbsp;";
			} else if (taskState != null) {
				return "&nbsp;" + taskState.name() + "&nbsp;&nbsp;";
			} else
				return "&nbsp;" + taskContextState.name() + "&nbsp;&nbsp;";
		}
	}
}
