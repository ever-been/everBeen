package cz.cuni.mff.d3s.been.nginx;

import cz.cuni.mff.d3s.been.taskapi.Requestor;
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

	private File workingDirectory = new File(".");

	private void downloadSources() {
		String svnPath = this.getProperty("svnPath");
		int currentRevision = Integer.parseInt(this.getProperty("revision"));

		CommandLine cmdLine = new CommandLine("svn");
		cmdLine.addArgument("checkout");
		cmdLine.addArgument("-r");
		cmdLine.addArgument(Integer.toString(currentRevision));
		cmdLine.addArgument(svnPath);
		cmdLine.addArgument("nginx");

		DefaultExecutor executor = new DefaultExecutor();
		executor.setWorkingDirectory(workingDirectory);
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
		File sourcesDir = new File(workingDirectory, "nginx");

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
		Requestor requestor = new Requestor();

		downloadSources();

		log.info("DownloadSources finished successfully.");

		buildSources();

		log.info("BuildSources finished successfully.");

		runServer();

		log.info("RunServer finished successfully.");
		log.info("Waiting for clients...");

		int numberOfClients = Integer.parseInt(this.getProperty("numberOfClients"));
		requestor.latchSet("rendezvous-latch", numberOfClients);
		requestor.checkPointSet("rendezvous-checkpoint", "1");
		requestor.latchWait("rendezvous-latch");
		requestor.latchSet("shutdown-latch", numberOfClients);
		requestor.checkPointSet("server-address", hostname + ":" + port);

		log.info("Server is running, waiting for clients to finish...");

		requestor.latchWait("shutdown-latch");

		log.info("All clients finished.");

		shutdownServer();

		log.info("ShutdownServer finished successfully.");

		requestor.close();
	}
}
