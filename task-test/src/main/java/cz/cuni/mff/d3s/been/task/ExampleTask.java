package cz.cuni.mff.d3s.been.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.results.DAOException;
import cz.cuni.mff.d3s.been.taskapi.Task;
import cz.cuni.mff.d3s.been.taskapi.results.ResultPersister;

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
		final ResultPersister rp = results.createResultPersister(new TestContainerId());
		System.out.println("Hello world!");
		try {
			rp.persist(new TestResult());
		} catch (DAOException e) {
			log.error("OMG, Result persistence got mashed!");
		}
		log.info("task is logging");
		System.err.println("Output to stderr");
	}
}
