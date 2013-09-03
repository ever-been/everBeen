package cz.cuni.mff.d3s.been.hostruntime.task;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.bpk.BpkNames;
import cz.cuni.mff.d3s.been.bpk.JavaRuntime;
import cz.cuni.mff.d3s.been.core.task.Java;
import cz.cuni.mff.d3s.been.core.task.ModeEnum;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.hostruntime.TaskException;

/**
 * Command line builder for JVM based tasks.
 * 
 * @author Tadeas Palusga
 * @author Martin Sixta
 * @author Kuba Brecka
 * 
 */
class JVMCmdLineBuilder implements CmdLineBuilder {

	private static final String CP_WILDCARD = "*";

	private static final Logger log = LoggerFactory.getLogger(JVMCmdLineBuilder.class);

	/** Name of Java's executable. This is overkill, isn't it? */
	private static final String JAVA_EXECUTABLE = "java";

	/** Name of Java's classpath argument. See {@link #JAVA_EXECUTABLE}. */
	private static final String JAVA_CLASSPATH_ARG = "-cp";

	/** Name of the java Task Runner */
	static final String TASK_RUNNER_CLASS = "cz.cuni.mff.d3s.been.taskapi.TaskRunner";

	/**
	 * Java debug parameter template of Java's classpath argument. See
	 * {@link #JAVA_EXECUTABLE}. See <a
	 * href="http://docs.oracle.com/javase/1.5.0/docs/guide/jpda/conninv.html"
	 * >Oracle documentation</a> <br/>
	 * <br>
	 * <br>
	 * There are 3 string placeholders in the template. <br>
	 * <br>
	 * <b>1. - 'server' (official documentation follows)</b><br>
	 * <i>Default: "n"</i>
	 * <p>
	 * If "y", listen for a debugger application to attach; otherwise, attach to
	 * the debugger application at the specified address. * If "y" and no address
	 * is specified, choose a transport address at which to listen for a debugger
	 * application, and print the address to the standard output stream.
	 * </p>
	 * 
	 * <b>2. - 'address' (official documentation follows)</b><br>
	 * <p>
	 * Transport address for the connection. If server=n, attempt to attach to
	 * debugger application at this address. If server=y, listen for a connection
	 * at this address.
	 * </p>
	 * <b>3. - 'suspend' (official documentation follows)</b><br>
	 * <i>Default: "y"</i>
	 * <p>
	 * If "y", VMStartEvent has a suspendPolicy of SUSPEND_ALL. If "n",
	 * VMStartEvent has a suspendPolicy of SUSPEND_NONE.
	 * </p>
	 */
	private static final String JAVA_DEBUG_ARG_TEMPLATE = "-agentlib:jdwp=transport=dt_socket,server=%s,address=%s,suspend=%s";

	/** task library directory */
	private final File libDir;

	private File fileDir;

	/** underlying task descriptor */
	private final TaskDescriptor taskDescriptor;

	/** underlying java runtime */
	private JavaRuntime runtime;

