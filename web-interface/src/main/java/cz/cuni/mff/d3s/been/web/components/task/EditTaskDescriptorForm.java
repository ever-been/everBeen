package cz.cuni.mff.d3s.been.web.components.task;

import cz.cuni.mff.d3s.been.core.task.ModeEnum;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;
import cz.cuni.mff.d3s.been.web.components.Component;
import cz.cuni.mff.d3s.been.web.model.KeyValuePair;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.util.List;


public class EditTaskDescriptorForm extends Component {


    /* alternative args */
    @Parameter(name = "index")
    volatile Integer _index;

    @Parameter(name = "tasksArgs")
    List<List<KeyValuePair>> _tasksArgs;

    @Parameter(name = "tasksJavaOpts")
    List<List<KeyValuePair>> _tasksJavaOpts;

    @Parameter(name = "tasksDescriptors")
    List<TaskDescriptor> _tasksDescriptors;

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

    @Cached
    public List<KeyValuePair> getArgs() {
        return _args != null ? _args : _tasksArgs.get(_index);
    }


    @Cached
    public List<KeyValuePair> getOpts() {
        return _opts != null ? _opts : _tasksJavaOpts.get(_index);
    }


    @Cached
    public TaskDescriptor getTaskDescriptor() {
        return _taskDescriptor != null ? _taskDescriptor : _tasksDescriptors.get(_index);
    }


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

    /**
     * Creates new empty argument which will be added to indexed argument list.
     *
     * @return
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
     * @param kvp
     */
    void onRemoveRowFromArgumentList(KeyValuePair kvp) {
        getArgs().set(kvp.key, new KeyValuePair(getArgs().size(), null));
    }

    /**
     * Creates new empty java option which will be added to indexed java option list.
     *
     * @return
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
     * @param kvp
     */
    void onRemoveRowFromJavaOptList(KeyValuePair kvp) {
        getOpts().set(kvp.key, new KeyValuePair(getOpts().size(), null));
    }

    /**
     * Encoder used to translate values between server(java) and Client (browser)
     *
     * @return
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
     * @return
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
     * Collects possible values for TaskExclusivity select box
     *
     * @return
     */
    public TaskExclusivity[] getAvailableExclusivities() {
        return TaskExclusivity.values();
    }

    /**
     * Creates value encoder for Task Exclusivity entities.
     *
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
     *
     * @return
     */
    public ModeEnum[] getAvailableDebugModes() {
        return ModeEnum.values();
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
