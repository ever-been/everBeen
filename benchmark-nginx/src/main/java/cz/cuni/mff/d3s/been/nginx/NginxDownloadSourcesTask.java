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
 * Time: 18:43
 * To change this template use File | Settings | File Templates.
 */
public class NginxDownloadSourcesTask extends Task {

	private static final Logger log = LoggerFactory.getLogger(Task.class);

	public static void main(String[] args) {
		new NginxDownloadSourcesTask().doMain(args);
	}

	private String getProperty(String name) {
		return null; // TODO
	}

	@Override
	public void run() {
		String svnPath = this.getProperty("svnPath");
		int currentRevision = Integer.parseInt(this.getProperty("svnRevision"));

		File contextWorkingDir = null; // TODO, myContext.getWorkingDirectory();

		CommandLine cmdLine = new CommandLine("svn");
		cmdLine.addArgument("checkout");
		cmdLine.addArgument("-r");
		cmdLine.addArgument(Integer.toString(currentRevision));
		cmdLine.addArgument(svnPath);
		cmdLine.addArgument("nginx");

		DefaultExecutor executor = new DefaultExecutor();
		executor.setWorkingDirectory(contextWorkingDir);
		int exitValue = 0;

		try {
			exitValue = executor.execute(cmdLine);
		} catch (IOException e) {
			throw new RuntimeException("Executing 'svn' failed.", e);
		}

		if (exitValue != 0)
			throw new RuntimeException("Svn returned an error (exit code " + exitValue + ").");

		log.info("Task NginxDownloadSourcesTask finished successfully.");
	}
}
