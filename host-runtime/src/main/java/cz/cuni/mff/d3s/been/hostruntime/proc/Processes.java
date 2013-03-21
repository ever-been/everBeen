package cz.cuni.mff.d3s.been.hostruntime.proc;

import java.nio.file.Path;

import cz.cuni.mff.d3s.been.bpk.BpkRuntime;
import cz.cuni.mff.d3s.been.bpk.JavaRuntime;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.hostruntime.TaskException;

/**
 * Utility class for creating TaskProcesses (Host Runtime abstraction of task's
 * process.
 * 
 * @author Martin Sixta
 */
public final class Processes {
	public static TaskProcess createProcess(BpkRuntime runtime,
			TaskDescriptor td, Path taskDir) throws TaskException {
		if (runtime instanceof JavaRuntime) {
			return new JavaBasedProcess((JavaRuntime) runtime, td, taskDir);
		} else {
			String msg = String.format("Cannot create process for unknown runtime: %s", runtime.getClass());
			throw new TaskException(msg);
		}
	}
}
