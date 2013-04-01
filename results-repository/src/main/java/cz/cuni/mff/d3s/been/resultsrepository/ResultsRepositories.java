package cz.cuni.mff.d3s.been.resultsrepository;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.resultsrepository.storage.Storage;
import cz.cuni.mff.d3s.been.resultsrepository.storage.StorageFactory;

/**
 * Utility class for creating SoftwareRepository instances.
 * 
 * The code is here to enable sharing.
 * 
 * @author Martin Sixta
 */
public class ResultsRepositories {

	/**
	 * Creates a new {@link SoftwareRepository}.
	 * 
	 * @param ctx
	 *          Cluster context used to register the service.
	 * @param host
	 *          Host on which to listen for requests
	 * @param port
	 *          Port on which to listen for requests
	 * 
	 * @return SoftwareRepository ready to be started.
	 */
	public static ResultsRepository createResultsRepository(ClusterContext ctx) {

		Storage storage = StorageFactory.createStorage();
		ResultsRepository resultsRepository = new ResultsRepository(ctx, storage);
		return resultsRepository;
	}
}
