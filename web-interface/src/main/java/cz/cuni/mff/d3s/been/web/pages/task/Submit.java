package cz.cuni.mff.d3s.been.web.pages.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;

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

	@Property
	Descriptor descriptor;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	public Collection<BpkIdentifier> getBpks() throws BeenApiException {
		return getApi().getBpks();
	}

	Object onDeleteNamedTaskDescriptor(String name, String groupId, String bpkId, String version) throws BeenApiException {
		BpkIdentifier bpkIdentifier = new ObjectFactory().createBpkIdentifier().withBpkId(bpkId).withGroupId(groupId).withVersion(
				version);

		getApi().deleteNamedTaskDescriptor(bpkIdentifier, name);

		return Submit.class;
	}

			Object
			onDeleteNamedTaskContextDescriptor(String name, String groupId, String bpkId, String version) throws BeenApiException {
		BpkIdentifier bpkIdentifier = new ObjectFactory().createBpkIdentifier().withBpkId(bpkId).withGroupId(groupId).withVersion(
				version);

		getApi().deleteNamedTaskContextDescriptor(bpkIdentifier, name);

		return Submit.class;
	}

	public Collection<Descriptor> descriptorsForBpk(BpkIdentifier bpk) throws BeenApiException {
		Collection<Descriptor> result = new ArrayList<>();

		result.addAll(createTaskDescriptorWrappers(bpk));
		result.addAll(createTaskContextDescriptorWrappers(bpk));

		return result;
	}

	private Collection<Descriptor> createTaskDescriptorWrappers(BpkIdentifier bpk) throws BeenApiException {
		Collection<Descriptor> descriptors = new ArrayList();

		descriptors.addAll(createTaskDescriptorWrappers(getApi().getTaskDescriptors(bpk).entrySet(), bpk, false));
		descriptors.addAll(createTaskDescriptorWrappers(getApi().getNamedTaskDescriptorsForBpk(bpk).entrySet(), bpk, true));

		return descriptors;
	}

	private Collection<Descriptor> createTaskContextDescriptorWrappers(BpkIdentifier bpk) throws BeenApiException {
		Collection<Descriptor> descriptors = new ArrayList();

		descriptors.addAll(createTaskContextDescriptorWrappers(
				getApi().getTaskContextDescriptors(bpk).entrySet(),
				bpk,
				false));
		descriptors.addAll(createTaskContextDescriptorWrappers(
				getApi().getNamedContextDescriptorsForBpk(bpk).entrySet(),
				bpk,
				true));

		return descriptors;
	}

	private Collection<Descriptor> createTaskDescriptorWrappers(Collection<Map.Entry<String, TaskDescriptor>> entries,
			BpkIdentifier bpk, boolean named) throws BeenApiException {
		Collection<Descriptor> descriptors = new ArrayList();
		for (Map.Entry<String, TaskDescriptor> entry : entries) {
			String descriptorName = entry.getKey();
			TaskDescriptor td = entry.getValue();
			Object[] linkEventContext = new Object[] { bpk.getGroupId(), bpk.getBpkId(), bpk.getVersion(), descriptorName };

			String submitLink = null;
			if (td.getType() == TaskType.TASK) {
				submitLink = createLink(SubmitTaskDescriptor.class, linkEventContext);
			} else if (td.getType() == TaskType.BENCHMARK) {
				submitLink = createLink(SubmitBenchmarkDescriptor.class, linkEventContext);
			}

			descriptors.add(new Descriptor(td, submitLink, descriptorName, named));
		}
		return descriptors;
	}

	private
			Collection<Descriptor>
			createTaskContextDescriptorWrappers(Collection<Map.Entry<String, TaskContextDescriptor>> entries,
					BpkIdentifier bpk, boolean named) throws BeenApiException {
		Collection<Descriptor> descriptors = new ArrayList();
		for (Map.Entry<String, TaskContextDescriptor> entry : entries) {
			String descriptorName = entry.getKey();
			TaskContextDescriptor tcd = entry.getValue();
			Object[] linkEventContext = new Object[] { bpk.getGroupId(), bpk.getBpkId(), bpk.getVersion(), descriptorName };
			String submitLink = createLink(SubmitTaskContextDescriptor.class, linkEventContext);
			descriptors.add(new Descriptor(tcd, submitLink, descriptorName, named));
		}
		return descriptors;
	}

	private String createLink(Class<?> pageClass, Object[] linkEventContext) {
		return pageRenderLinkSource.createPageRenderLinkWithContext(pageClass, linkEventContext).toAbsoluteURI().toString();
	}

	// ******************************
	// Displayable descriptor wrapper
	// ******************************

	public class Descriptor {
		public String name;
		public TaskDescriptor taskDescriptor;
		public TaskContextDescriptor taskContextDescriptor;
		public String submitLink;
		public boolean isNamed;

		public Descriptor(TaskDescriptor taskDescriptor, String submitLink, String name, boolean named) {
			this.taskDescriptor = taskDescriptor;
			this.submitLink = submitLink;
			this.name = name;
			this.isNamed = named;
		}

		public Descriptor(TaskContextDescriptor taskContextDescriptor, String submitLink, String name, boolean named) {
			this.taskContextDescriptor = taskContextDescriptor;
			this.submitLink = submitLink;
			this.name = name;
			this.isNamed = named;
		}
	}

}
