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
public class SubmitTaskDescriptor extends Page {

	@Property
	BpkIdentifier bpkIdentifier;

	@Property
	@Persist
	TaskDescriptor taskDescriptor;

	void onActivate(String groupId, String bpkId, String version, String descriptorName) {
		bpkIdentifier = new BpkIdentifier();
		bpkIdentifier.setGroupId(groupId);
		bpkIdentifier.setBpkId(bpkId);
		bpkIdentifier.setVersion(version);

		this.taskDescriptor = this.api.getApi().getTaskDescriptor(bpkIdentifier, descriptorName);
	}

	Object onPassivate() {
		return new Object[]{bpkIdentifier.getGroupId(), bpkIdentifier.getBpkId(), bpkIdentifier
				.getVersion(), taskDescriptor.getName()};
	}

	@Component
	private Form form;

	Object onSuccess() {
		this.api.getApi().submitTask(taskDescriptor);
		return Overview.class;
	}


}
