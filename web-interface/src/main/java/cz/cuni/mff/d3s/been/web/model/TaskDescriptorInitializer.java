package cz.cuni.mff.d3s.been.web.model;


import cz.cuni.mff.d3s.been.core.task.ModeEnum;
import cz.cuni.mff.d3s.been.core.task.ObjectFactory;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;

import java.net.UnknownHostException;
import java.util.List;

public class TaskDescriptorInitializer {

    public static final int DEFAULT_DEBUG_PORT = 9000;
    public static final ModeEnum DEFAULT_DEBUG_MODE = ModeEnum.NONE;
    public static final TaskExclusivity DEFAULT_TASK_EXCLUSIVITY = TaskExclusivity.NON_EXCLUSIVE;


    public static void initialize(TaskDescriptor taskDescriptor, List<KeyValuePair> args, List<KeyValuePair> javaOpts) {
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
                // ignored, because we are trying only to help
                // person who is trying to launch this task
                // descriptor.... This is used only as first hint
                // in form for submitting task descriptor.
            }
            taskDescriptor.getDebug().setPort(DEFAULT_DEBUG_PORT);

        }

        for (int i = 0; i < taskDescriptor.getArguments().getArgument().size(); i++) {
            args.add(new KeyValuePair(i, taskDescriptor.getArguments().getArgument().get(i)));
        }


        for (int i = 0; i < taskDescriptor.getJava().getJavaOptions().getJavaOption().size(); i++) {
            javaOpts.add(new KeyValuePair(i, taskDescriptor.getJava().getJavaOptions().getJavaOption().get(i)));
        }
    }
}
