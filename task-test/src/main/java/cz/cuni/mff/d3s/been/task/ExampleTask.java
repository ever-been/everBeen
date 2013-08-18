package cz.cuni.mff.d3s.been.task;

import static org.junit.Assert.assertEquals;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.Query;
import cz.cuni.mff.d3s.been.persistence.QueryBuilder;
import cz.cuni.mff.d3s.been.taskapi.ResultPersister;
import cz.cuni.mff.d3s.been.taskapi.Task;

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
			if (now.getTime() - start.getTime() > 1000)
				break; // run for 1 second

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
			ExampleResult r = results.createResult(ExampleResult.class);
			r.count = count;
			rp.persist(r);
			//rp.close(); <- notice this forgotten close: it works anyway
		} catch (DAOException e) {
			log.error("Cannot persist result.", e);
		}
	}

	private int pickupResult() {
		final EntityID eid = new EntityID();
		eid.setKind("result");
		eid.setGroup("example-md5");

		try {
			final Collection<ExampleResult> myResults = results.query(
					new QueryBuilder().on(eid).with("taskId", getId()).fetch(),
					ExampleResult.class);
			log.info("Picked up result {}", myResults);
			return myResults.size();
		} catch (DAOException e) {
			log.error("Cannot retrieve result.", e);
			return -1;
		}
	}

	private void tryAllKindsOfQueries() {
		final EntityID eid = new EntityID();
		eid.setKind("result");
		eid.setGroup("example-queryTesting");

		ExampleTestableResult result1;
		ExampleTestableResult result2;
		ExampleTestableResult result3;

		try {
			result1 = results.createResult(ExampleTestableResult.class);
			result2 = results.createResult(ExampleTestableResult.class);
			result3 = results.createResult(ExampleTestableResult.class);

			result1.init("res1", 1, 1.0f);
			result2.init("res2", 2, 2.0f);
			result3.init("res3", 3, 3.0f);
		} catch (DAOException e) {
			throw new AssertionError("Result testing sample creation failed", e);
		}

		try {
			final ResultPersister rp = results.createResultPersister(eid);
			rp.persist(result1);
			rp.persist(result2);
			rp.persist(result3);
			rp.close();
		} catch (DAOException e) {
			throw new AssertionError("Failed to initialize result persister for query integration testing samples", e);
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// Some generic wait for queue processing and persistence
			throw new AssertionError("Persist wait interrupted", e);
		}

		final Query regexQuery = new QueryBuilder().on(eid).with("taskId", getId()).with("testString").like(".*3").fetch();
		final Query intervalQueryInt = new QueryBuilder().with("taskId", getId()).on(eid).with("testInt").between(2, 3).fetch();
		final Query neqQuery = new QueryBuilder().on(eid).with("taskId", getId()).with("testInt").differentFrom(2).fetch();

		try {
			final Collection<ExampleTestableResult> regexResults = results.query(regexQuery, ExampleTestableResult.class);
			assertEquals(1, regexResults.size());
			assertEquals(Integer.valueOf(3), regexResults.iterator().next().getTestInt());
		} catch (DAOException e) {
			throw new AssertionError("Failed to execute regex query", e);
		}

		try {
			final Collection<ExampleTestableResult> intervalResults = results.query(
					intervalQueryInt,
					ExampleTestableResult.class);
			assertEquals(1, intervalResults.size());
			assertEquals(Integer.valueOf(2), intervalResults.iterator().next().getTestInt());
		} catch (DAOException e) {
			throw new AssertionError("Failed to execute interval query", e);
		}

		try {
			final Collection<ExampleTestableResult> neqResults = results.query(neqQuery, ExampleTestableResult.class);
			assertEquals(2, neqResults.size());
			final Iterator<ExampleTestableResult> ri = neqResults.iterator();
			assertEquals(Integer.valueOf(1), ri.next().getTestInt());
			assertEquals(Integer.valueOf(3), ri.next().getTestInt());
		} catch (DAOException e) {
			throw new AssertionError("Failed to execute not-eq query", e);
		}
	}

	// proof of concept deletion using commented out delete method
	/*
	private void deleteResult() {
		final EntityID eid = new EntityID();
		eid.setKind("result");
		eid.setGroup("example-md5");

		try {
			results.delete(new QueryBuilder().on(eid).with("taskId", getId()).delete());
		} catch (DAOException e) {
			log.error("Delete failed");
		}
	}*/

	@Override
	public void run(String[] args) {

		System.out.println("STANDARD OUTPUT (System.out.println...) MESSAGE");
		System.err.println("ERROR OUTPUT (System.err.println...) MESSAGE");
		log.info("ExampleTask just started.");
		log.info("I am task from iteration {}", this.getProperty("iteration"));
		performHeavyCalculations();
		log.info("Performance testing finished.");
		persistResult();
		log.info("Result stored.");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {}
		final int resCount1 = pickupResult();
		log.info("Result retrieved");

		tryAllKindsOfQueries();

		/*
		deleteResult();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}

		final int resCount2 = pickupResult();

		if (resCount1 <= resCount2) {
			throw new AssertionError("Nothing was deleted"); // assert that something was actually deleted
		}*/

		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			throw new RuntimeException("Sleep interrupted.", e);
		}

		log.info("Task is about to finish.");
	}

}
