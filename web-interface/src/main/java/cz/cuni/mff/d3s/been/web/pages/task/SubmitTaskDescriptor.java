package cz.cuni.mff.d3s.been.web.pages.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.core.task.ModeEnum;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.model.ConversationHolder;
import cz.cuni.mff.d3s.been.web.model.KeyValuePair;
import cz.cuni.mff.d3s.been.web.model.TaskDescriptorInitializer;
import cz.cuni.mff.d3s.been.web.pages.Index;
import cz.cuni.mff.d3s.been.web.pages.Overview;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * This component is used to edit task descriptor properties and submit task
 * defined by this task descriptor.
 * 
 * @author Kuba Brecka
 * @author Tadeas Palusga
 */
@Page.Navigation(section = Layout.Section.TASK_SUBMIT)
@SuppressWarnings("unused")
public class SubmitTaskDescriptor extends Page {

	// -----------------------------
	// KEYS USED IN CONVERSATION HOLDER
	// -----------------------------

	private static final String KEY_TASK_DESCRIPTOR = "task_descriptor";

	private static final String KEY_ARGS = "args";

	private static final String KEY_OPTS = "opts";

	// -----------------------------
	// CONVERSATION POLICY
	// -----------------------------

	@SessionState(create = true)
	private ConversationHolder<Map<String, Object>> conversationHolder;

	@Property
	boolean save;

	@Property
	String saveName;

	/**
	 * Task descriptor loaded in onActivate() method
	 */
	@Property
	TaskDescriptor taskDescriptor;

	/**
	 * Argument list transformed from list of string on taskDescriptor to list of
	 * keyValuePairs. We have to assign temporary IDs (keys) to arguments (values)
	 * specified in task descriptor to tie them conclusively from JAVA to
	 * JAVASCRIPT and vice versa. (Arguments in task descriptor has no unique ID)
	 */
	@Property
	List<KeyValuePair> args;

	/**
	 * Java option list transformed from list of string on taskDescriptor to list
	 * of keyValuePairs. We have to assign temporary IDs (keys) to options
	 * (values) specified in task descriptor to tie them conclusively from JAVA to
	 * JAVASCRIPT and vice versa. (Java Options in task descriptor has no unique
	 * ID)
	 */
	@Property
	List<KeyValuePair> opts;

	// -----------------------------
	// TAPESTRY TEMPLATE FIELDS
	// -----------------------------

	/**
	 * Identifier of current conversation
	 */
	private String conversationId;

	/**
	 * Injected form component for task submitting.
	 */
	@Component
	@SuppressWarnings("unused")
	private Form submitTaskForm;

	// -----------------------------
	// ACTIVATION AND PASSIVATION
	// -----------------------------

	/**
	 * Is set to true if page has been already activated
	 */
	private boolean activated;

	/**
	 * Activate page with context of conversation with given identifier.
	 * 
	 * @param conversationId
	 * @return null if page has been already activated or page has been correctly
	 *         activated. Redirect to {@link Index} page otherwise.
	 */
	Object onActivate(String conversationId) {
		// we have to check if page has been already activated, because we don't want
		// to override context of already activated pages
		if (activated) {
			return null;
		}

		if (!conversationHolder.contains(conversationId)) {
			// FIXME .. inform user in proper way?
			return Index.class;
		} else {
			taskDescriptor = (TaskDescriptor) conversationHolder.get(conversationId).get(KEY_TASK_DESCRIPTOR);
			args = (List<KeyValuePair>) conversationHolder.get(conversationId).get(KEY_ARGS);
			opts = (List<KeyValuePair>) conversationHolder.get(conversationId).get(KEY_OPTS);

			this.conversationId = conversationId;

			activated = true;
			return null;
		}
	}

