package cz.cuni.mff.d3s.been.task;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.DAOException;
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
	public void run(String[] args) {
		final EntityID eid = new EntityID();
		eid.setKind("result");
		eid.setGroup("test");
		final ResultPersister rp = results.createResultPersister(eid);
		System.out.println("Hello world!");
		try {
			TestResult r = new TestResult();
			r.field = "Hello lols!";
			r.values = Arrays.asList(
					(int) (Math.random() * 100),
					(int) (Math.random() * 100));
			r.i.a = 7;
			r.i.b = 17;
			rp.persist(r);
		} catch (DAOException e) {
			log.error("OMG, Result persistence got mashed!", e);
		}
		log.info("task is logging");
		System.err.println("Output to stderr");
		try {
			Thread.sleep(30 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
		}
	}
}
