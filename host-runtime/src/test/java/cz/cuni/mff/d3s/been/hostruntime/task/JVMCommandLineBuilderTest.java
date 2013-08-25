package cz.cuni.mff.d3s.been.hostruntime.task;

import static cz.cuni.mff.d3s.been.hostruntime.task.JVMCmdLineBuilder.TASK_RUNNER_CLASS;

import java.io.File;

import org.apache.commons.exec.CommandLine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import cz.cuni.mff.d3s.been.bpk.BpkNames;
import cz.cuni.mff.d3s.been.bpk.JavaRuntime;
import cz.cuni.mff.d3s.been.core.task.*;

public class JVMCommandLineBuilderTest extends Assert {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	private JavaRuntime runtime;

	private File taskDir;

	@Before
	public void setUp() throws Exception {
		taskDir = tmpFolder.newFolder();
		runtime = new JavaRuntime();
	}

	@Test
	public void testCompleteCommandLineIsBuilt() throws Exception {
		TaskDescriptor td = new TaskDescriptor();
		td.setJava(createJavaWithOpts("opt1", "opt2"));
		td.setArguments(createArgs("arg1", "arg2"));
		td.setDebug(createDebug(ModeEnum.CONNECT, "host", 124));

		JavaRuntime runtime = new JavaRuntime();
		runtime.setJarFile("jarfile");

		CommandLine cmdLine = new JVMCmdLineBuilder(taskDir, td, runtime).build();

		assertEquals(8, cmdLine.getArguments().length);

		assertEquals("java", cmdLine.getExecutable());
		assertEquals("-cp", cmdLine.getArguments()[0]);
		String filesCPart = new File(taskDir, BpkNames.FILES_DIR + File.separator + "*").getAbsolutePath();
		String libsCPart = new File(taskDir, BpkNames.LIB_DIR + File.separator + "*").getAbsolutePath();
		assertEquals(filesCPart + File.pathSeparator + libsCPart, cmdLine.getArguments()[1]);
		assertEquals("opt1", cmdLine.getArguments()[2]);
		assertEquals("opt2", cmdLine.getArguments()[3]);
		assertEquals("-agentlib:jdwp=transport=dt_socket,server=n,address=host:124,suspend=n", cmdLine.getArguments()[4]);
		assertEquals(TASK_RUNNER_CLASS, cmdLine.getArguments()[5]);
		assertEquals("arg1", cmdLine.getArguments()[6]);
		assertEquals("arg2", cmdLine.getArguments()[7]);
	}

	@Test
	public void testJavaOptsInBuiltCommandLine() throws Exception {
		String opt1 = "opt1";
		String opt2 = "opt2";
		TaskDescriptor td = new TaskDescriptor();
		td.setJava(createJavaWithOpts(opt1, opt2));
		CommandLine cmdLine = new JVMCmdLineBuilder(taskDir, td, runtime).build();

		assertEquals(opt1, cmdLine.getArguments()[2]);
		assertEquals(opt2, cmdLine.getArguments()[3]);
	}

	@Test
	public void testArgsInBuiltCommandLine() throws Exception {
		String arg1 = "arg1";
		String arg2 = "arg2";
		TaskDescriptor td = new TaskDescriptor();
		td.setArguments(createArgs(arg1, arg2));
		td.setJava(new Java());
		td.getJava().setMainClass("mainClass");
		CommandLine cmdLine = new JVMCmdLineBuilder(taskDir, td, runtime).build();

		assertEquals(TASK_RUNNER_CLASS, cmdLine.getArguments()[2]);
		assertEquals("mainClass", cmdLine.getArguments()[3]);
		assertEquals(arg1, cmdLine.getArguments()[4]);
		assertEquals(arg2, cmdLine.getArguments()[5]);
	}

	@Test
	public void testDebugParam_CONNECT_InBuiltCommandLine() throws Exception {
		ModeEnum mode = ModeEnum.CONNECT;
		String host = "host";
		int port = 12345;
		TaskDescriptor td = new TaskDescriptor();
		td.setJava(new Java());
		td.getJava().setMainClass("mainClass");
		td.setDebug(createDebug(mode, host, port));

		TaskCommandLine cmdLine = new JVMCmdLineBuilder(taskDir, td, runtime).build();

		assertEquals(
				"-agentlib:jdwp=transport=dt_socket,server=n,address=" + host + ":" + port + ",suspend=n",
				cmdLine.getArguments()[2]);
		assertFalse(cmdLine.isDebugListeningMode());
		assertEquals(0, cmdLine.getDebugPort());
	}

	@Test
	public void testDebugParam_LISTEN_InBuiltCommandLine() throws Exception {
		ModeEnum mode = ModeEnum.LISTEN;
		String host = "host";
		int port = 12345;
		TaskDescriptor td = new TaskDescriptor();
		td.setJava(new Java());
		td.getJava().setMainClass("mainClass");
		td.setDebug(createDebug(mode, host, port));

		TaskCommandLine cmdLine = new JVMCmdLineBuilder(taskDir, td, runtime).build();

		assertEquals(
				"-agentlib:jdwp=transport=dt_socket,server=y,address=" + port + ",suspend=n",
				cmdLine.getArguments()[2]);

		assertTrue(cmdLine.isDebugListeningMode());
		assertTrue(cmdLine.getDebugPort() > 0);
	}

	@Test
	public void testDebugParam_NONE_InBuiltCommandLine() throws Exception {
		ModeEnum mode = ModeEnum.NONE;
		String host = "host";
		int port = 12345;
		TaskDescriptor td = new TaskDescriptor();
		td.setJava(new Java());
		td.getJava().setMainClass("mainClass");
		td.setDebug(createDebug(mode, host, port));

		TaskCommandLine cmdLine = new JVMCmdLineBuilder(taskDir, td, runtime).build();

		assertEquals(4, cmdLine.getArguments().length);
		assertFalse(cmdLine.isDebugListeningMode());
		assertEquals(0, cmdLine.getDebugPort());
	}

	private Debug createDebug(ModeEnum mode, String host, int port) {
		Debug debug = new Debug();
		debug.setHost(host);
		debug.setPort(port);
		debug.setMode(mode);
		return debug;
	}

	private Java createJavaWithOpts(String opt1, String opt2) {
		Java java = new Java();
		JavaOptions opts = new JavaOptions();
		opts.getJavaOption().add(opt1);
		opts.getJavaOption().add(opt2);
		java.setJavaOptions(opts);
		return java;
	}

	private Arguments createArgs(String arg1, String arg2) {
		Arguments args = new Arguments();
		args.getArgument().add(arg1);
		args.getArgument().add(arg2);
		return args;
	}

}
