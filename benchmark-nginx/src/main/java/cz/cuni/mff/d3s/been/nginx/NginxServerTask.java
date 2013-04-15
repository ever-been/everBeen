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
public class NginxServerTask extends Task {

	private static final Logger log = LoggerFactory.getLogger(Task.class);

	public static void main(String[] args) {
		new NginxServerTask().doMain(args);
	}

	private String getProperty(String name) {
		return null; // TODO
	}

	private void downloadSources() {
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
	}

	private void buildSources() {
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
	}

	private String hostname;
	private int port;

	private void runServer() {
		// TODO
	}

	private void shutdownServer() {

	}

	@Override
	public void run() {
		downloadSources();

		log.info("DownloadSources finished successfully.");

		buildSources();

		log.info("BuildSources finished successfully.");

		runServer();

		log.info("RunServer finished successfully.");
		log.info("Waiting for clients...");

		int numberOfClients = Integer.parseInt(this.getProperty("numberOfClients"));
		this.waitForCheckpointValue("rendezvous", numberOfClients);
		this.checkpointReached("server-running", hostname + ":" + port);

		log.info("Server is running, waiting for clients to finish...");

		this.waitForCheckpointValue("client-finished", numberOfClients);

		log.info("All clients finished.");

		shutdownServer();

		log.info("ShutdownServer finished successfully.");
	}
}
