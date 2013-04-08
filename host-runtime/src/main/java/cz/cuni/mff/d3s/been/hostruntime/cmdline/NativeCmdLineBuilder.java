package cz.cuni.mff.d3s.been.hostruntime.cmdline;

import java.io.File;

import org.apache.commons.exec.CommandLine;

import cz.cuni.mff.d3s.been.bpk.BpkNames;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;

/**
 * Command line builder for native tasks.
 * 
 * @author Tadeas Palusga
 * 
 */
class NativeCmdLineBuilder implements CmdLineBuilder {

	/** file name of the executable file relative to task home directory */
	private final String executableFileName;

	/** underlying task descriptor from which the command line should be built */
	private final TaskDescriptor taskDescriptor;

	/** task home directory */
	private final File taskDir;

	/**
	 * @param executableFileName
	 *          name of the executable file relative to task home directory
	 *          (parameter taskDir)
	 * @param taskDescriptor
	 * @param taskDir
	 */
	public NativeCmdLineBuilder(String executableFileName, File taskDir, TaskDescriptor taskDescriptor) {
		this.executableFileName = executableFileName;
		this.taskDescriptor = taskDescriptor;
		this.taskDir = taskDir;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaskCommandLine create() {
		File executable = taskDir.toPath().resolve(BpkNames.FILES_DIR).resolve(executableFileName).toFile();
		executable.setExecutable(true);
		TaskCommandLine cmdLine = new TaskCommandLine(executable);

		addArgsFromTaskDescriptor(cmdLine);

		return cmdLine;
	}
	/**
	 * Searches for program arguments in task descriptor and appends these
	 * arguments to given {@link CommandLine}
	 * 
	 * @param cmdLine
	 *          command line to which the generated argument should be added
	 */
	private void addArgsFromTaskDescriptor(CommandLine cmdLine) {
		boolean hasArguments = taskDescriptor.isSetArguments();
		if (hasArguments) {
			for (String taskOpt : taskDescriptor.getArguments().getArgument()) {
				cmdLine.addArgument(taskOpt);
			}
		}
	}

}
