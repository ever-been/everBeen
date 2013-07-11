package cz.cuni.mff.d3s.been.resultsrepository.mongo;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.core.utils.JsonException;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.storage.Storage;
import cz.cuni.mff.d3s.been.storage.StorageBuilderFactory;

public final class MongoStorageTest extends Assert {

	/**
	 * A test implementation of {@link cz.cuni.mff.d3s.been.results.Result}.
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

	MongoStorageTest() {
		dummyId = new EntityID();
		dummyId.setKind("results");
		dummyId.setGroup("testStorage");
	}

	private void connectStorage() throws ServiceException {
		final Properties properties = new Properties();
		storage = StorageBuilderFactory.createBuilder(properties).build();
		storage.start();
	}

	private void disconnectStorage() {
		storage.stop();
		storage = null;
	}

	@Rule
	public StorageAllocatorRule storageAllocatorRule = new StorageAllocatorRule();

	//@Test
	public void testSubmitAndRetrieveItem() throws JsonException, DAOException {
		storage.store(dummyId, JSONUtils.serialize(new DummyResult()));
	}

}