	/**
	 * Setup method. Loads task descriptor (corresponding to given parameters)
	 * using been api and sets this descriptor as editable property for this
	 * component.
	 * 
	 * @param groupId
	 *          group id of {@link BpkIdentifier} to which the underlying
	 *          {@link TaskDescriptor} belows
	 * @param bpkId
	 *          bpk id of {@link BpkIdentifier} to which the underlying
	 *          {@link TaskDescriptor} belows
	 * @param version
	 *          version id of {@link BpkIdentifier} to which the underlying
	 *          {@link TaskDescriptor} belows
	 * @param descriptorName
	 *          name of concrete {@link TaskDescriptor} for {@link BpkIdentifier}
	 *          identified by previous parameters
	 * @return null (see tapestry page documentation about return values from
	 *         onActivate and onPassivate methods)
	 */
	@SuppressWarnings("unused")
	Object onActivate(String groupId, String bpkId, String version, String descriptorName) throws BeenApiException {
		// load correct task descriptor
		BpkIdentifier bpkIdentifier = new BpkIdentifier();
		bpkIdentifier.setGroupId(groupId);
		bpkIdentifier.setBpkId(bpkId);
		bpkIdentifier.setVersion(version);
		this.taskDescriptor = this.api.getApi().getTaskDescriptor(bpkIdentifier, descriptorName);

		args = new ArrayList<>();
		opts = new ArrayList<>();

		TaskDescriptorInitializer.initialize(taskDescriptor, args, opts);

		HashMap<String, Object> conversationArgs = new HashMap<String, Object>();
		conversationArgs.put(KEY_OPTS, opts);
		conversationArgs.put(KEY_ARGS, args);
		conversationArgs.put(KEY_TASK_DESCRIPTOR, taskDescriptor);
		this.conversationId = conversationHolder.set(conversationArgs);

		activated = true;
		return null;
	}

	/**
	 * @return conversationId as passivate parameter (used on next onActivate
	 *         parameter). See tapestry documentation to get more information
	 *         about expected return values from onActivate and onPassivate
	 *         methods
	 */
	Object onPassivate() {
		return conversationId;
	}

	// -----------------------------
	// FORM HANDLING
	// -----------------------------

	/**
	 * To be overloaded from SubmitBenchmarkDescriptor.
	 * 
	 * @param taskDescriptor
	 *          task descriptor to submit
	 */
	protected void submitTaskDescriptor(TaskDescriptor taskDescriptor) throws BeenApiException {
		this.api.getApi().submitTask(taskDescriptor);
	}

	/**
	 * This handler is called when users click on form SUBMIT button. Submits task
	 * to BEEN cluster using {@link cz.cuni.mff.d3s.been.api.BeenApi}
	 * 
	 * @return redirect to {@link Overview} page
	 */
	@SuppressWarnings("unused")
	Object onSubmitFromSubmitTaskForm() throws BeenApiException {

		args.remove(null);
		taskDescriptor.getArguments().getArgument().clear();
		taskDescriptor.getJava().getJavaOptions().getJavaOption().clear();
		for (KeyValuePair arg : args) {
			if (arg.value != null) {
				taskDescriptor.getArguments().getArgument().add(arg.value);
			}
		}
		for (KeyValuePair opt : opts) {
			if (opt.value != null) {
				taskDescriptor.getJava().getJavaOptions().getJavaOption().add(opt.value);
			}
		}

        if (taskDescriptor.getDebug().getMode() == ModeEnum.NONE) {
            taskDescriptor.getDebug().setSuspend(false);
        }

		if (save) {
			BpkIdentifier bpkIdentifier = new BpkIdentifier();
			bpkIdentifier.setGroupId(taskDescriptor.getGroupId());
			bpkIdentifier.setBpkId(taskDescriptor.getBpkId());
			bpkIdentifier.setVersion(taskDescriptor.getVersion());
			this.api.getApi().saveNamedTaskDescriptor(taskDescriptor, this.saveName, bpkIdentifier);
		}

		// try to execute the filter to see if it is syntactically correct
		String xpath = taskDescriptor.getHostRuntimes().getXpath();
		if (xpath != null && !xpath.isEmpty()) {
			this.api.getApi().getRuntimes(taskDescriptor.getHostRuntimes().getXpath());
		}

		submitTaskDescriptor(taskDescriptor);

		return Overview.class;
	}

}
