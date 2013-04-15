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

/**
 * 
 * @author "Tadeas Palusga"
 * 
 */
public class TaskProcess {

	/** magic constant which is used to set process timeout to infinite */
	public static final long NO_TIMEOUT = ExecuteWatchdog.INFINITE_TIMEOUT;

	/** process working directory */
	private final File wrkDir;

	/** prepared command line for the process */
	private final CommandLine cmd;

	/** environment variables set for the process */
	private final Map<String, String> environment;

	/** std/err output stream handlers for the process */
	private final ExecuteStreamHandler streamhandler;

	/** process timeout in seconds */
	private long timeoutInMillis;

	/** long time run watchdog. */
	private final ExecuteWatchdog watchdog;

	private boolean killed;

	/**
	 * Creates new task process.
	 * 
	 * @param cmd
	 *          process command line
	 * @param wrkDir
	 *          working directory of the process
	 * @param environment
	 *          environment variables for process to be set
	 * @param timeout
	 *          in seconds - process will be terminated after this timeout
	 */
	public TaskProcess(CommandLine cmd, File wrkDir, Map<String, String> environment, ExecuteStreamHandler streamhandler, long timeout) {
		this.cmd = cmd;
		this.wrkDir = wrkDir;
		this.environment = environment;
		this.streamhandler = streamhandler;
		this.timeoutInMillis = timeout <= 0 ? NO_TIMEOUT : TimeUnit.SECONDS.toMillis(timeout);
		this.watchdog = new ExecuteWatchdog(this.timeoutInMillis);
	}

	/**
	 * Starts process using Apache {@link Executor}.
	 * 
	 * @return process exit value (throws {@link TaskException} on error exit
	 *         values)
	 * @throws TaskException
	 *           when process cannot be started from some reason or process ends
	 *           with error exit value
	 */
	public int start() throws TaskException {
		Executor executor = prepare();
		return start(executor);
	}

	/**
	 * Prepares executor for the underlying process. Sets the working directory,
	 * watchdog (watches for long running time) and std/err output stream handler.
	 * 
	 * @return prepared executor
	 */
	private Executor prepare() {
		Executor executor = new DefaultExecutor();
		executor.setWorkingDirectory(wrkDir);
		executor.setWatchdog(watchdog);
		executor.setStreamHandler(streamhandler);
		// FIXME issue #84 - we should be able to set expected process exit values

		return executor;
		/*
		 * we do not care about executor.addProcessDestroyer(..) shutdown hook for
		 * each task should be handled manually (we want to sent some info after
		 * successful termination to cluster)
		 */
	}

	/**
	 * Starts synchronous execution of the process.
	 * 
	 * @param executor
	 *          prepared executor to be started
	 * @return exit value of the process (if exit value is not expected, throws
	 *         {@link TaskException})
	 * @throws TaskException
	 *           when task process ended with error exit value or execution of
	 *           task process failed
	 */
	private int start(Executor executor) throws TaskException {
		try {
			int exitValue = executor.execute(cmd, environment);

			return exitValue;
		} catch (ExecuteException e) {
			if (killed) {
				throw new TaskException(String.format("Task has been killed with exit value %d", e.getExitValue()));
			}

			if (executor.isFailure(e.getExitValue()) && watchdog.killedProcess()) {
				throw new TaskException(String.format("Timeout (%d seconds) exceeded", timeoutInMillis / 1000));
			}

			if (executor.isFailure(e.getExitValue())) {
				throw new TaskException(String.format("Task process ended with error exit value %d", e.getExitValue()), e);
			}

			throw new TaskException(String.format("Execution of task process failed"), e);
		} catch (IOException e) {
			throw new TaskException("Execution of task process failed", e);
		} catch (Throwable t) {
			// should not happen, but one never knows :)
			throw new TaskException("Execution of task process failed from unknowh reason", t);
		}
	}

	/**
	 * Destroys the running process manually.
	 */
	public void kill() {
		this.killed = true;
		watchdog.destroyProcess();
	}

}
