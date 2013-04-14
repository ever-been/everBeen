package cz.cuni.mff.d3s.been.hostruntime.task;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;

import cz.cuni.mff.d3s.been.hostruntime.TaskException;

public class TaskProcess {

	public static final long NO_TIMEOUT = ExecuteWatchdog.INFINITE_TIMEOUT;

	private final File wrkDir;

	private final CommandLine cmd;

	private final Map<String, String> environment;

	private final ExecuteStreamHandler streamhandler;

	private final ExecuteWatchdog watchdog;

	/**
	 * 
	 * @param cmd
	 * @param wrkDir
	 * @param environment
	 * @param timeout in seconds
	 */
	public TaskProcess(CommandLine cmd, File wrkDir, Map<String, String> environment, ExecuteStreamHandler streamhandler, long timeout) {
		this.cmd = cmd;
		this.wrkDir = wrkDir;
		this.environment = environment;
		this.streamhandler = streamhandler;
		this.watchdog = new ExecuteWatchdog(timeout <= 0 ? NO_TIMEOUT : TimeUnit.SECONDS.toMillis(timeout));
	}

	public void start() throws TaskException {
		Executor executor = prepare();
		start(executor);
	}

	private Executor prepare() {
		Executor executor = new DefaultExecutor();
		executor.setWorkingDirectory(wrkDir);
		executor.setWatchdog(watchdog);
		executor.setStreamHandler(streamhandler);

		return executor;
		/*
		 * we do not care about executor.addProcessDestroyer(..) shutdown hook for
		 * each task should be handled manually (we want to sent some info after
		 * successful termination to cluster)
		 */
	}

	private void start(Executor executor) throws TaskException {
		try {
			executor.execute(cmd, environment);
		} catch (ExecuteException e) {
			if (executor.isFailure(e.getExitValue())) {
				throw new TaskException(String.format("Task process ended with error exit value %d", e.getExitValue()), e);
			} else {
				throw new TaskException(String.format("Execution of task process failed"), e);
			}
		} catch (IOException e) {
			throw new TaskException("Execution of task process failed", e);
		}
	}

	public void kill() {
		watchdog.destroyProcess();
	}

}