	/**
	 * @param taskDir
	 *          task home directory - from this directory is determined library
	 *          directory ({@link BpkNames#LIB_DIR})
	 * @param taskDescriptor
	 *          associated TaskDescriptor
	 * @param runtime
	 *          Java runtime definition
	 */
	public JVMCmdLineBuilder(File taskDir, TaskDescriptor taskDescriptor, JavaRuntime runtime) {
		this.taskDescriptor = taskDescriptor;
		this.runtime = runtime;
		this.libDir = new File(taskDir, BpkNames.LIB_DIR);
		this.fileDir = new File(taskDir, BpkNames.FILES_DIR);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TaskCommandLine build() throws TaskException {
		TaskCommandLine cmdLine = new TaskCommandLine(JAVA_EXECUTABLE);

		addClassPath(cmdLine);
		addJavaOptsFromTaskDescriptor(cmdLine);
		addDebugParameters(cmdLine);
		addMainClass(cmdLine);
		addArgsFromTaskDescriptor(cmdLine);

		return cmdLine;
	}

	private void addMainClass(TaskCommandLine cmdLine) throws TaskException {
		Java java = taskDescriptor.getJava();

		if (useTaskRunner(java)) {
			cmdLine.addArgument(TASK_RUNNER_CLASS);
		}

		String finalMainClass = java.getMainClass();
		cmdLine.addArgument(finalMainClass);

	}

	private boolean useTaskRunner(Java java) {
		boolean isValueSpecified = java != null && java.isSetUseTaskRunner();

		if (isValueSpecified) {
			return java.isUseTaskRunner();
		} else {
			return true; // default to true
		}

	}

	/**
	 * Generates classpath value argument. Joins all absolute paths of files in
	 * library directory ({@link JVMCmdLineBuilder#libDir}) to single string.
	 * (Path join is platform independent - it means that ':' is used as the path
	 * delimiter on Microsoft OS and ':' on UNIX/Linux OS)
	 * 
	 * @param cmdLine
	 *          command line to which the generated argument should be added
	 */
	private void addClassPath(TaskCommandLine cmdLine) {
		String filesClasspath = fileDir.toPath().toString() + File.separator + CP_WILDCARD;
		String libClasspath = libDir.toPath().toString() + File.separator + CP_WILDCARD;
		cmdLine.addArgument(JAVA_CLASSPATH_ARG).addArgument(concat(filesClasspath, libClasspath));
	}

	private String concat(String... paths) {
		return StringUtils.toString(paths, File.pathSeparator);
	}

	/**
	 * Searches for java options in task descriptor and appends these options to
	 * given {@link CommandLine}
	 * 
	 * @param cmdLine
	 *          command line to which the options should be appended
	 */
	void addJavaOptsFromTaskDescriptor(TaskCommandLine cmdLine) {
		boolean javaElementDefined = taskDescriptor.isSetJava();
		if (javaElementDefined) {
			boolean javaOptionsDefined = taskDescriptor.getJava().isSetJavaOptions();
			if (javaOptionsDefined) {
				for (String option : taskDescriptor.getJava().getJavaOptions().getJavaOption()) {
					cmdLine.addArgument(option);
				}
			}
		}
	}

	/**
	 * Searches for debug options in task descriptor. If debug is defined and
	 * debug mode is [{@link ModeEnum#CONNECT} or {@link ModeEnum#LISTEN} ] then
	 * the debug argument is generated from defined template (see
	 * {@link JVMCmdLineBuilder#JAVA_DEBUG_ARG_TEMPLATE} for detailed informations
	 * about debug argument.)
	 * 
	 * @param cmdLine
	 *          Task's command line
	 * @throws TaskException
	 */
	private void addDebugParameters(TaskCommandLine cmdLine) throws TaskException {
		if (isDebugSectionDefinedInTaskDescriptor()) {
			switch (taskDescriptor.getDebug().getMode()) {
				case CONNECT: {
					String host = taskDescriptor.getDebug().getHost();
					int port = taskDescriptor.getDebug().getPort();
					cmdLine.suspended = taskDescriptor.getDebug().isSuspend();
					cmdLine.addArgument(createDebugParam(false, host + ":" + port, cmdLine.suspended));
					break;
				}
				case LISTEN: {
					int port = taskDescriptor.getDebug().getPort();
					if (port == 0) {
						port = detectRandomPort();
					}
					cmdLine.debugPort = port;
					cmdLine.debugListeningMode = true;
					cmdLine.suspended = taskDescriptor.getDebug().isSuspend();
					cmdLine.addArgument(createDebugParam(true, "" + port, cmdLine.suspended));
					log.info("Debugged process is listening on port {}", port);
					break;
				}
				case NONE:
					return;
				default:
					return;
			}
		}
	}

	/**
	 * Searches for program arguments in task descriptor and appends these
	 * arguments to given {@link CommandLine}
	 * 
	 * @param cmdLine
	 *          command line to which the generated argument should be added
	 */
	private void addArgsFromTaskDescriptor(TaskCommandLine cmdLine) {
		boolean hasArguments = taskDescriptor.isSetArguments();
		if (hasArguments) {
			for (String taskOpt : taskDescriptor.getArguments().getArgument()) {
				cmdLine.addArgument(taskOpt);
			}
		}
	}

	/**
	 * Detects <b>random available</b> port on localhost.
	 * 
	 * @return detected port
	 * @throws TaskException
	 *           in port cannot be detected from some reaosn
	 */
	private int detectRandomPort() throws TaskException {
		try (ServerSocket ss = new ServerSocket(0)) {
			return ss.getLocalPort();
		} catch (IOException e) {
			throw new TaskException("Cannot detect random port on localhost for debugging", e);
		}
	}

	/**
	 * Creates transport debug parameter. For detailed parameter description see
	 * {@link JVMCmdLineBuilder#JAVA_DEBUG_ARG_TEMPLATE}
	 * 
	 * @param server
	 *          listen for a debugger application to attach
	 * @param address
	 *          transport address for the connection
	 * @param suspend
	 *          if <i>true</i> ... suspend policy = SUSPEND_ALL. If <i>false</i>
	 *          ... suspend policy = SUSPEND_NONE
	 * @return created parameter
	 */
	private String createDebugParam(boolean server, String address, boolean suspend) {
		return String.format(JAVA_DEBUG_ARG_TEMPLATE, (server ? "y" : "n"), address, (suspend ? "y" : "n"));
	}

	/**
	 * Tells if debugging section is defined (beware: defined != enabled)
	 * 
	 * @return whether the debug section is defined
	 */
	private boolean isDebugSectionDefinedInTaskDescriptor() {
		return taskDescriptor.isSetDebug();
	}

}
