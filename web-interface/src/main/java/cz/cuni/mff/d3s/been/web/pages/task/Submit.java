package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.DetailPage;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * User: donarus Date: 4/29/13 Time: 2:14 PM
 */
@Page.Navigation(section = Layout.Section.TASK_SUBMIT)
public class Submit extends Page {

	@Property
	BpkIdentifier bpk;

	public Collection<BpkIdentifier> getBpks() {
		return this.api.getApi().getBpks();
	}

	public class Descriptor {
		public String name;
		public boolean isTaskDescriptor;
		public TaskDescriptor taskDescriptor;
		public TaskContextDescriptor taskContextDescriptor;
		public String submitLink;
	}

	@Property
	Descriptor descriptor;

	public Collection<Descriptor> descriptorsInBpk(BpkIdentifier bpk) {
		Collection<Descriptor> result = new ArrayList<Descriptor>();
		for (Map.Entry<String, TaskDescriptor> entry : this.api.getApi().getTaskDescriptors(bpk).entrySet()) {
			String descriptorName = entry.getKey();
			TaskDescriptor td = entry.getValue();
			Descriptor d = new Descriptor();
			d.name = descriptorName;
			d.isTaskDescriptor = true;
			d.taskDescriptor = td;
			d.submitLink = "/task/submittaskdescriptor/" + bpk.getGroupId() + "/" + bpk.getBpkId() + "/" + bpk.getVersion() + "/" + descriptorName;
			result.add(d);
		}

		for (Map.Entry<String, TaskContextDescriptor> entry : this.api.getApi().getTaskContextDescriptors(bpk).entrySet()) {
			String descriptorName = entry.getKey();
			TaskContextDescriptor tcd = entry.getValue();
			Descriptor d = new Descriptor();
			d.name = descriptorName;
			d.isTaskDescriptor = false;
			d.taskContextDescriptor = tcd;
			d.submitLink = "/task/submittaskcontextdescriptor/" + bpk.getGroupId() + "/" + bpk.getBpkId() + "/" + bpk.getVersion() + "/" + descriptorName;
			result.add(d);
		}

		return result;
	}

}
