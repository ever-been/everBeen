package cz.cuni.mff.d3s.been.hostruntime.task;

import java.io.File;

import cz.cuni.mff.d3s.been.bpk.BpkRuntime;
import cz.cuni.mff.d3s.been.bpk.JavaRuntime;
import cz.cuni.mff.d3s.been.bpk.NativeRuntime;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.hostruntime.TaskException;

/**
 * Utility class for creating command line builder for tasks based on runtime
 * type.
 * 
 * @author Martin Sixta
 * @author Tadeas Palusga
 */
public final class CmdLineBuilderFactory {

	/**
	 * Selects and creates correct {@link CmdLineBuilder} implementation based on
	 * runtime type.<br>
	 * <br>
	 * {@link JavaRuntime} -&gt; {@link JVMCmdLineBuilder}<br>
	 * {@link NativeRuntime} -&gt; {@link NativeCmdLineBuilder}
	 * 
	 * @param runtime
	 * @param taskDescriptor
	 * @param taskDir
	 *          task home directory
	 * 
	 * @return correct {@link CmdLineBuilder} implementation
	 * @throws TaskException
	 *           if {@link CmdLineBuilder} implementation for given runtime not
	 *           defined
	 */
	public static CmdLineBuilder create(BpkRuntime runtime,
			TaskDescriptor taskDescriptor, File taskDir) throws TaskException {
		if (runtime instanceof JavaRuntime) {
			return new JVMCmdLineBuilder(taskDir, taskDescriptor, ((JavaRuntime) runtime));
		} else if (runtime instanceof NativeRuntime) {
			return new NativeCmdLineBuilder(((NativeRuntime) runtime).getBinary(), taskDir, taskDescriptor);
		} else {
			String msg = String.format("Cannot create command line builder for unknown runtime: %s", runtime.getClass());
			throw new TaskException(msg);
		}
	}

}
