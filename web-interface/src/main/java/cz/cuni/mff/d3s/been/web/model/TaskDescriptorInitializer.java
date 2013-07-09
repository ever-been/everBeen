package cz.cuni.mff.d3s.been.web.model;


import cz.cuni.mff.d3s.been.core.task.ModeEnum;
import cz.cuni.mff.d3s.been.core.task.ObjectFactory;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;

import java.net.UnknownHostException;
import java.util.List;

/**
 * This class is used to initialize TaskDescriptor from bpk. The right meaning
 * of term 'initialization' is to replace null values in given task descriptor
 * by empty structures. The main purpose of this pseudo-initialization is to
 * prepare task descriptor for using in Tapestry forms.
 */
public class TaskDescriptorInitializer {

    /**
     * If debug port is not specified in given task descriptor, this value will be used as default value.
     */
    public static final int DEFAULT_DEBUG_PORT = 9000;

    /**
     * If debug mode is not specified in given task descriptor, this value will be used as default value.
     */
    public static final ModeEnum DEFAULT_DEBUG_MODE = ModeEnum.NONE;

    /**
     * If task exclusivity is not specified in given task descriptor, this value will be used as default value.
     */
    public static final TaskExclusivity DEFAULT_TASK_EXCLUSIVITY = TaskExclusivity.NON_EXCLUSIVE;


    /**
     * Initialize given task descriptor. String list of arguments and String list of java opts are transformed to Lists
     * of key value pair.
     *
     * @param taskDescriptor task descriptor which will be initialized
     * @param args           list of key value pairs to which all args from task descriptor will be inserted
     * @param javaOpts       list of key value pairs to which all java opts from task descriptor will be inserted
     */
    public static void initialize(TaskDescriptor taskDescriptor, List<KeyValuePair> args, List<KeyValuePair> javaOpts) {
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
            taskDescriptor.setExclusive(DEFAULT_TASK_EXCLUSIVITY);
        }

        if (!taskDescriptor.isSetFailurePolicy()) {
            taskDescriptor.setFailurePolicy(new ObjectFactory().createFailurePolicy());
        }

        if (!taskDescriptor.isSetDebug()) {
            taskDescriptor.setDebug(new ObjectFactory().createDebug());
            taskDescriptor.getDebug().setMode(DEFAULT_DEBUG_MODE);

            try {
                taskDescriptor.getDebug().setHost(java.net.InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                // this is only default initialization, we can simply ignore this exception
            }
            // we can set the port because mode is DEBUG by default
            taskDescriptor.getDebug().setPort(DEFAULT_DEBUG_PORT);

        }

        // insert all args and java opts to given lists of key value pais
        for (int i = 0; i < taskDescriptor.getArguments().getArgument().size(); i++) {
            args.add(new KeyValuePair(i, taskDescriptor.getArguments().getArgument().get(i)));
        }


        for (int i = 0; i < taskDescriptor.getJava().getJavaOptions().getJavaOption().size(); i++) {
            javaOpts.add(new KeyValuePair(i, taskDescriptor.getJava().getJavaOptions().getJavaOption().get(i)));
        }
    }

}
