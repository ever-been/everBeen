package cz.cuni.mff.d3s.been.hostruntime;

import static cz.cuni.mff.d3s.been.hostruntime.HostRuntimeConfiguration.*;

import java.io.File;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;

import cz.cuni.mff.d3s.been.util.PropertyReader;

/**
 * Resolver of working directories
 * 
 * @author donarus
 */
public class WorkingDirectoryResolver {
	private static final String TASKS_WRKDIR_PARENT = "tasks";

	private Properties properties;

	/**
	 * Creates new WorkingDirectoryResolver.
	 * 
	 * @param properties
	 *          resolver's settings
	 */
	public WorkingDirectoryResolver(Properties properties) {
		this.properties = properties;
	}

	/**
	 * Returns this Host Runtime's working directory.
	 * 
	 * @return this Host Runtime's working directory
	 */
	public File getHostRuntimeWorkingDirectory() {
		final PropertyReader propReader = PropertyReader.on(properties);

		final String hrWrkDir = propReader.getString(WRKDIR_NAME, DEFAULT_WRKDIR_NAME);
		return new File(hrWrkDir);
	}

	/**
	 * Returns this Host Runtime's task working directory.
	 * 
	 * @return this Host Runtime's task working directory
	 */
	public File getTasksWorkingDirectory() {
		final PropertyReader propReader = PropertyReader.on(properties);
		Integer hrTasksWrkDirMaxHistory = propReader.getInteger(TASKS_WRKDIR_MAX_HISTORY, DEFAULT_TASKS_WRKDIR_MAX_HISTORY);
		if (hrTasksWrkDirMaxHistory < 1) {
			hrTasksWrkDirMaxHistory = 1;
		}

		File hrWrkDir = getHostRuntimeWorkingDirectory();
		File tasksParentDir = new File(hrWrkDir, TASKS_WRKDIR_PARENT);

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
