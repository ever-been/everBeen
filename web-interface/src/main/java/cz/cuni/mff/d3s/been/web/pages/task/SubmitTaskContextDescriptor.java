package cz.cuni.mff.d3s.been.web.pages.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.got5.tapestry5.jquery.components.Dialog;

import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.*;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.model.ConversationHolder;
import cz.cuni.mff.d3s.been.web.model.KeyValuePair;
import cz.cuni.mff.d3s.been.web.model.TaskDescriptorInitializer;
import cz.cuni.mff.d3s.been.web.pages.Index;
import cz.cuni.mff.d3s.been.web.pages.Overview;
import cz.cuni.mff.d3s.been.web.pages.Page;

/**
 * @author Kuba Brecka
 */
@Page.Navigation(section = Layout.Section.TASK_SUBMIT)
public class SubmitTaskContextDescriptor extends Page {

	// -----------------------------
	// KEYS USED IN CONVERSATION HOLDER
	// -----------------------------

	private static final String KEY_TASK_CONTEXT_DESCRIPTOR = "task_context_descriptor";

	private static final String KEY_BPK_IDENTIFIER = "bpk_identifier";

	private static final String KEY_TASK_CONTEXT_TEMPLATE_TASKS_DESCRIPTORS = "task_context_template_tasks_descriptors";

	private static final String KEY_TASK_CONTEXT_TEMPLATE_TASKS_ARGS = "task_context_template_tasks_args";

	private static final String KEY_TASK_CONTEXT_TEMPLATE_TASKS_JAVA_OPTS = "task_context_template_tasks_java_opts";

	private static final String KEY_TASK_CONTEXT_TASKS_DESCRIPTORS = "task_context_tasks_descriptors";

	private static final String KEY_TASK_CONTEXT_TASKS_ARGS = "task_context_tasks_args";

	private static final String KEY_TASK_CONTEXT_TASKS_JAVA_OPTS = "task_context_tasks_java_opts";

	private static final String KEY_LOOP_INDEX = "loop_index";

	// -----------------------------
	// CONVERSATION POLICY
	// -----------------------------

	@SessionState(create = true)
	private ConversationHolder<Map<String, Object>> conversationHolder;

	/**
	 * Task context descriptor loaded in onActivate() method
	 */
	@Property
	TaskContextDescriptor taskContextDescriptor;

	// -----------------------------
	// TAPESTRY TEMPLATE FIELDS
	// -----------------------------

	@Property
	boolean save;

	@Property
	String saveName;

	@Property
	BpkIdentifier bpkIdentifier;

	/**
	 * Identifier of current conversation
	 */
	private String conversationId;

	/**
	 * Injected form component for task submitting.
	 */
	@Component
	@SuppressWarnings("unused")
	private Form submitTaskContextForm;

	/**
	 * loop index used in loops in template
	 */
	@Property
	@SuppressWarnings("unused")
	private Integer loopIndex;

	/**
	 * List of lists of task arguments (represented by KeyValuePairs) for task
	 * descriptor templates specified in task context descriptor
	 */
	@Property
	@SuppressWarnings("unused")
	private List<List<KeyValuePair>> taskTemplatesArgs;

	/**
	 * List of lists of task java options (represented by KeyValuePairs) for task
	 * descriptor templates specified in task context descriptor
	 */
	@Property
	@SuppressWarnings("unused")
	private List<List<KeyValuePair>> taskTemplatesJavaOpts;

	/**
	 * List of task descriptor templates specified in task context descriptor.
	 */
	@Property
	@SuppressWarnings("unused")
	private List<TaskDescriptor> taskTemplatesDescriptors;

	/**
	 * List of lists of task arguments (represented by KeyValuePairs) for task
	 * descriptors specified in task context descriptor
	 */
	@Property
	@SuppressWarnings("unused")
	private List<List<KeyValuePair>> tasksArgs;

	/**
	 * List of lists of task java options (represented by KeyValuePairs) for task
	 * descriptors specified in task context descriptor
	 */
	@Property
	@SuppressWarnings("unused")
	private List<List<KeyValuePair>> tasksJavaOpts;

