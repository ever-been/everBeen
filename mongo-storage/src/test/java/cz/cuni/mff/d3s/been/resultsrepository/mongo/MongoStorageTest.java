package cz.cuni.mff.d3s.been.resultsrepository.mongo;

import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceLoader;

import cz.cuni.mff.d3s.been.core.persistence.Entity;
import cz.cuni.mff.d3s.been.core.persistence.Query;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.core.utils.JsonException;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.storage.Storage;
import cz.cuni.mff.d3s.been.storage.StorageBuilder;

public final class MongoStorageTest extends Assert {

	public class DummyEntity extends Entity {
		/** A testing string */
		private String something = "strange";

		DummyEntity() {
			this.taskId = "1";
			this.contextId = "1";
			this.benchmarkId = "1";
		}

		public String getSomething() {
			return something;
		}
	}

	class StorageUsingStatement extends Statement {
		private final Statement base;
		private final MongoServerStandalone mongo = new MongoServerStandalone();

		StorageUsingStatement(Statement base) {
			this.base = base;
		}

		@Override
		public void evaluate() throws Throwable {
			mongo.start();
			connectStorage();
			base.evaluate();
			disconnectStorage();
			mongo.stop();
		}
	}

	class StorageAllocatorRule implements TestRule {
		@Override
		public Statement apply(Statement base, Description description) {
			return new StorageUsingStatement(base);
		}
	}

	private Storage storage;
	private final EntityID dummyId;

	public MongoStorageTest() {
		dummyId = new EntityID();
		dummyId.setKind("results");
		dummyId.setGroup("test");
	}

	private void connectStorage() throws ServiceException {
		final Properties props = new Properties();
		props.setProperty("mongodb.hostname", "localhost:12345");
		storage = new MongoStorageBuilder().withProperties(props).build();
		storage.start();
	}

	private void disconnectStorage() {
		storage.stop();
		storage = null;
	}

	@Rule
	public StorageAllocatorRule storageAllocatorRule = new StorageAllocatorRule();

	@Test
	public void testLoadDynamically() {
		Iterator<StorageBuilder> sl = ServiceLoader.load(StorageBuilder.class).iterator();
		assertTrue(sl.hasNext());
		assertTrue(sl.next() instanceof MongoStorageBuilder);
	}

	@Test
	public void testSubmitAndRetrieveItems() throws JsonException, DAOException {
		storage.store(dummyId, JSONUtils.serialize(new DummyEntity()));
		assertEquals(1, storage.query(Query.on(dummyId).with("something", "strange")).size());

		storage.store(dummyId, JSONUtils.serialize(new DummyEntity()));
		assertEquals(2, storage.query(Query.on(dummyId).with("something", "strange")).size());
	}

	@Test
	public void testRetrieveEmptyResults() throws JsonException, DAOException {
		storage.store(dummyId, JSONUtils.serialize(new DummyEntity()));
		assertEquals(0, storage.query(Query.on(dummyId).with("something", "funny")).size());
	}

	@Test
	public void testRetrieveFromInexistentCollection() {
		assertEquals(0, storage.query(Query.on(dummyId).with("something", "strange")).size());
	}

}
