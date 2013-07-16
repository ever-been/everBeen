package cz.cuni.mff.d3s.been.hostruntime.task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.*;
import org.apache.commons.io.FileUtils;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.hostruntime.TaskException;

/**
 * 
 * @author "Tadeas Palusga"
 * 
 */
public class TaskProcess implements AutoCloseable {

	/** magic constant which is used to set process timeout to infinite */
	public static final long NO_TIMEOUT = ExecuteWatchdog.INFINITE_TIMEOUT;

	/** process working directory */
	private final Path wrkDir;

	/** prepared command line for the process */
	private final TaskCommandLine cmd;

	/** environment variables set for the process */
	private final Map<String, String> environment;

	/** std/err output stream handlers for the process */
	private final ExecuteStreamHandler streamHandler;

	/** process timeout in seconds */
	private long timeoutInMillis;

	/** long time run watchdog. */
	private ExecuteWatchdog watchdog;

	/** all identifiers of Bpks needed by the process. */
	private final Collection<BpkIdentifier> bkpDependencies;

	/** All identifiers of Artifacts needed by the process. */
	private final Collection<ArtifactIdentifier> artifactDependencies;

	/** tells if manual shutdown has been requested */
	private boolean killed;

	/**
	 * Creates new task process.
	 * 
	 * @param cmdLineBuilder
	 *          process command line builder
	 * @param wrkDir
	 *          working directory of the process
	 * @param environment
	 *          environment variables for process to be set
	 * @param artifactDownloader
	 */
	public TaskProcess(CmdLineBuilder cmdLineBuilder, Path wrkDir, Map<String, String> environment, ExecuteStreamHandler streamHandler, DependencyDownloader artifactDownloader)
			throws TaskException {
		this.artifactDependencies = artifactDownloader.getArtifactDependencies();
		this.bkpDependencies = artifactDownloader.getBkpDependencies();
		this.cmd = cmdLineBuilder.build();
		this.wrkDir = wrkDir;
		if (!wrkDir.toFile().exists()) {
			wrkDir.toFile().mkdirs();
		}
		this.environment = environment;
		this.streamHandler = streamHandler;
		this.watchdog = new ExecuteWatchdog(NO_TIMEOUT);
	}

	/**
	 * Starts process using Apache {@link Executor}.
	 * 
	 * @return process exit value (throws {@link TaskException}
	 *         //To change body of implemented methods use File | Settings | File
	 *         Templates. on error exit values)
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
		executor.setWorkingDirectory(wrkDir.toFile());
		executor.setWatchdog(watchdog);
		executor.setStreamHandler(streamHandler);
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

			return executor.execute(cmd, environment);

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
			throw new TaskException("Execution of task process failed for unknown reason", t);
		}
	}

	/**
	 * Destroys the running process manually.
	 */
	public void kill() {
		this.killed = true;
		watchdog.destroyProcess();
	}

	@Override
	public void close() throws Exception {
		if (!this.killed) {
			watchdog.destroyProcess();
		}
	}

	public boolean isDebugListeningMode() {
		return cmd.isDebugListeningMode();
	}

	public int getDebugPort() {
		return cmd.getDebugPort();
	}

	/**
	 * Whether the process is started suspended.
	 * 
	 * This is currently supported only for JVM based processes.
	 * 
	 * @return whether the process is started suspended
	 */
	public boolean isSuspended() {
		return cmd.isSuspended();
	}

	/**
	 * 
	 * Sets timeout for the process.
	 * 
	 * @param timeout
	 *          timeout in seconds
	 */
	public void setTimeout(long timeout) {
		timeoutInMillis = timeout <= 0 ? NO_TIMEOUT
				: TimeUnit.SECONDS.toMillis(timeout);
		this.watchdog = new ExecuteWatchdog(timeoutInMillis);
	}

	/**
	 * Returns task arguments including executable name.
	 * 
	 * @return task arguments including executable name
	 */
	public List<String> getArgs() {
		return Arrays.asList(cmd.toStrings());

	}

	/**
	 * Returns task's working directory.
	 * 
	 * @return task's working directory
	 */
	public String getWorkingDirectory() {
		return wrkDir.toAbsolutePath().toString();
	}

	/**
	 * Returns all BPK dependencies of the process
	 * 
	 * @return BPK dependencies of the process
	 */
	public Collection<BpkIdentifier> getBkpDependencies() {
		return bkpDependencies;
	}

	/**
	 * Returns all Artifact dependencies of the process
	 * 
	 * @return Artifact dependencies of the process
	 */
	public Collection<ArtifactIdentifier> getArtifactDependencies() {
		return artifactDependencies;
	}

	/**
	 * Returns environment properties of the process
	 * 
	 * @return environment properties of the process
	 */
	public Map<String, String> getEnvironmentProperties() {
		return this.environment;
	}
}