	/**
	 * List of task descriptors specified in task context descriptor.
	 */
	@Property
	@SuppressWarnings("unused")
	private List<TaskDescriptor> tasksDescriptors;

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
	 *         activated. Redirect to {@link cz.cuni.mff.d3s.been.web.pages.Index}
	 *         page otherwise.
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
			taskContextDescriptor = (TaskContextDescriptor) conversationHolder.get(conversationId).get(
					KEY_TASK_CONTEXT_DESCRIPTOR);
			bpkIdentifier = (BpkIdentifier) conversationHolder.get(conversationId).get(KEY_BPK_IDENTIFIER);
			taskTemplatesArgs = (List<List<KeyValuePair>>) conversationHolder.get(conversationId).get(
					KEY_TASK_CONTEXT_TEMPLATE_TASKS_ARGS);
			taskTemplatesJavaOpts = (List<List<KeyValuePair>>) conversationHolder.get(conversationId).get(
					KEY_TASK_CONTEXT_TEMPLATE_TASKS_JAVA_OPTS);
			taskTemplatesDescriptors = (List<TaskDescriptor>) conversationHolder.get(conversationId).get(
					KEY_TASK_CONTEXT_TEMPLATE_TASKS_DESCRIPTORS);
			tasksArgs = (List<List<KeyValuePair>>) conversationHolder.get(conversationId).get(KEY_TASK_CONTEXT_TASKS_ARGS);
			tasksJavaOpts = (List<List<KeyValuePair>>) conversationHolder.get(conversationId).get(
					KEY_TASK_CONTEXT_TASKS_JAVA_OPTS);
			tasksDescriptors = (List<TaskDescriptor>) conversationHolder.get(conversationId).get(
					KEY_TASK_CONTEXT_TASKS_DESCRIPTORS);
			loopIndex = (Integer) conversationHolder.get(conversationId).get(KEY_LOOP_INDEX);

			/*
			if (!taskContextDescriptor.isSetTask()) {
			     // FIXME .... task descriptor without tasks is should be avoided...
			}
			*/

			this.conversationId = conversationId;

