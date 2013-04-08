package cz.cuni.mff.d3s.been.hostruntime.cmdline;

import java.io.File;

import org.apache.commons.exec.CommandLine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import cz.cuni.mff.d3s.been.core.task.Arguments;
import cz.cuni.mff.d3s.been.core.task.Debug;
import cz.cuni.mff.d3s.been.core.task.Java;
import cz.cuni.mff.d3s.been.core.task.JavaOptions;
import cz.cuni.mff.d3s.been.core.task.ModeEnum;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;

public class JVMCommandLineBuilderTest extends Assert {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	private File taskDir;

	@Before
	public void setUp() throws Exception {
		taskDir = tmpFolder.newFolder();
	}

	@Test
	public void testCompleteCommandLineIsBuilt() throws Exception {
		TaskDescriptor td = new TaskDescriptor();
		td.setJava(createJavaWithOpts("opt1", "opt2"));
		td.setArguments(createArgs("arg1", "arg2"));
		td.setDebug(createDebug(ModeEnum.CONNECT, "host", 124));

		CommandLine cmdLine = new JVMCmdLineBuilder(taskDir, td).create();

		assertEquals(7, cmdLine.getArguments().length);

		assertEquals("java", cmdLine.getExecutable());
		assertEquals("-cp", cmdLine.getArguments()[0]);
		assertEquals(new File(taskDir, "lib/*").getAbsolutePath(), cmdLine.getArguments()[1]);
		assertEquals("opt1", cmdLine.getArguments()[2]);
		assertEquals("opt2", cmdLine.getArguments()[3]);
		assertEquals("-agentlib:jdwp=transport=dt_socket,server=n,address=host:124,suspend=n", cmdLine.getArguments()[4]);
		assertEquals("arg1", cmdLine.getArguments()[5]);
		assertEquals("arg2", cmdLine.getArguments()[6]);
	}

	@Test
	public void testJavaOptsInBuiltCommandLine() throws Exception {
		String opt1 = "opt1";
		String opt2 = "opt2";
		TaskDescriptor td = new TaskDescriptor();
		td.setJava(createJavaWithOpts(opt1, opt2));

		CommandLine cmdLine = new JVMCmdLineBuilder(taskDir, td).create();

		assertEquals(opt1, cmdLine.getArguments()[2]);
		assertEquals(opt2, cmdLine.getArguments()[3]);
	}

	@Test
	public void testArgsInBuiltCommandLine() throws Exception {
		String arg1 = "arg1";
		String arg2 = "arg2";
		TaskDescriptor td = new TaskDescriptor();
		td.setArguments(createArgs(arg1, arg2));

		CommandLine cmdLine = new JVMCmdLineBuilder(taskDir, td).create();

		assertEquals(arg1, cmdLine.getArguments()[2]);
		assertEquals(arg2, cmdLine.getArguments()[3]);
	}

	@Test
	public void testDebugParam_CONNECT_InBuiltCommandLine() throws Exception {
		ModeEnum mode = ModeEnum.CONNECT;
		String host = "host";
		int port = 12345;
		TaskDescriptor td = new TaskDescriptor();
		td.setDebug(createDebug(mode, host, port));

		TaskCommandLine cmdLine = new JVMCmdLineBuilder(taskDir, td).create();

		assertEquals("-agentlib:jdwp=transport=dt_socket,server=n,address=" + host + ":" + port + ",suspend=n", cmdLine.getArguments()[2]);
		assertFalse(cmdLine.isDebugListeningMode());
		assertEquals(0, cmdLine.getDebugPort());
	}

	@Test
	public void testDebugParam_LISTEN_InBuiltCommandLine() throws Exception {
		ModeEnum mode = ModeEnum.LISTEN;
		String host = "host";
		int port = 12345;
		TaskDescriptor td = new TaskDescriptor();
		td.setDebug(createDebug(mode, host, port));

		TaskCommandLine cmdLine = new JVMCmdLineBuilder(taskDir, td).create();

		assertEquals("-agentlib:jdwp=transport=dt_socket,server=n,address=" + port + ",suspend=n", cmdLine.getArguments()[2]);

		assertTrue(cmdLine.isDebugListeningMode());
		assertTrue(cmdLine.getDebugPort() > 0);
	}

	@Test
	public void testDebugParam_NONE_InBuiltCommandLine() throws Exception {
		ModeEnum mode = ModeEnum.NONE;
		String host = "host";
		int port = 12345;
		TaskDescriptor td = new TaskDescriptor();
		td.setDebug(createDebug(mode, host, port));

		TaskCommandLine cmdLine = new JVMCmdLineBuilder(taskDir, td).create();

		assertEquals(2, cmdLine.getArguments().length);
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
