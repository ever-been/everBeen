package cz.cuni.mff.d3s.been.nginx;

import cz.cuni.mff.d3s.been.taskapi.Task;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 07.04.13
 * Time: 19:03
 * To change this template use File | Settings | File Templates.
 */
public class NginxBuildSourcesTask extends Task {

	private static final Logger log = LoggerFactory.getLogger(Task.class);

	public static void main(String[] args) {
		new NginxBuildSourcesTask().doMain(args);
	}

	private String getProperty(String name) {
		return null; // TODO
	}

	@Override
	public void run() {
		File contextWorkingDir = null; // TODO, myContext.getWorkingDirectory();
		File sourcesDir = new File(contextWorkingDir, "nginx");

		CommandLine cmdLine = new CommandLine("auto/configure");
		DefaultExecutor executor = new DefaultExecutor();
		executor.setWorkingDirectory(sourcesDir);
		int exitValue = 0;

		try {
			exitValue = executor.execute(cmdLine);
		} catch (IOException e) {
			throw new RuntimeException("Executing 'auto/configure' failed.", e);
		}

		if (exitValue != 0)
			throw new RuntimeException("Configure returned an error (exit code " + exitValue + ").");

		cmdLine = new CommandLine("make");
		exitValue = 0;
		try {
			exitValue = executor.execute(cmdLine);
		} catch (IOException e) {
			throw new RuntimeException("Executing 'make' failed.", e);
		}

		if (exitValue != 0)
			throw new RuntimeException("Make returned an error (exit code " + exitValue + ").");

		log.info("Task NginxBuildSourcesTask finished successfully.");
	}
}
