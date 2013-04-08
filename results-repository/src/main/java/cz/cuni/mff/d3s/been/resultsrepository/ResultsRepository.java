package cz.cuni.mff.d3s.been.resultsrepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IQueue;
import com.hazelcast.core.ItemListener;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.results.ResultCarrier;
import cz.cuni.mff.d3s.been.resultsrepository.storage.Storage;

/**
 * A cluster node that can store and provide BPKs and Maven artifacts through a
 * simple HTTP server.
 * 
 * @author darklight
 * 
 */
public class ResultsRepository implements IClusterService {

	/** Results repository logger */
	private static final Logger log = LoggerFactory.getLogger(ResultsRepository.class);

	/** Result queue this repository is listening on */
	private IQueue<ResultCarrier> resQueue;
	private ResultQueueDigester digester;
	private final ItemListener<ResultCarrier> resQueueListener;

	/** The cluster instance in which this repository is running */
	private final ClusterContext clusterCtx;
	/** Data storage layer */
	private final Storage storage;

	ResultsRepository(ClusterContext clusterCtx, Storage storage) {
		this.clusterCtx = clusterCtx;
		this.storage = storage;
		this.resQueueListener = new ResultCounterListener(digester);
	}

	@Override
	public void start() throws ServiceException {
		log.info("Starting results repository...");
		if (storage == null) {
			throw new ServiceException("Results Repository presistence layer is unavailable.");
		}
		storage.start();
		resQueue = clusterCtx.getInstance().getQueue(Names.RESULT_QUEUE_NAME);
		digester = new ResultQueueDigester(resQueue, storage);
		digester.start();
		resQueue.addItemListener(resQueueListener, false);
		log.info("Results repository successfully started!");
	}

	@Override
	public void stop() {
		log.info("Stopping results repository...");
		resQueue.removeItemListener(resQueueListener);
		digester.stop();
		storage.stop();
		log.info("Results repository stopped.");
	}

	@Override
	public Reaper createReaper() {
		final Reaper reaper = new Reaper() {
			@Override
			protected void reap() throws InterruptedException {
				resQueue.removeItemListener(resQueueListener);
			}

			@Override
			protected void shutdown() throws InterruptedException {
				storage.stop();
			}
		};
		reaper.pushTarget(digester);
		return reaper;
	}
}
