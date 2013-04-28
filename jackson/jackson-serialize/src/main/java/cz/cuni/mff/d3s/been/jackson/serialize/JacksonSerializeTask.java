package cz.cuni.mff.d3s.been.jackson.serialize;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.results.DAOException;
import cz.cuni.mff.d3s.been.taskapi.Task;
import cz.cuni.mff.d3s.been.taskapi.results.ResultPersister;

/**
 * @author darklight
 */
public class JacksonSerializeTask extends Task {
	private static final Logger log = LoggerFactory.getLogger(JacksonSerializeTask.class);

	public static void main(String[] args) {
		new JacksonSerializeTask().doMain(args);
	}

	@Override
	public void run() {
		log.info(System.getProperties().toString());
		log.info(System.getenv().toString());
		final DataType dataType = DataType.valueOf(System.getenv("dataType"));
		final CachePolicy cachePolicy = CachePolicy.valueOf(System.getenv("cachePolicy"));
		final Long repetitions = Long.valueOf(System.getenv("repetitions"));

		final EntityID eid = new EntityID();
		eid.setKind("result");
		eid.setGroup("jackson-serialize");

		final ResultPersister rp = results.createResultPersister(eid);

		final SerializationUnit su = cachePolicy.getSerializationUnit();
		final DataGenerator dg = dataType.getDataGenerator();

		for (int i = 0; i < repetitions; ++i) {
			try {
				rp.persist(new TimeResult(su.doMeasure(dg.generate())));
			} catch (DAOException e) {
				log.info(
						"Benchmark was done but BEEN failed to serialize my result.",
						e);
			} catch (IOException e) {
				log.error("ObjectMapper failed to serialize generated object", e);
			}
		}
	}
}