			activated = true;
			return null;
		}
	}

	/**
	 * Setup method. Loads task context descriptor (corresponding to given
	 * parameters) using been api and sets this descriptor as editable property
	 * for this component.
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
		// load correct task context descriptor
		bpkIdentifier = new BpkIdentifier().withGroupId(groupId).withBpkId(bpkId).withVersion(version);
		this.taskContextDescriptor = getApi().getTaskContextDescriptor(bpkIdentifier, descriptorName);

		if (this.taskContextDescriptor == null) {
			this.taskContextDescriptor = getApi().getNamedContextDescriptorsForBpk(bpkIdentifier).get(descriptorName);
		}

		HashMap<String, Object> conversationArgs = new HashMap<String, Object>();
		conversationArgs.put(KEY_TASK_CONTEXT_DESCRIPTOR, taskContextDescriptor);
		conversationArgs.put(KEY_BPK_IDENTIFIER, bpkIdentifier);
		this.conversationId = conversationHolder.set(conversationArgs);

		if (!taskContextDescriptor.isSetProperties()) {
			taskContextDescriptor.setProperties(new ObjectFactory().createProperties());
		}

		taskTemplatesArgs = new ArrayList<>();
		taskTemplatesJavaOpts = new ArrayList<>();
		taskTemplatesDescriptors = new ArrayList<>();

		tasksArgs = new ArrayList<>();
		tasksJavaOpts = new ArrayList<>();
		tasksDescriptors = new ArrayList<>();

		if (!taskContextDescriptor.isSetTemplates()) {
			taskContextDescriptor.setTemplates(new ObjectFactory().createTemplates());
		}

		for (Template template : taskContextDescriptor.getTemplates().getTemplate()) {
			List<KeyValuePair> args = new ArrayList<>();
			List<KeyValuePair> javaOpts = new ArrayList<>();

			TaskDescriptorInitializer.initialize(template.getTaskDescriptor(), args, javaOpts);

			taskTemplatesArgs.add(args);
			taskTemplatesJavaOpts.add(javaOpts);
			taskTemplatesDescriptors.add(template.getTaskDescriptor());

		}

		for (Task task : taskContextDescriptor.getTask()) {
			if (task.getDescriptor().isSetTaskDescriptor()) {
				List<KeyValuePair> args = new ArrayList<>();
				List<KeyValuePair> javaOpts = new ArrayList<>();

				TaskDescriptorInitializer.initialize(task.getDescriptor().getTaskDescriptor(), args, javaOpts);
				tasksArgs.add(args);
				tasksJavaOpts.add(javaOpts);
				tasksDescriptors.add(task.getDescriptor().getTaskDescriptor());
			} else {
				taskTemplatesArgs.add(null);
				taskTemplatesJavaOpts.add(null);
				taskTemplatesDescriptors.add(null);
			}
		}

		conversationArgs.put(KEY_TASK_CONTEXT_TEMPLATE_TASKS_DESCRIPTORS, taskTemplatesDescriptors);
		conversationArgs.put(KEY_TASK_CONTEXT_TEMPLATE_TASKS_ARGS, taskTemplatesArgs);
		conversationArgs.put(KEY_TASK_CONTEXT_TEMPLATE_TASKS_JAVA_OPTS, taskTemplatesJavaOpts);

		conversationArgs.put(KEY_TASK_CONTEXT_TASKS_DESCRIPTORS, tasksDescriptors);
		conversationArgs.put(KEY_TASK_CONTEXT_TASKS_ARGS, tasksArgs);
		conversationArgs.put(KEY_TASK_CONTEXT_TASKS_JAVA_OPTS, tasksJavaOpts);

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
	 * This handler is called when users click on form SUBMIT button. Submits task
	 * to BEEN cluster using {@link cz.cuni.mff.d3s.been.api.BeenApi}
	 * 
	 * @return redirect to {@link Overview} page
	 */
	@SuppressWarnings("unused")
	Object onSubmitFromSubmitTaskContextForm() throws BeenApiException {
		if (save) {
			getApi().saveNamedContextDescriptor(taskContextDescriptor, saveName, bpkIdentifier);
		}

		getApi().submitTaskContext(taskContextDescriptor);
		return Overview.class;
	}

	@Property
	private TaskDescriptor taskDescriptor;

	@Property
	private List<KeyValuePair> args;

	@Property
	private List<KeyValuePair> javaOpts;

	@Inject
	private Block taskTemplateDialogBlock;

	@Inject
	private Block taskDescriptorDialogBlock;

	@Component
	private Dialog dialog;

	Object onActionFromTaskTemplateDialogLink(Integer loopIndex) {
		displayModalDialog(loopIndex);
		return taskTemplateDialogBlock;
	}

	Object onActionFromTaskDescriptorDialogLink(Integer loopIndex) {
		displayModalDialog(loopIndex);
		return taskDescriptorDialogBlock;
	}

	private void displayModalDialog(Integer loopIndex) {
		this.loopIndex = loopIndex;
		conversationHolder.get(conversationId).put(KEY_LOOP_INDEX, loopIndex);

		ajaxResponseRenderer.addCallback(new JavaScriptCallback() {
			@Override
			public void run(JavaScriptSupport javascriptSupport) {
				javascriptSupport.addScript(
						"$('#%s').dialog('option', 'width', ($(window).width() / 100) * 95);",
						dialog.getClientId());
			}
		});
	}

	void onSubmitFromTaskTemplateForm(int loopIndex) {
		TaskDescriptor taskDescriptor = taskTemplatesDescriptors.get(loopIndex);
		List<KeyValuePair> args = taskTemplatesArgs.get(loopIndex);
		List<KeyValuePair> opts = taskTemplatesJavaOpts.get(loopIndex);
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

		ajaxResponseRenderer.addCallback(new JavaScriptCallback() {
			@Override
			public void run(JavaScriptSupport javascriptSupport) {
				javascriptSupport.addScript("$('#%s').dialog('close');", dialog.getClientId());
			}
		});
	}

	void onSubmitFromTaskDescriptorForm(int loopIndex) {
		TaskDescriptor taskDescriptor = tasksDescriptors.get(loopIndex);
		List<KeyValuePair> args = tasksArgs.get(loopIndex);
		List<KeyValuePair> opts = tasksJavaOpts.get(loopIndex);
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

		ajaxResponseRenderer.addCallback(new JavaScriptCallback() {
			@Override
			public void run(JavaScriptSupport javascriptSupport) {
				javascriptSupport.addScript("$('#%s').dialog('close');", dialog.getClientId());
			}
		});
	}

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

}
