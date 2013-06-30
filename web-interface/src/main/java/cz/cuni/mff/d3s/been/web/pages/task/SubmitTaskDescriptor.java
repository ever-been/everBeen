package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.ModeEnum;
import cz.cuni.mff.d3s.been.core.task.ObjectFactory;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.model.ConversationHolder;
import cz.cuni.mff.d3s.been.web.pages.Index;
import cz.cuni.mff.d3s.been.web.pages.Overview;
import cz.cuni.mff.d3s.been.web.pages.Page;
import cz.cuni.mff.d3s.been.web.utils.KeyValuePair;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ConversationHolder<Map<String, Object>> sessionHolder;

    /**
     * Task descriptor loaded in onActivate() method
     */
    @Property
    TaskDescriptor taskDescriptor;

    /**
     * Argument list transformed from list of string on taskDescriptor
     * to list of keyValuePairs. We have to assign temporary IDs (keys)
     * to arguments (values) specified in task descriptor to tie them
     * conclusively from JAVA to JAVASCRIPT and vice versa. (Arguments
     * in task descriptor has no unique ID)
     */
    @Property
    List<KeyValuePair> args;

    /**
     * Java option list transformed from list of string on taskDescriptor
     * to list of keyValuePairs. We have to assign temporary IDs (keys)
     * to options (values) specified in task descriptor to tie them
     * conclusively from JAVA to JAVASCRIPT and vice versa. (Java Options
     * in task descriptor has no unique ID)
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


    /**
     * Used in loop over arguments
     */
    @Property
    private KeyValuePair arg;

    /**
     * Used in loop over java options
     */
    @Property
    private KeyValuePair opt;

    /**
     * loop index used in loops in template
     */
    @Property
    @SuppressWarnings("unused")
    private int loopIndex;

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
     * @return null if page has been already activated or page has been correctly activated.
     *         Redirect to {@link Index} page otherwise.
     */
    Object onActivate(String conversationId) {
        // we have to check if page has been already activated, because we don't want
        // to override context of already activated pages
        if (activated) {
            return null;
        }

        if (!sessionHolder.contains(conversationId)) {
            // FIXME .. inform user in proper way?
            return Index.class;
        } else {
            taskDescriptor = (TaskDescriptor) sessionHolder.get(conversationId).get(KEY_TASK_DESCRIPTOR);
            args = (List<KeyValuePair>) sessionHolder.get(conversationId).get(KEY_ARGS);
            opts = (List<KeyValuePair>) sessionHolder.get(conversationId).get(KEY_OPTS);

            this.conversationId = conversationId;

            activated = true;
            return null;
        }
    }


    /**
     * Setup method. Loads task descriptor (corresponding to given parameters)
     * using been api and sets this descriptor as editable property for this component.
     *
     * @param groupId        group id of {@link BpkIdentifier} to which the underlying {@link TaskDescriptor} belows
     * @param bpkId          bpk id of {@link BpkIdentifier} to which the underlying {@link TaskDescriptor} belows
     * @param version        version id of {@link BpkIdentifier} to which the underlying {@link TaskDescriptor} belows
     * @param descriptorName name of concrete {@link TaskDescriptor} for {@link BpkIdentifier} identified by previous parameters
     * @return null (see tapestry page documentation about return values from onActivate and onPassivate methods)
     */
    @SuppressWarnings("unused")
    Object onActivate(String groupId, String bpkId, String version, String descriptorName) {
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

        if (!taskDescriptor.isSetJava()) {
            taskDescriptor.setJava(new ObjectFactory().createJava());
            taskDescriptor.getJava().setJavaOptions(new ObjectFactory().createJavaOptions());
        }

        if (!taskDescriptor.isSetLoadMonitoring()) {
            taskDescriptor.setLoadMonitoring(new ObjectFactory().createLoadMonitoring());
        }

        if (!taskDescriptor.isSetExclusive()) {
            taskDescriptor.setExclusive(TaskExclusivity.NON_EXCLUSIVE);
        }

        if (!taskDescriptor.isSetDebug()) {
            taskDescriptor.setDebug(new ObjectFactory().createDebug());
            taskDescriptor.getDebug().setMode(ModeEnum.NONE);

            try {
                taskDescriptor.getDebug().setHost(java.net.InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                // ignored, because we are trying only to help
                // person who is trying to launch this task
                // descriptor.... This is used only as first hint
                // in form for submitting task descriptor.
            }
            taskDescriptor.getDebug().setPort(9000);
        }

        args = new ArrayList<>();
        for (int i = 0; i < taskDescriptor.getArguments().getArgument().size(); i++) {
            args.add(new KeyValuePair(i, taskDescriptor.getArguments().getArgument().get(i)));
        }


        opts = new ArrayList<>();
        for (int i = 0; i < taskDescriptor.getJava().getJavaOptions().getJavaOption().size(); i++) {
            opts.add(new KeyValuePair(i, taskDescriptor.getJava().getJavaOptions().getJavaOption().get(i)));
        }

        HashMap<String, Object> conversationArgs = new HashMap<String, Object>();
        conversationArgs.put(KEY_OPTS, opts);
        conversationArgs.put(KEY_ARGS, args);
        conversationArgs.put(KEY_TASK_DESCRIPTOR, taskDescriptor);
        this.conversationId = sessionHolder.set(conversationArgs);

        activated = true;
        return null;
    }

    /**
     * @return conversationId as passivate parameter (used on next
     *         onActivate parameter). See tapestry documentation to get more
     *         information about expected return values from onActivate and
     *         onPassivate methods
     */
    Object onPassivate() {
        return conversationId;
    }


    // -----------------------------
    // FORM HANDLING
    // -----------------------------

    /**
     * This handler is called when users click on form SUBMIT button.
     * Submits task to BEEN cluster using {@link cz.cuni.mff.d3s.been.api.BeenApi}
     *
     * @return redirect to {@link Overview} page
     */
    @SuppressWarnings("unused")
    Object onSuccessFromSubmitTaskForm() {
        this.api.getApi().submitTask(taskDescriptor);
        args.remove(null);
        taskDescriptor.getArguments().getArgument().clear();
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
        return Overview.class;
    }

    /**
     * Creates new empty argument which will be added to indexed argument list.
     *
     * @return
     */
    Object onAddRowFromArgumentList() {
        KeyValuePair newArg = new KeyValuePair(args.size(), "");
        args.add(newArg);
        return newArg;
    }

    /**
     * Removes indexed argument from argument list ('remove' is not the really exact description.
     * Instead of direct removing from list is option with given index(kvp.key) set to null)
     *
     * @param kvp
     */
    void onRemoveRowFromArgumentList(KeyValuePair kvp) {
        args.set(kvp.key, null);
    }

    /**
     * Creates new empty java option which will be added to indexed java option list.
     *
     * @return
     */
    Object onAddRowFromJavaOptList() {
        KeyValuePair opt = new KeyValuePair(opts.size(), "");
        opts.add(opt);
        return opt;
    }

    /**
     * Removes indexed java option from java option list ('remove' is not the really exact
     * description. Instead of direct removing from list is argument with given index(kvp.key)
     * set to null)
     *
     * @param kvp
     */
    void onRemoveRowFromJavaOptList(KeyValuePair kvp) {
        opts.set(kvp.key, null);
    }

    /**
     * Encoder used to translate values between server(java) and Client (browser)
     *
     * @return
     */
    public ValueEncoder<KeyValuePair> getKeyValuePairEncoder() {
        return new ValueEncoder<KeyValuePair>() {
            @Override
            public String toClient(KeyValuePair value) {
                return String.valueOf(value.key);
            }

            @Override
            public KeyValuePair toValue(String value) {
                return args.get(Integer.parseInt(value));
            }
        };
    }

    /**
     * Collects possible values for TaskExclusivity select box
     * @return
     */
    public TaskExclusivity[] getAvailableExclusivities() {
        return TaskExclusivity.values();
    }

    /**
     * Creates value encoder for Task Exclusivity entities.
     * @return
     */
    public ValueEncoder<TaskExclusivity> getTaskExclusivityEncoder() {
        return new ValueEncoder<TaskExclusivity>() {
            @Override
            public String toClient(TaskExclusivity value) {
                if (value == null) {
                    return null;
                }
                return value.value();
            }

            @Override
            public TaskExclusivity toValue(String clientValue) {
                if (clientValue == null) {
                    return null;
                }
                return TaskExclusivity.fromValue(clientValue);
            }
        };
    }



    /**
     * Collects possible values for ModeEnum select box (ModeEnum = enum for possible
     * values for debug mode)
     * @return
     */
    public ModeEnum[] getAvailableDebugModes() {
        return ModeEnum.values();
    }

    /**
     * Creates value encoder for ModeEnum (used for specifying debug mode) entities.
     * @return
     */
    public ValueEncoder<ModeEnum> getDebugModeEncoder() {
        return new ValueEncoder<ModeEnum>() {
            @Override
            public String toClient(ModeEnum value) {
                if (value == null) {
                    return null;
                }
                return value.value();
            }

            @Override
            public ModeEnum toValue(String clientValue) {
                if (clientValue == null) {
                    return null;
                }
                return ModeEnum.fromValue(clientValue);
            }
        };
    }

}
