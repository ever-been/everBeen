package cz.cuni.mff.d3s.been.resultsrepository.mongo;

import java.util.Iterator;
import java.util.ServiceLoader;

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

	/**
	 * A test implementation of {@link Result}.
	 * 
	 * @author darklight
	 */
	public class DummyResult {
		/** A testing string */
		private String something = "strange";

		public String getSomething() {
			return something;
		}

		public void setSomething(String something) {
			this.something = something;
		}
	}

	class StorageUsingStatement extends Statement {
		private final Statement base;

		StorageUsingStatement(Statement base) {
			this.base = base;
		}

		@Override
		public void evaluate() throws Throwable {
			connectStorage();
			base.evaluate();
			disconnectStorage();
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
		storage = new MongoStorageBuilder().build();
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
	@Ignore
	public void testSubmitAndRetrieveItem() throws JsonException, DAOException {
		storage.store(dummyId, JSONUtils.serialize(new DummyResult()));
	}

}
