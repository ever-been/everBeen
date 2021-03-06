package cz.cuni.mff.d3s.been.hostruntime.task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.tools.*;

import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import cz.cuni.mff.d3s.been.debugassistant.DebugAssistant;
import cz.cuni.mff.d3s.been.hostruntime.TaskException;

public class TaskProcessTest extends Assert {

	@Mock
	private DebugAssistant debugAssistant;

	@Mock
	private DependencyDownloader dependencyDownloader;

	@Mock
	private CmdLineBuilder cmdLineBuilder;

	private String sourceWithTimeoutAsFirstArg = //
	"public class Main {" + //
	"	public static void main(String[] args) throws InterruptedException {" + //
	"		Thread.sleep(new Integer(args[0]));" + //
	"	}" + //
	"}";

	private String sourceWithStdOutWrite = //
	"public class Main {" + //
	"	public static void main(String[] args) throws InterruptedException {" + //
	"		System.out.println(\"STD OUT WRITE EXAMPLE\");" + //
	"	}" + //
	"}";

	private String sourceWithStdErrWrite = //
	"public class Main {" + //
	"	public static void main(String[] args) throws InterruptedException {" + //
	"		System.err.println(\"STD ERR WRITE EXAMPLE\");" + //
	"	}" + //
	"}";

	private String sourceDoingNothing = //
	"public class Main {" + //
	"	public static void main(String[] args) throws InterruptedException {" + //
	"	}" + //
	"}";

	private String sourceWithBadExitCode = //
	"public class Main {" + //
	"	public static void main(String[] args) throws InterruptedException {" + //
	"		System.exit(123);" + //
	"	}" + //
	"}";

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	private File workingDirectory;

	private Path wrkDirPath;

	@Mock
	private OutputStream fakeStdOutOutputStream;

