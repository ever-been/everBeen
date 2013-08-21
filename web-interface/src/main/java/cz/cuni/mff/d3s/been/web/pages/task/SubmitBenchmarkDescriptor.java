package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.TASK_SUBMIT)
public class SubmitBenchmarkDescriptor extends SubmitTaskDescriptor {

	@Override
	protected void submitTaskDescriptor(TaskDescriptor taskDescriptor) throws BeenApiException {
		getApi().submitBenchmark(taskDescriptor);
	}

}
