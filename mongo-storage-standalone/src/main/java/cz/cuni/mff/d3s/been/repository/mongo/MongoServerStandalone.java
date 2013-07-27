package cz.cuni.mff.d3s.been.repository.mongo;

import java.io.IOException;

import cz.cuni.mff.d3s.been.cluster.Service;
import cz.cuni.mff.d3s.been.storage.StorageException;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.distribution.Version;

/**
 * @author Martin Sixta
 */
public class MongoServerStandalone implements Service {

	private MongodProcess mongod;
	private MongodExecutable mongodExecutable;

	@Override
	public void start() throws StorageException {

		int port = 12345;
		MongodConfig mongodConfig = new MongodConfig(Version.Main.PRODUCTION, port, false);

		MongodStarter runtime = MongodStarter.getDefaultInstance();

		mongodExecutable = runtime.prepare(mongodConfig);
		try {
			mongod = mongodExecutable.start();
		} catch (IOException e) {
			throw new StorageException("Cannot start standalone MondDB server", e);
		}

	}

	@Override
	public void stop() {
		if (mongodExecutable != null) {
			mongodExecutable.stop();
			mongodExecutable = null;
		}
	}
}
