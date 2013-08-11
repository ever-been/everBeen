package cz.cuni.mff.d3s.been.repository.janitor;

import cz.cuni.mff.d3s.been.core.PropertyReader;
import cz.cuni.mff.d3s.been.storage.Storage;

import java.util.Properties;

import static cz.cuni.mff.d3s.been.repository.janitor.PersistenceJanitorConfiguration.DEFAULT_FAILED_LONGEVITY;
import static cz.cuni.mff.d3s.been.repository.janitor.PersistenceJanitorConfiguration.FAILED_LONGEVITY;

/**
 * A keeper thread that runs persistence cleanup every once in a while
 *
 * @author darklight
 */
public class Janitor extends Thread {

	private final Long failedLongevity;
	private final Storage storage;

	private Janitor(Storage storage, Long failedLongevity) {
		this.storage = storage;
		this.failedLongevity = failedLongevity;
	}

	public static Janitor create(Storage storage, Properties config) {
		final PropertyReader propertyReader = PropertyReader.on(config);

		final Long failedLongevity = propertyReader.getLong(FAILED_LONGEVITY, DEFAULT_FAILED_LONGEVITY);

		return new Janitor(storage, failedLongevity);
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {

		}
	}
}
