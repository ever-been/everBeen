package cz.cuni.mff.d3s.been.web.pages.task;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.ObjectFactory;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskProperty;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.pages.Overview;
import cz.cuni.mff.d3s.been.web.pages.Page;
import cz.cuni.mff.d3s.been.web.utils.KeyValuePair;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;

import java.util.*;
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
    @Persist(DEFAULT_PERSIST_MODE)
    TaskDescriptor taskDescriptor;

    /**
     * Injected form component for task submitting.
     */
    @Component
    @SuppressWarnings("unused")
    private Form submitTaskForm;

    /**
     * We have to assign temporary ID (key) to argument list (values) specified in task descriptor
     * to tie them conclusively from JAVA to JAVASCRIPT and vice versa. (Arguments in task descriptor has no
     * unique ID)
     */
    @Property
    @Persist(DEFAULT_PERSIST_MODE)
    List<KeyValuePair> args;

    @Property
    @Persist(DEFAULT_PERSIST_MODE)
    List<KeyValuePair> opts;

    /**
     * property holder for argument in template
     */
    @Property
    private KeyValuePair arg;


    @Property
    private KeyValuePair opt;

    /**
     * loop index used in loops in template
     */
    @Property
    @SuppressWarnings("unused")
    private int loopIndex;

    /**
     * group id of bpk with which this page has been initialized
     */
    private String groupId;

    /**
     * bpk id with of bpk which this page has been initialized
     */
    private String bpkId;

    /**
     * version of bpk with which this page has been initialized
     */
    private String version;

    /**
     * descriptor of bpk with which this page has been initialized
     */
    private String descriptorName;

    /**
     * initialization method
     *
     * @param groupId        group id of {@link BpkIdentifier} to which the underlying {@link TaskDescriptor} belows
     * @param bpkId          bpk id of {@link BpkIdentifier} to which the underlying {@link TaskDescriptor} belows
     * @param version        version id of {@link BpkIdentifier} to which the underlying {@link TaskDescriptor} belows
     * @param descriptorName name of concrete {@link TaskDescriptor} for {@link BpkIdentifier} identified by previous parameters
     * @return null if conversation has been already started, self if conversation has been started in this
     *         method call (see tapestry documentation for more info about return values from onActivate method)
     */
    Object onActivate(String groupId, String bpkId, String version, String descriptorName) {
        return onActivate(groupId, bpkId, version, descriptorName, conversationId);
    }


    /**
     * Setup method. Loads task descriptor (corresponding to given parameters)
     * using been api and sets this descriptor as editable property for this component.
     *
     * @param groupId        group id of {@link BpkIdentifier} to which the underlying {@link TaskDescriptor} belows
     * @param bpkId          bpk id of {@link BpkIdentifier} to which the underlying {@link TaskDescriptor} belows
     * @param version        version id of {@link BpkIdentifier} to which the underlying {@link TaskDescriptor} belows
     * @param descriptorName name of concrete {@link TaskDescriptor} for {@link BpkIdentifier} identified by previous parameters
     * @param conversationId id of current conversation
     * @return null if conversation with given has been already started, self if conversation has been started in this
     *         method call (see tapestry documentation for more info about return values from onActivate method)
     */
    @SuppressWarnings("unused")
    Object onActivate(String groupId, String bpkId, String version, String descriptorName, String conversationId) {
        this.groupId = groupId;
        this.bpkId = bpkId;
        this.version = version;
        this.descriptorName = descriptorName;

        if (!conversationManager.isActiveConversation(conversationId)) {
            return createConversation();
        }

        this.conversationId = conversationId;

        if (taskDescriptor != null) {
            return null;
        }


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


        args = new ArrayList<>();
        for (int i = 0; i < taskDescriptor.getArguments().getArgument().size(); i++) {
            args.add(new KeyValuePair(i, taskDescriptor.getArguments().getArgument().get(i)));
        }


        opts = new ArrayList<>();
        for (int i = 0; i < taskDescriptor.getJava().getJavaOptions().getJavaOption().size(); i++) {
            opts.add(new KeyValuePair(i, taskDescriptor.getJava().getJavaOptions().getJavaOption().get(i)));
        }

        return null;
    }

    /**
     *
     * @return array of groupId, bpkId, version, descriptorName and conversationId ... The same values which has been
     * set in onActivate method. This array will be added to each request from client side (in all links, submits etc.,
     * so this class will be activated with right parameters each time.
     */
    Object onPassivate() {
        return new String[]{
                groupId,
                bpkId,
                version,
                descriptorName,
                conversationId
        };
    }


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

    // -----------------------------
    // HANDLING ARGUMENTS
    // -----------------------------


    /**
     * Creates new empty argument which will be added to indexed argument list.
     * @return
     */
    Object onAddRowFromArgumentList() {
        KeyValuePair newArg = new KeyValuePair(args.size(), "");
        args.add(newArg);
        return newArg;
    }

    /**
     * Removes indexed argument from argument list ('remove' is not the really exact description. Instead of direct
     * removing from list is argument with given index(kvp.key) set to null)
     * @param kvp
     */
    void onRemoveRowFromArgumentList(KeyValuePair kvp) {
        args.set(kvp.key, null);
    }


    Object onAddRowFromJavaOptList() {
        KeyValuePair opt = new KeyValuePair(opts.size(), "");
        opts.add(opt);
        return opt;
    }

    void onRemoveRowFromJavaOptList(KeyValuePair kvp) {
        opts.set(kvp.key, null);
    }

    /**
     * Encoder used to translate values between server(java) and Client (browser)
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

}
