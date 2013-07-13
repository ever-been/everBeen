package cz.cuni.mff.d3s.been.task;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import cz.cuni.mff.d3s.been.core.persistence.Query;
import cz.cuni.mff.d3s.been.core.persistence.QueryBuilder;
import cz.cuni.mff.d3s.been.taskapi.ResultFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.taskapi.Task;
import cz.cuni.mff.d3s.been.taskapi.ResultPersister;

/**
 * @author Martin Sixta
 */
public class ExampleTask extends Task {

	private static final Logger log = LoggerFactory.getLogger(ExampleTask.class);

	int count = 0;

	private void performHeavyCalculations() {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("No MD5?", e);
		}

		Date start = new Date();
		byte[] message = "HELLO WORLD".getBytes();
		while (true) {
			Date now = new Date();
			if (now.getTime() - start.getTime() > 1000) break; // run for 1 second

			message = md.digest(message);
			count++;
		}
	}

	private void persistResult() {
		final EntityID eid = new EntityID();
		eid.setKind("result");
		eid.setGroup("example-md5");

		try {
			final ResultPersister rp = results.createResultPersister(eid);
			ExampleResult r = new ExampleResult();
			r.count = count;
			rp.persist(r);
			//rp.close(); <- notice this forgotten close: it works anyway
		} catch (DAOException e) {
			log.error("Cannot persist result.", e);
		}
	}

	private void pickupResult() {
		final EntityID eid = new EntityID();
		eid.setKind("result");
		eid.setGroup("example-md5");

		try {
			log.info("Picked up result {}", results.retrieveResults(new QueryBuilder().on(eid).build(), ExampleResult[].class).toString());
		} catch (DAOException e) {
			log.error("Cannot retrieve result.", e);
		}
	}

	@Override
	public void run(String[] args) {
		log.info("ExampleTask just started.");
		log.info("I am task from iteration {}", this.getProperty("iteration"));
		performHeavyCalculations();
		log.info("Performance testing finished.");
		persistResult();
		log.info("Result stored.");
		pickupResult();
		log.info("Result retrieved");

		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			throw new RuntimeException("Sleep interrupted.", e);
		}

		log.info("Task is about to finish.");
	}

}
