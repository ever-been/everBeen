package cz.cuni.mff.d3s.been.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.taskapi.Task;
import cz.cuni.mff.d3s.been.taskapi.mq.MessagingSystem;

/**
 * @author Martin Sixta
 */
public class ExampleTask extends Task {
	private static final Logger log = LoggerFactory.getLogger(Task.class);

	public static void main(String[] args) {
		new ExampleTask().doMain(args);
	}

	@Override
	public void run() {
		MessagingSystem.connect();

		System.out.println("Hello world!");
		log.info("task is logging");
		System.err.println("Output to stderr");

		MessagingSystem.disconnect();
	}
}
