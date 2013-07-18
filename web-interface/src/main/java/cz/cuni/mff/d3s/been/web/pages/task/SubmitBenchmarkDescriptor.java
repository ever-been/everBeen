package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Overview;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.TASK_SUBMIT)
public class SubmitBenchmarkDescriptor extends SubmitTaskDescriptor {

	@Override
	protected void submitTaskDescriptor(TaskDescriptor taskDescriptor) {
		this.api.getApi().submitBenchmark(taskDescriptor);
	}

}
