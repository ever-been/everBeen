package cz.cuni.mff.d3s.been.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.taskapi.ResultPersister;
import cz.cuni.mff.d3s.been.taskapi.Task;

/**
 * @author Martin Sixta
 */
public class JacksonExampleTask extends Task {
	private static final Logger log = LoggerFactory.getLogger(Task.class);

	public static void main(String[] args) {
		new JacksonExampleTask().doMain(args);
	}

	@Override
	public void run(String[] args) {
		final EntityID eid = new EntityID();
		eid.setKind("result");
		eid.setGroup("test");

		System.out.println("Hello world!");
		try (final ResultPersister rp = results.createResultPersister(eid)) {
			rp.persist(new JacksonTestResult());
		} catch (DAOException e) {
			log.error("OMG, Result persistence got mashed!", e);
		}
		log.info("task is logging");
		System.err.println("Output to stderr");
	}
}
