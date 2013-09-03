package cz.cuni.mff.d3s.been.web.components.task;

import cz.cuni.mff.d3s.been.core.task.ModeEnum;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;
import cz.cuni.mff.d3s.been.web.components.Component;
import cz.cuni.mff.d3s.been.web.model.KeyValuePair;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.List;


/**
 * This component is used for editing TaskDescriptor properties and must be used only and only inside
 * an existing form.
 * <br/>
 * This component cane be used in two different ways:<br/><br/>
 * <b>1. way</b>
 * <pre>
 *     &lt;t:task.editTaskDescriptorForm t:args="args" t:javaOpts="javaOpts"
 *         t:taskDescriptor="taskDescriptor"/&gt;
 * </pre>
 * <b>taskDescriptor</b> : {@link TaskDescriptor} for which are the form fields generated<br/>
 * <b>args</b> : {@link List} of {@link KeyValuePair}s with arguments specified in task descriptor.
 * We can't use List of Strings because we have to distinguish between arguments (some
 * arguments can be equal each other)<br/>
 * <b>javaOpts</b> : {@link List} of {@link KeyValuePair}s with java options specified in task descriptor.
 * We can't use List of string because we have to distinguish between arguments (some
 * arguments can be equal each other)
 * <br/>
 * <br/>
 * <b>2. way</b>
 * <pre>
 *     &lt;t:task.editTaskDescriptorForm t:index="index" t:tasksArgs="tasksArgs"
 *         t:tasksJavaOpts="tasksJavaOpts" t:tasksDescriptors="tasksDescriptors"/&gt;
 * </pre>
 * <b>tasksDescriptors</b> : {@link List} of {@link TaskDescriptor}s in which the correct one
 * is situated at position given in argument 'index'<br/>
 * <p/>
 * <b>tasksArgs</b> : {@link List} of {@link List}s of {@link KeyValuePair}s in which the correct one
 * is situated at position given in argument 'index'<br/>
 * <p/>
 * <b>tasksJavaOpts</b> : {@link List} of {@link List}s of {@link KeyValuePair}s in which the correct one
 * is situated at position given in argument 'index'<br/>
 * <p/>
 * <b>index</b> : this index defines at which position are correct items for editing<br/>
 * <br/><br/><br/>
 * It is not allowed to combine parameters from incompatible ways.<br/>
 *
 * @author Tadeas Palusga
 */
public class EditTaskDescriptorForm extends Component {

    // -----------------------------
    //  1'ST WAY PARAMETERS
    // -----------------------------

    /**
     * Task descriptor loaded in onActivate() method
     */
    @Parameter(name = "taskDescriptor")
    TaskDescriptor _taskDescriptor;

    /**
     * Argument list transformed from list of string on taskDescriptor
     * to list of keyValuePairs. We have to assign temporary IDs (keys)
     * to arguments (values) specified in task descriptor to tie them
     * conclusively from JAVA to JAVASCRIPT and vice versa. (Arguments
     * in task descriptor has no unique ID)
     */
    @Parameter(name = "args")
    List<KeyValuePair> _args;

    /**
     * Java option list transformed from list of string on taskDescriptor
     * to list of keyValuePairs. We have to assign temporary IDs (keys)
     * to options (values) specified in task descriptor to tie them
     * conclusively from JAVA to JAVASCRIPT and vice versa. (Java Options
     * in task descriptor has no unique ID)
     */
    @Parameter(name = "javaOpts")
    List<KeyValuePair> _opts;


    // -----------------------------
    // 2'ND WAY PARAMETERS
    // -----------------------------

    /**
     * Specify position of correct items to idet in lists given as this component's parameters.
     * It is expected that items exists in all lists at given position.
     */
    @Parameter(name = "index")
    volatile Integer _index;

    /**
     * List of lists of task arguments (represented by KeyValuePairs) where selection of the correct list is based on 'index' parameter
     */
    @Parameter(name = "tasksArgs")
    List<List<KeyValuePair>> _tasksArgs;

    /**
     * List of lists of task java opts (represented by KeyValuePairs) where selection of the  correct list is based on 'index' parameter
     */
    @Parameter(name = "tasksJavaOpts")
    List<List<KeyValuePair>> _tasksJavaOpts;


    /**
     * List of task descriptors where selection of the correct one is based on 'index' parameter
     */
    @Parameter(name = "tasksDescriptors")
    List<TaskDescriptor> _tasksDescriptors;


    // -----------------------------
    // COMPONENT INITIALIZATION
    // -----------------------------

