package cz.cuni.mff.d3s.been.resultsrepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IQueue;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
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

	public static final String RESULT_QUEUE = "results";

	/** Result queue this repository is listening on */
	private IQueue<ResultCarrier> resQueue;

	/** The cluster instance in which this repository is running */
	private final ClusterContext clusterCtx;
	/** Data storage layer */
	private final Storage storage;

	ResultsRepository(ClusterContext clusterCtx, Storage storage) {
		this.clusterCtx = clusterCtx;
		this.storage = storage;
	}

	@Override
	public void start() throws ServiceException {
		storage.start();
		resQueue = clusterCtx.getInstance().getQueue(RESULT_QUEUE);
	}

	@Override
	public void stop() {
		storage.stop();
	}

}
