package cz.cuni.mff.d3s.been.hostruntime.task;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import cz.cuni.mff.d3s.been.hostruntime.TaskException;

public class TaskProcessTest extends Assert {

	private String sourceWithTimeoutAsFirstArg = //
			"public class Main {" + //
			"	public static void main(String[] args) throws InterruptedException {" + //
			"		Thread.sleep(new Integer(args[0]));" + //
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

	@Before
	public void setUp() throws Exception {
		workingDirectory = tmpFolder.getRoot();
	}

	@Test(expected = TaskException.class)
	public void testExceptionIsThrownWhenProcessEndsWithErrorExitCode() throws Exception {
		CommandLine cmd = cmdLine(sourceWithBadExitCode);
		Map<String, String> environment = new HashMap<>();
		ExecuteStreamHandler streamhandler = new PumpStreamHandler();
		long processTimeout = TaskProcess.NO_TIMEOUT;
		TaskProcess process = new TaskProcess(cmd, workingDirectory, environment, streamhandler, processTimeout);

		process.start();
	}

	@Test(expected = TaskException.class)
	public void testExceptionIsThrownWhenInvalidCommandLineProvided() throws Exception {
		CommandLine cmd = new CommandLine("q w e r t y u i");
		Map<String, String> environment = new HashMap<>();
		ExecuteStreamHandler streamhandler = new PumpStreamHandler();
		long processTimeout = TaskProcess.NO_TIMEOUT;
		final TaskProcess process = new TaskProcess(cmd, workingDirectory, environment, streamhandler, processTimeout);

		process.start();
	}

	@Test(expected = TaskException.class)
	public void testExceptionIsThrownOnTimeoutExceeded() throws Exception {
		CommandLine cmd = cmdLineWithExecutionTime(sourceWithTimeoutAsFirstArg, 10);
		Map<String, String> environment = new HashMap<>();
		ExecuteStreamHandler streamhandler = new PumpStreamHandler();
		long processTimeout = 1;
		TaskProcess process = new TaskProcess(cmd, workingDirectory, environment, streamhandler, processTimeout);

		process.start();
	}

	@Test
	public void testCorrectExitCodeIsReturned() throws Exception {
		CommandLine cmd = cmdLine(sourceDoingNothing);
		Map<String, String> environment = new HashMap<>();
		ExecuteStreamHandler streamhandler = new PumpStreamHandler();
		long processTimeout = TaskProcess.NO_TIMEOUT;
		final TaskProcess process = new TaskProcess(cmd, workingDirectory, environment, streamhandler, processTimeout);

		assertEquals(0, process.start());
	}

	@Test
	public void testTaskSuccessfullyFinishedBeforeTimeout() throws Exception {
		CommandLine cmd = cmdLineWithExecutionTime(sourceWithTimeoutAsFirstArg, 0);
		Map<String, String> environment = new HashMap<>();
		ExecuteStreamHandler streamhandler = new PumpStreamHandler();
		long processTimeout = 2;
		TaskProcess process = new TaskProcess(cmd, workingDirectory, environment, streamhandler, processTimeout);

		process.start();
	}

	@Test(timeout = 5000)
	public void testProcessIsCorrectlyKilled() throws Exception {
		CommandLine cmd = cmdLineWithExecutionTime(sourceWithTimeoutAsFirstArg, 100000);
		Map<String, String> environment = new HashMap<>();
		ExecuteStreamHandler streamhandler = new PumpStreamHandler();
		long processTimeout = TaskProcess.NO_TIMEOUT;
		final TaskProcess process = new TaskProcess(cmd, workingDirectory, environment, streamhandler, processTimeout);

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

	/**
	 * Creates command line for process which will sleep given amount of seconds.
	 * I used this ugly hack because all tests should be runnable on all operating
	 * systems and java is ideal candidate.
	 */
	private CommandLine cmdLine(String source) throws Exception {
		String className = "Main";
		compile(source, className);
		return new CommandLine("java").addArgument(className);
	}

	/**
	 * Creates command line for process which will sleep given amount of seconds.
	 * I used this ugly hack because all tests should be runnable on all operating
	 * systems and java is ideal candidate.
	 */
	private CommandLine cmdLineWithExecutionTime(String source, int execSeconds) throws Exception {
		return cmdLine(source).addArgument("" + execSeconds * 1000);
	}

	/**
	 * compiles java source to X.java file where X is className given as
	 * parameter.
	 * 
	 * @param source
	 * @param className
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