	@Mock
	private OutputStream fakeStdErrOutputStream;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		workingDirectory = tmpFolder.getRoot();
		wrkDirPath = workingDirectory.toPath();
	}

	@Test(expected = TaskException.class)
	public void testExceptionIsThrownWhenProcessEndsWithErrorExitCode() throws Exception {
		setUpCmdLineBuilder(sourceWithBadExitCode);
		Map<String, String> environment = new HashMap<>();
		TaskProcess process = new TaskProcess(cmdLineBuilder, workingDirectory.toPath(), environment, fakeStdOutOutputStream, fakeStdErrOutputStream, dependencyDownloader);

		process.start();
	}

	@Test(expected = TaskException.class)
	public void testExceptionIsThrownWhenInvalidCommandLineProvided() throws Exception {
		Mockito.when(cmdLineBuilder.build()).thenReturn(new TaskCommandLine("q w e r t y"));
		Map<String, String> environment = new HashMap<>();
		final TaskProcess process = new TaskProcess(cmdLineBuilder, wrkDirPath, environment, fakeStdOutOutputStream, fakeStdErrOutputStream, dependencyDownloader);

		process.start();
	}

	@Test(expected = TaskException.class)
	public void testExceptionIsThrownOnTimeoutExceeded() throws Exception {
		setUpCmdLineBuilderWithExecTime(sourceWithTimeoutAsFirstArg, 10);
		Map<String, String> environment = new HashMap<>();
		TaskProcess process = new TaskProcess(cmdLineBuilder, wrkDirPath, environment, fakeStdOutOutputStream, fakeStdErrOutputStream, dependencyDownloader);
		process.setTimeout(1);

		process.start();
	}

	@Test
	public void testCorrectExitCodeIsReturned() throws Exception {
		setUpCmdLineBuilder(sourceDoingNothing);
		Map<String, String> environment = new HashMap<>();
		final TaskProcess process = new TaskProcess(cmdLineBuilder, wrkDirPath, environment, fakeStdOutOutputStream, fakeStdErrOutputStream, dependencyDownloader);

		assertEquals(0, process.start());
	}

	@Test
	public void testTaskSuccessfullyFinishedBeforeTimeout() throws Exception {
		setUpCmdLineBuilderWithExecTime(sourceWithTimeoutAsFirstArg, 0);
		Map<String, String> environment = new HashMap<>();
		TaskProcess process = new TaskProcess(cmdLineBuilder, wrkDirPath, environment, fakeStdOutOutputStream, fakeStdErrOutputStream, dependencyDownloader);
		process.setTimeout(2);

		process.start();
	}

	@Test
	public void testTaskHandlesStdOutput() throws Exception {
		setUpCmdLineBuilderWithExecTime(sourceWithStdOutWrite, 0);
		Map<String, String> environment = new HashMap<>();
		ByteArrayOutputStream fakeStdOutOutputStream = new ByteArrayOutputStream();
		TaskProcess process = new TaskProcess(cmdLineBuilder, wrkDirPath, environment, fakeStdOutOutputStream, fakeStdErrOutputStream, dependencyDownloader);
		process.setTimeout(2);

		process.start();
		assertEquals("STD OUT WRITE EXAMPLE\n", new String(fakeStdOutOutputStream.toByteArray()));
	}

	@Test
	public void testTaskHandlesErrOutput() throws Exception {
		setUpCmdLineBuilderWithExecTime(sourceWithStdErrWrite, 0);
		Map<String, String> environment = new HashMap<>();
		ByteArrayOutputStream fakeStdErrOutputStream = new ByteArrayOutputStream();
		TaskProcess process = new TaskProcess(cmdLineBuilder, wrkDirPath, environment, fakeStdOutOutputStream, fakeStdErrOutputStream, dependencyDownloader);
		process.setTimeout(2);

		process.start();
		assertEquals("STD ERR WRITE EXAMPLE\n", new String(fakeStdErrOutputStream.toByteArray()));
	}

	@Test(timeout = 15000)
	public void testProcessIsCorrectlyKilled() throws Exception {
		setUpCmdLineBuilderWithExecTime(sourceWithTimeoutAsFirstArg, 10000);
		Map<String, String> environment = new HashMap<>();
		ExecuteStreamHandler streamhandler = new PumpStreamHandler();
		final TaskProcess process = new TaskProcess(cmdLineBuilder, wrkDirPath, environment, fakeStdOutOutputStream, fakeStdErrOutputStream, dependencyDownloader);

		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					process.start();
					fail("expected task has been killed exception");
				} catch (TaskException e) {
					assertTrue(e.getMessage().contains("Task has been killed"));
				}
			}
		};
		t.start();
		Thread.sleep(200);
		process.kill();
		t.join();
	}

	///////////////////////////
	//
	// END OF TEST METHODS
	//
	///////

	private void setUpCmdLineBuilder(String source) throws Exception {
		TaskCommandLine cmd = cmdLine(source);
		Mockito.when(cmdLineBuilder.build()).thenReturn(cmd);
	}

	private void setUpCmdLineBuilderWithExecTime(String source, int execTime) throws Exception {
		TaskCommandLine cmd = cmdLineWithExecutionTime(source, execTime);
		Mockito.when(cmdLineBuilder.build()).thenReturn(cmd);
	}

	/**
	 * Creates command line for process which will sleep given amount of seconds.
	 * I used this ugly hack because all tests should be runnable on all operating
	 * systems and java is ideal candidate.
	 */
	private TaskCommandLine cmdLine(String source) throws Exception {
		String className = "Main";
		compile(source, className);
		TaskCommandLine commandLine = new TaskCommandLine("java");
		commandLine.addArgument("-Xms2m");
		commandLine.addArgument("-Xmx4m");
		commandLine.addArgument(className);
		return commandLine;
	}

	/**
	 * Creates command line for process which will sleep given amount of seconds.
	 * I used this ugly hack because all tests should be runnable on all operating
	 * systems and java is ideal candidate.
	 */
	private TaskCommandLine cmdLineWithExecutionTime(String source, int execSeconds) throws Exception {
		TaskCommandLine commandLine = cmdLine(source);
		commandLine.addArgument("" + execSeconds * 1000);
		return commandLine;
	}

	/**
	 * compiles java source to X.java file where X is className given as
	 * parameter.
	 * 
	 * @param source
	 *          source
	 * @param className
	 *          class name
	 * @throws Exception
	 */
	private void compile(String source, String className) throws Exception {
		File javaFile = new File(workingDirectory, className + ".java");
		FileUtils.writeStringToFile(javaFile, source);

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(Arrays.asList(javaFile.getAbsolutePath()));
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
		assertTrue("class " + source + " not compiled - check syntax in test definition", task.call());
		fileManager.close();
	}
}
