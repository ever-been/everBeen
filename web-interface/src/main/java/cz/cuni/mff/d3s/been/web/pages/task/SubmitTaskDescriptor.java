package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.ObjectFactory;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskProperty;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Overview;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This component is used to edit task descriptor properties and submit
 * task defined by this task descriptor.
 *
 * @author Kuba Brecka
 * @author Tadeas Palusga
 */
@Page.Navigation(section = Layout.Section.TASK_SUBMIT)
@SuppressWarnings("unused")
public class SubmitTaskDescriptor extends Page {

	/**
	 * Task descriptor loaded in onActivate() method
	 */
	@Property
	@Persist
	TaskDescriptor taskDescriptor;

	/**
	 * Variable is used as property in AjaxFormLoop via argument wrappers used in tmlTemplate
	 */
	@Property
	@SuppressWarnings("unused")
	private String arg;

	@Property
	@SuppressWarnings("unused")
	private int propertyLoopIndex;

	/**
	 * Setup method. Loads task descriptor (corresponding to given parameters)
	 * using been api and sets this descriptor as editable property for this component.
	 *
	 * @param groupId        group id of {@link BpkIdentifier} to which the underlying {@link TaskDescriptor} belows
	 * @param bpkId          bpk id of {@link BpkIdentifier} to which the underlying {@link TaskDescriptor} belows
	 * @param version        version id of {@link BpkIdentifier} to which the underlying {@link TaskDescriptor} belows
	 * @param descriptorName name of concrete {@link TaskDescriptor} for {@link BpkIdentifier} identified by previous parameters
	 */
	@SuppressWarnings("unused")
	void onActivate(String groupId, String bpkId, String version, String descriptorName) {
		// load correct task descriptor
		BpkIdentifier bpkIdentifier = new BpkIdentifier();
		bpkIdentifier.setGroupId(groupId);
		bpkIdentifier.setBpkId(bpkId);
		bpkIdentifier.setVersion(version);
		this.taskDescriptor = this.api.getApi().getTaskDescriptor(bpkIdentifier, descriptorName);

		// initialize fields on loaded task descriptor if not initialized
		if (!taskDescriptor.isSetArguments()) {
			taskDescriptor.setArguments(new ObjectFactory().createArguments());
		}

		if (!taskDescriptor.isSetProperties()) {
			taskDescriptor.setProperties(new ObjectFactory().createTaskProperties());
		}
	}


	/**
	 * Injected form component for task submitting.
	 */
	@Component
	@SuppressWarnings("unused")
	private Form submitTaskForm;

	/**
	 * This handler is called when users click on form SUBMIT button.
	 * Submits task to BEEN cluster using {@link cz.cuni.mff.d3s.been.api.BeenApi}
	 *
	 * @return redirect to {@link Overview} page
	 */
	@SuppressWarnings("unused")
	Object onSuccessFromSubmitTaskForm() {
		this.api.getApi().submitTask(taskDescriptor);
		return Overview.class;
	}

	// -----------------------------
	// HANDLING ARGUMENTS
	// -----------------------------

	/**
	 * This handler is invoked when user click on the ADD (+) button in arguments section.
	 * Returns new default argument (empty string)
	 *
	 * @return empty argument
	 */
	@SuppressWarnings("unused")
	String onAddRowFromArgumentList() {
		return "";
	}

}