    /**
     * Checks if component has been instantiated with valid combination of parameters.
     *
     * @throws Exception if component has been instantiated with invalid combination of parameters
     */
    @SetupRender
    void setupRender() throws Exception {
        if (this._tasksArgs != null || this._tasksJavaOpts != null || this._tasksDescriptors != null || this._index != null) {
            if (_args != null || _opts != null || _taskDescriptor != null) {
                // UGLY MEGA GIGA HYPER EXTRA PHOEY FIXME exception handling
                throw new Exception("You can't define both alternative args (index, tasksArgs, " +
                        "tasksJavaOpts, tasksDescriptors) and primary args (taskDescriptor, args, javaOpts)");
            } else {
                if (this._tasksArgs == null || this._tasksJavaOpts == null || this._tasksDescriptors == null || this._index == null) {
                    throw new Exception("You must specify all alternative args (index, tasksArgs, " +
                            "tasksJavaOpts, tasksDescriptors) if you want to use them");
                    // UGLY MEGA GIGA HYPER EXTRA PHOEY FIXME exception handling

                }
            }
        }
    }


    // -----------------------------
    // TEMPLATE PROPERTY GETTERS
    // -----------------------------

    /**
     * @return correct list of KeyValuePairs representing task descriptor arguments to be edited
     */
    public List<KeyValuePair> getArgs() {
        return _args != null ? _args : _tasksArgs.get(_index);
    }

    /**
     * @return correct list of KeyValuePairs representing task descriptor java options to be edited
     */
    public List<KeyValuePair> getOpts() {
        return _opts != null ? _opts : _tasksJavaOpts.get(_index);
    }

    /**
     * @return correct task descriptor to be edited
     */
    public TaskDescriptor getTaskDescriptor() {
        return _taskDescriptor != null ? _taskDescriptor : _tasksDescriptors.get(_index);
    }


    // -----------------------------
    // TEMPLATE HELPER PROPERTIES
    // -----------------------------

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
     * loop index used in different loops in template
     */
    @Property
    @SuppressWarnings("unused")
    private int loopIndex;


    // -----------------------------
    // DYNAMIC AJAX ADD/REMOVE ACTIONS
    // -----------------------------

    /**
     * Creates new empty argument which will be added to indexed argument list.
     *
     * @return The argument
     */
    Object onAddRowFromArgumentList() {
        System.out.println(_index);
        KeyValuePair newArg = new KeyValuePair(getArgs().size(), "");
        getArgs().add(newArg);
        return newArg;
    }

    /**
     * Removes indexed argument from argument list ('remove' is not the really exact description.
     * Instead of direct removing from list is option with given index(kvp.key) set to null)
     *
     * @param kvp Key-Value pair to be removed
     */
    void onRemoveRowFromArgumentList(KeyValuePair kvp) {
        getArgs().set(kvp.key, new KeyValuePair(getArgs().size(), null));
    }

    /**
     * Creates new empty java option which will be added to indexed java option list.
     *
     * @return The option
     */
    Object onAddRowFromJavaOptList() {
        KeyValuePair opt = new KeyValuePair(getOpts().size(), "");
        getOpts().add(opt);
        return opt;
    }

    /**
     * Removes indexed java option from java option list ('remove' is not the really exact
     * description. Instead of direct removing from list is argument with given index(kvp.key)
     * set to null)
     *
     * @param kvp Key-Value pair to be removed
     */
    void onRemoveRowFromJavaOptList(KeyValuePair kvp) {
        getOpts().set(kvp.key, new KeyValuePair(getOpts().size(), null));
    }


    // -----------------------------
    // VALUES FOR FORM LIST SELECTS
    // -----------------------------

    /**
     * Collects possible values for TaskExclusivity select box
     *
     * @return Possible task exclusivity values
     */
    public TaskExclusivity[] getAvailableExclusivities() {
        return TaskExclusivity.values();
    }


    /**
     * Collects possible values for ModeEnum select box (ModeEnum = enum for possible
     * values for debug mode)
     *
     * @return
     */
    public ModeEnum[] getAvailableDebugModes() {
        return ModeEnum.values();
    }

    // -----------------------------
    // JAVA2HTML/JS AND HTML/JS2JAVA TRANSLATION METHODS
    // -----------------------------

    /**
     * Encoder used to translate values between server(java) and Client (browser)
     *
     * @return encoder for arguments
     */
    public ValueEncoder<KeyValuePair> getArgsKeyValuePairEncoder() {
        return new ValueEncoder<KeyValuePair>() {
            @Override
            public String toClient(KeyValuePair value) {
                return String.valueOf(value.key);
            }

            @Override
            public KeyValuePair toValue(String value) {
                return getArgs().get(Integer.parseInt(value));
            }
        };
    }

    /**
     * Encoder used to translate values between server(java) and Client (browser)
     *
     * @return encoder for java options
     */
    public ValueEncoder<KeyValuePair> getJavaOptsKeyValuePairEncoder() {
        return new ValueEncoder<KeyValuePair>() {
            @Override
            public String toClient(KeyValuePair value) {
                return String.valueOf(value.key);
            }

            @Override
            public KeyValuePair toValue(String value) {
                return getOpts().get(Integer.parseInt(value));
            }
        };
    }

    /**
     * Creates value encoder for Task Exclusivity entities.
     *
     * @return encoder for task exclusivities
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
     * Creates value encoder for ModeEnum (used for specifying debug mode) entities.
     *
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
