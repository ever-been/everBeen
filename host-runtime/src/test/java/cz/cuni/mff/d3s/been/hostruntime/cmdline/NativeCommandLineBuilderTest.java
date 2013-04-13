package cz.cuni.mff.d3s.been.hostruntime.cmdline;

import java.io.File;

import org.apache.commons.exec.CommandLine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import cz.cuni.mff.d3s.been.bpk.NativeRuntime;
import cz.cuni.mff.d3s.been.core.task.Arguments;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;

public class NativeCommandLineBuilderTest extends Assert {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	private File taskDir;

	@Before
	public void setUp() throws Exception {
		taskDir = tmpFolder.newFolder();
	}

	@Test
	public void testCompleteCommandLineIsBuilt() throws Exception {
		NativeRuntime runtime = new NativeRuntime();
		runtime.setBinary("binary");
		TaskDescriptor td = new TaskDescriptor();
		td.setArguments(createArgs("arg1", "arg2"));

		String executable = "executable";
		CommandLine cmdLine = new NativeCmdLineBuilder(executable, taskDir, td).build();

		assertEquals(2, cmdLine.getArguments().length);
		assertEquals(new File(taskDir, "files/executable").getAbsolutePath(), cmdLine.getExecutable());
		assertEquals("arg1", cmdLine.getArguments()[0]);
		assertEquals("arg2", cmdLine.getArguments()[1]);
	}

	@Test
	public void testExecutableFlagSetOnExecutableFile() throws Exception {
		NativeRuntime runtime = new NativeRuntime();
		runtime.setBinary("binary");
		TaskDescriptor td = new TaskDescriptor();

		File filesDir = new File(taskDir, "files");
		filesDir.mkdir();
		File executable = new File(filesDir, "executable");
		executable.createNewFile();
		assertFalse(executable.canExecute());

		new NativeCmdLineBuilder(executable.getName(), taskDir, td).build();

		assertTrue(executable.canExecute());
	}

	private Arguments createArgs(String arg1, String arg2) {
		Arguments args = new Arguments();
		args.getArgument().add(arg1);
		args.getArgument().add(arg2);
		return args;
	}

}
