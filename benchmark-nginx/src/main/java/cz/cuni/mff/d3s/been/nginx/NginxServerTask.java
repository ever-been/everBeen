package cz.cuni.mff.d3s.been.nginx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.taskapi.CheckpointController;
import cz.cuni.mff.d3s.been.taskapi.Task;

/**
 * 
 * @author Kuba Břečka
 */
public class NginxServerTask extends Task {

	private static final Logger log = LoggerFactory.getLogger(NginxServerTask.class);

	private File workingDirectory = new File(".");

	private boolean fakeRun;

	private void downloadSources() {
		if (fakeRun) return;

		String svnPath = this.getProperty("svnPath");
		int currentRevision = Integer.parseInt(this.getProperty("revision"));

		MyUtils.exec(".", "svn", new String[] { "checkout", "-r", Integer.toString(currentRevision), svnPath, "nginx" });
	}

	private void buildSources() {
		if (fakeRun) return;

		File sourcesDir = new File(workingDirectory, "nginx");

		MyUtils.exec("./nginx", "auto/configure", new String[] {});
		MyUtils.exec("./nginx", "make", new String[] {});
	}

	private String hostname;
	private int port;
	Thread runnerThread;

	private void runServer() {
		if (fakeRun) return;

		// create logs dir
		try {
			final File sourcesDir = new File(workingDirectory, "nginx");
			Files.createDirectory(new File(sourcesDir, "logs").toPath());
		} catch (IOException e) {
			throw new RuntimeException("Cannot create logs directory.", e);
		}

		port = MyUtils.findRandomPort();
		hostname = MyUtils.getHostname();

		String configuration = MyUtils.getResourceAsString("nginx.conf");
		configuration = configuration.replace("listen 8000", "listen " + port);
		MyUtils.saveFileFromString(new File("./nginx/conf/nginx.conf"), configuration);

		// run the server from a separate thread
		runnerThread = new Thread() {
			@Override
			public void run() {
				MyUtils.exec("./nginx", "objs/nginx", new String[] { "-p", "." });
			}
		};
		runnerThread.start();

	}

	private void shutdownServer() {
		if (fakeRun) return;

		MyUtils.exec("./nginx", "objs/nginx", new String[] { "-p", ".", "-s", "stop" });
		try {
			runnerThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException("Cannot join runnerThread.", e);
		}
	}

	@Override
	public void run(String[] args) {
		try (CheckpointController requestor = CheckpointController.create()) {
			fakeRun = Boolean.parseBoolean(this.getProperty("fakeRun"));

			log.info("Nginx Server Task started.");

			downloadSources();

			log.info("DownloadSources finished successfully.");

			buildSources();

			log.info("BuildSources finished successfully.");

			runServer();

			log.info("RunServer finished successfully.");
			log.info("Waiting for clients...");

			int numberOfClients = Integer.parseInt(this.getProperty("numberOfClients"));
			requestor.latchSet("rendezvous-latch", numberOfClients);
			requestor.checkPointSet("rendezvous-checkpoint", "ok");
			requestor.latchWait("rendezvous-latch");
			requestor.latchSet("shutdown-latch", numberOfClients);

			requestor.checkPointSet("server-address", hostname + ":" + port);

			log.info("Server is running, waiting for clients to finish...");

			requestor.latchWait("shutdown-latch");

			log.info("All clients finished.");

			shutdownServer();

			log.info("ShutdownServer finished successfully.");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
