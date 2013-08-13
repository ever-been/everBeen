package cz.cuni.mff.d3s.been.repository.mongo;

import java.util.*;

import cz.cuni.mff.d3s.been.core.persistence.Entity;
import cz.cuni.mff.d3s.been.persistence.*;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;
import cz.cuni.mff.d3s.been.storage.Storage;
import cz.cuni.mff.d3s.been.storage.StorageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MongoStorageTest extends Assert {
	private static final Logger log = LoggerFactory.getLogger(MongoStorageTest.class);
	private static final Random random = new Random();

	private final JSONUtils jsonUtils = JSONUtils.newInstance();

	public static class DummyEntity extends Entity {
		/** A testing string */
		private String something;
		private Integer someNumber;

		DummyEntity() {
			this.something = "strange";
			this.someNumber = random.nextInt();
		}

		DummyEntity(int someNumber) {
			this.something = "strange";
			this.someNumber = someNumber;
		}

		public String getSomething() {
			return something;
		}

		public Integer getSomeNumber() {
			return someNumber;
		}
	}

	public static class ExtendedDummyEntity extends DummyEntity {
		private Float someFloat;

		public ExtendedDummyEntity() {
			this.someFloat = random.nextFloat();
		}

		public ExtendedDummyEntity(int someNumber, Float someFloat) {
			super(someNumber);
			this.someFloat = someFloat;
		}

		public Float getSomeFloat() {
			return someFloat;
		}
	}

	class StorageUsingStatement extends Statement {
		private final Statement base;
		private final MongoServerStandalone mongo;

		StorageUsingStatement(Statement base) {
			this.base = base;
			this.mongo = new MongoServerStandalone();
		}

		@Override
		public void evaluate() throws Throwable {
			mongo.start();
			connectStorage();
			try {
				base.evaluate();
			} finally {
				disconnectStorage();
				mongo.stop();
			}
		}
	}

	class OffedStorageUsingStatement extends Statement {
		private final Statement base;
		private final MongoServerStandalone mongo;

		OffedStorageUsingStatement(Statement base) {
			this.base = base;
			this.mongo = new MongoServerStandalone();
		}

		@Override
		public void evaluate() throws Throwable {
			mongo.start();
			connectStorage();
			mongo.stop();
			try {
				base.evaluate();
			} finally {
				disconnectStorage();
			}
		}
	}

	class StorageAllocatorRule implements TestRule {
		@Override
		public Statement apply(Statement base, Description description) {
			if (description.getMethodName().endsWith("_dbDown")) {
				return new OffedStorageUsingStatement(base);
			} else {
				return new StorageUsingStatement(base);
			}
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
		storage.store(dummyId, jsonUtils.serialize(new DummyEntity()));
		assertEquals(1, storage.query(new QueryBuilder().on(dummyId).with("something", "strange").fetch()).getData().size());

		storage.store(dummyId, jsonUtils.serialize(new DummyEntity()));
		assertEquals(2, storage.query(new QueryBuilder().on(dummyId).with("something", "strange").fetch()).getData().size());
	}

	@Test
	public void testDeleteSomeResults() throws JsonException, DAOException {
		storage.store(dummyId, jsonUtils.serialize(new DummyEntity()));
		storage.store(dummyId, jsonUtils.serialize(new DummyEntity()));
		assertEquals(2, storage.query(new QueryBuilder().on(dummyId).with("something", "strange").fetch()).getData().size());

		assertEquals(QueryStatus.OK, storage.query(new QueryBuilder().on(dummyId).with("something", "strange").delete()).getStatus());
		assertEquals(0, storage.query(new QueryBuilder().on(dummyId).with("something", "strange").fetch()).getData().size());
	}

	@Test
	public void testRetrieveEmptyResults() throws JsonException, DAOException {
		storage.store(dummyId, jsonUtils.serialize(new DummyEntity()));
		assertEquals(0, storage.query(new QueryBuilder().on(dummyId).with("something", "funny").fetch()).getData().size());
	}

	@Test
	public void testRetrieveFromInexistentCollection() {
		assertEquals(0, storage.query(new QueryBuilder().on(dummyId).with("something", "strange").fetch()).getData().size());
	}

	@Test
	public void testFetchQuery_dbDown() {
		final QueryAnswer answer = storage.query(new QueryBuilder().on(dummyId).fetch());
		assertEquals(QueryStatus.PERSISTENCE_DOWN, answer.getStatus());
	}

	@Test
	public void testRegexQuery() throws JsonException, DAOException {
		storage.store(dummyId, jsonUtils.serialize(new DummyEntity()));
		assertEquals(1, storage.query(new QueryBuilder().on(dummyId).with("something").like("str.nge").fetch()).getData().size());
	}

	@Test
	public void testAboveQuery() throws JsonException, DAOException {
		storage.store(dummyId, jsonUtils.serialize(new DummyEntity(1)));
		storage.store(dummyId, jsonUtils.serialize(new DummyEntity(2)));
		final QueryAnswer answer = storage.query(new QueryBuilder().on(dummyId).with("someNumber").above(2).fetch());
		assertEquals(QueryStatus.OK, answer.getStatus());
		assertTrue(answer.isCarryingData());
		assertEquals(1, answer.getData().size());
	}

	@Test
	public void testBelowQuery() throws JsonException, DAOException {
		storage.store(dummyId, jsonUtils.serialize(new DummyEntity(1)));
		storage.store(dummyId, jsonUtils.serialize(new DummyEntity(2)));
		final QueryAnswer answer = storage.query(new QueryBuilder().on(dummyId).with("someNumber").below(2).fetch());
		assertEquals(QueryStatus.OK, answer.getStatus());
		assertTrue(answer.isCarryingData());
		assertEquals(1, answer.getData().size());
	}

	@Test
	public void testIntervalQuery() throws JsonException, DAOException {
		storage.store(dummyId, jsonUtils.serialize(new DummyEntity(1)));
		storage.store(dummyId, jsonUtils.serialize(new DummyEntity(2)));
		storage.store(dummyId, jsonUtils.serialize(new DummyEntity(3)));
		final QueryAnswer answer = storage.query(new QueryBuilder().on(dummyId).with("someNumber").between(1, 3).fetch());
		assertEquals(QueryStatus.OK, answer.getStatus());
		assertTrue(answer.isCarryingData());
		assertEquals(2, answer.getData().size());
	}

	@Test
	public void testEntityCropping() throws JsonException, DAOException {
		storage.store(dummyId, jsonUtils.serialize(new ExtendedDummyEntity()));
		final QueryAnswer answer = storage.query(new QueryBuilder().on(dummyId).retrieving("something", "someNumber").fetch());
		assertEquals(QueryStatus.OK, answer.getStatus());
		assertTrue(answer.isCarryingData());
		assertEquals(1, answer.getData().size());
		jsonUtils.deserialize(answer.getData(), DummyEntity.class);
	}

	@Test
	public void testIncompleteEntityDeserialization() throws JsonException, DAOException {
		storage.store(dummyId, jsonUtils.serialize(new ExtendedDummyEntity()));
		final QueryAnswer answer = storage.query(new QueryBuilder().on(dummyId).retrieving("something", "someNumber").fetch());
		assertEquals(QueryStatus.OK, answer.getStatus());
		assertTrue(answer.isCarryingData());
		assertEquals(1, answer.getData().size());
		jsonUtils.deserialize(answer.getData(), ExtendedDummyEntity.class);
	}

	@Test
	public void testInequalityQuery() throws JsonException, DAOException {
		storage.store(dummyId, jsonUtils.serialize(new DummyEntity(4)));
		storage.store(dummyId, jsonUtils.serialize(new DummyEntity(5)));
		final QueryAnswer answer = storage.query(new QueryBuilder().on(dummyId).with("someNumber").differentFrom(5).fetch());
		assertEquals(QueryStatus.OK, answer.getStatus());
		assertTrue(answer.isCarryingData());
		assertEquals(1, answer.getData().size());
		assertEquals(Integer.valueOf(4), jsonUtils.deserialize(answer.getData().iterator().next(), DummyEntity.class).getSomeNumber());
	}

}
