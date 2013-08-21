package cz.cuni.mff.d3s.been.web.pages.task;

import org.apache.tapestry5.annotations.Property;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.core.task.Debug;
import cz.cuni.mff.d3s.been.core.task.ModeEnum;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskProperty;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.model.TaskSupport;
import cz.cuni.mff.d3s.been.web.pages.DetailPage;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * User: donarus Date: 4/22/13 Time: 1:38 PM
 */
@Page.Navigation(section = Layout.Section.TASK_DETAIL)
public class Detail extends DetailPage {

	@Property
	private String arg;

	@Property
	private TaskProperty property;

	private TaskEntry task;

	public TaskEntry getTask() throws BeenApiException {
		if (this.task == null) {
			this.task = getApi().getTask(itemId);
		}
		return task;
	}
	@Override
	public void setupRender() {
		super.setupRender();
	}

	public String taskDebugToString(Debug debug) {
		if (debug == null)
			return "NONE";
		if (debug.getMode() == ModeEnum.NONE)
			return "NONE";
		else if (debug.getMode() == ModeEnum.LISTEN)
			return "LISTEN, port: " + debug.getPort();
		else if (debug.getMode() == ModeEnum.CONNECT)
			return "CONNECT, host: " + debug.getHost() + ", port: " + debug.getPort();

		throw new IllegalStateException("Invalid enum value.");
	}

	public boolean isTaskInFinalState(String taskId) throws BeenApiException {
		return new TaskSupport(getApi()).isTaskInFinalState(taskId);
	}

	Object onKillTask(String taskId) throws BeenApiException, InterruptedException {
		new TaskSupport(getApi()).killTask(taskId);
		return this;
	}

	Object onRemoveTask(String taskId) throws BeenApiException, InterruptedException {
		new TaskSupport(getApi()).removeKilledTask(taskId);
		return Tree.class;
	}

}
