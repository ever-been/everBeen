package cz.cuni.mff.d3s.been.resultsrepository.mongo;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils.JSONSerializerException;
import cz.cuni.mff.d3s.been.results.DAOException;
import cz.cuni.mff.d3s.been.results.ResultContainerId;

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

	private MongoStorage storage;
	private final ResultContainerId dummyId = new ResultContainerId() {

		@Override
		public String getContainerName() {
			return "testStorage";
		}

		@Override
		public String getDatabaseName() {
			return "results";
		}

		@Override
		public String getEntityName() {
			return "dummy";
		}

	};

	private void connectStorage() throws ServiceException {
		storage = new MongoStorage();
		storage.start();
	}

	private void disconnectStorage() {
		storage.stop();
		storage = null;
	}

	@Rule
	public StorageAllocatorRule storageAllocatorRule = new StorageAllocatorRule();

	@Test
	public void testSubmitAndRetrieveItem() throws JSONSerializerException, DAOException {
		storage.storeResult(dummyId, JSONUtils.serialize(new DummyResult()));
	}

}
