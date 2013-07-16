package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.core.PropertyReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Author: donarus
 */
public class WorkingDirectoryResolver {

    private static final String HR_WRKDIR_NAME_PROPERTY_NAME = "hostruntime.wrkdir.name";
    private static final String HR_WRKDIR_NAME_DEFAULT = ".HostRuntime";
    private static final String HR_TASKS_WRKDIR_PARENT = "tasks";

    private static final String HR_TASKS_WRKDIR_MAX_HISTORY_PROPERTY_NAME = "hostruntime.tasks.wrkdir.maxHistory";
    private static final Integer HR_TASKS_WRKDIR_MAX_HISTORY_DEFAULT = 4;
    private Properties properties;

    public WorkingDirectoryResolver(Properties properties) {
        this.properties = properties;
    }

    public File getHostRuntimeWorkingDirectory() {
        final PropertyReader propReader = PropertyReader.on(properties);

        final String hrWrkDir = propReader.getString(HR_WRKDIR_NAME_PROPERTY_NAME, HR_WRKDIR_NAME_DEFAULT);
        return new File(hrWrkDir);
    }

    public File getTasksWorkingDirectory() {
        final PropertyReader propReader = PropertyReader.on(properties);
        Integer hrTasksWrkDirMaxHistory = propReader.getInteger(HR_TASKS_WRKDIR_MAX_HISTORY_PROPERTY_NAME, HR_TASKS_WRKDIR_MAX_HISTORY_DEFAULT);
        if (hrTasksWrkDirMaxHistory < 1) {
            hrTasksWrkDirMaxHistory = 1;
        }

        File hrWrkDir = getHostRuntimeWorkingDirectory();
        File tasksParentDir = new File(hrWrkDir, HR_TASKS_WRKDIR_PARENT);

        if (tasksParentDir.exists()) {
            List<File> oldTasksWrkDirs = Arrays.asList(tasksParentDir.listFiles());
            Collections.sort(oldTasksWrkDirs, NameFileComparator.NAME_INSENSITIVE_COMPARATOR);

            for (int i = 0; i < oldTasksWrkDirs.size() - hrTasksWrkDirMaxHistory + 1; i++) {
                FileUtils.deleteQuietly(oldTasksWrkDirs.get(i));
            }
        }

        return new File(tasksParentDir, "" + new Date().getTime());

    }
}
