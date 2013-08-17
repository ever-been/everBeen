package cz.cuni.mff.d3s.been.web.pages.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.URLEncoder;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.bpk.ObjectFactory;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskType;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * User: donarus Date: 4/29/13 Time: 2:14 PM
 */
@Page.Navigation(section = Layout.Section.TASK_SUBMIT)
public class Submit extends Page {

	@Property
	BpkIdentifier bpk;

	public Collection<BpkIdentifier> getBpks() throws BeenApiException {
		return this.api.getApi().getBpks();
	}

	public class Descriptor {
		public boolean named = false;
		public String name;
		public boolean isTaskDescriptor;
		public TaskDescriptor taskDescriptor;
		public TaskContextDescriptor taskContextDescriptor;
		public String submitLink;
	}

	@Property
	Descriptor descriptor;

	@Inject
	URLEncoder urlEncoder;

	Object onActionFromDeleteNamedDescriptor(String name, boolean isTaskDescriptor, String groupId, String bpkId,
			String version) throws BeenApiException {
		BpkIdentifier bpkIdentifier = new ObjectFactory().createBpkIdentifier();
		bpkIdentifier.setBpkId(bpkId);
		bpkIdentifier.setGroupId(groupId);
		bpkIdentifier.setVersion(version);

		if (isTaskDescriptor) {
			api.getApi().deleteNamedTaskDescriptor(bpkIdentifier, name);
		} else {
			api.getApi().deleteNamedTaskContextDescriptor(bpkIdentifier, name);
		}

		return Submit.class;
	}

	public Collection<Descriptor> descriptorsForBpk(BpkIdentifier bpk) throws BeenApiException {
		Collection<Descriptor> result = new ArrayList<Descriptor>();
		for (Map.Entry<String, TaskDescriptor> entry : this.api.getApi().getTaskDescriptors(bpk).entrySet()) {
			String descriptorName = entry.getKey();
			TaskDescriptor td = entry.getValue();
			String link = linkFromBpkIdentifier(bpk, descriptorName);
			addTdToResults(result, descriptorName, td, link, false);
		}

		for (Map.Entry<String, TaskDescriptor> entry : this.api.getApi().getNamedTaskDescriptorsForBpk(bpk).entrySet()) {
			String descriptorName = entry.getKey();
			TaskDescriptor td = entry.getValue();
			String link = linkFromBpkIdentifier(bpk, descriptorName);
			addTdToResults(result, descriptorName, td, link, true);
		}

		for (Map.Entry<String, TaskContextDescriptor> entry : this.api.getApi().getTaskContextDescriptors(bpk).entrySet()) {
			String descriptorName = entry.getKey();
			TaskContextDescriptor tcd = entry.getValue();
			String link = linkFromBpkIdentifier(bpk, descriptorName);
			addTcdToResults(result, descriptorName, tcd, link, false);
		}

		for (Map.Entry<String, TaskContextDescriptor> entry : this.api.getApi().getNamedContextDescriptorsForBpk(bpk).entrySet()) {
			String descriptorName = entry.getKey();
			TaskContextDescriptor tcd = entry.getValue();
			String link = linkFromBpkIdentifier(bpk, descriptorName);
			addTcdToResults(result, descriptorName, tcd, link, true);
		}

		return result;
	}

	private void addTcdToResults(Collection<Descriptor> result, String descriptorName, TaskContextDescriptor tcd,
			String link, boolean named) {
		Descriptor d = new Descriptor();
		d.name = descriptorName;
		d.isTaskDescriptor = false;
		d.taskContextDescriptor = tcd;
		d.named = named;

		d.submitLink = "/task/submittaskcontextdescriptor/" + link;
		result.add(d);
	}

	private void addTdToResults(Collection<Descriptor> result, String descriptorName, TaskDescriptor td, String s,
			boolean named) {
		Descriptor d = new Descriptor();
		d.name = descriptorName;
		d.isTaskDescriptor = true;
		d.taskDescriptor = td;
		d.named = named;

		if (td.getType() == TaskType.TASK) {
			d.submitLink = "/task/submittaskdescriptor/" + s;
		} else if (td.getType() == TaskType.BENCHMARK) {
			d.submitLink = "/task/submitbenchmarkdescriptor/" + s;
		}
		result.add(d);
	}

	private String linkFromBpkIdentifier(BpkIdentifier bpk, String descriptorName) {
		return urlEncoder.encode(bpk.getGroupId()) + "/" + urlEncoder.encode(bpk.getBpkId()) + "/" + urlEncoder.encode(bpk.getVersion()) + "/" + urlEncoder.encode(descriptorName);
	}

}
