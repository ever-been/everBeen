package cz.cuni.mff.d3s.been.objectrepository;

import cz.cuni.mff.d3s.been.cluster.ClusterPersistenceConfiguration;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.util.PropertyReader;
import cz.cuni.mff.d3s.been.persistence.Query;
import cz.cuni.mff.d3s.been.persistence.SuccessAction;
import cz.cuni.mff.d3s.been.storage.Storage;

import java.util.concurrent.TimeUnit;

import static cz.cuni.mff.d3s.been.cluster.ClusterPersistenceConfiguration.*;
import static cz.cuni.mff.d3s.been.objectrepository.ObjectRepositoryConfiguration.*;

/**
 * Queue drain for the querying queue. Drains queries and initiates query processing.
 */
public class QueryQueueDrain extends QueueDrain<Query> {


	private QueryQueueDrain(ClusterContext ctx, SuccessAction<Query> successAction, Float failRateThreshold, Long suspendTimeOnHighFailRate) {
		super(ctx, Names.PERSISTENCE_QUERY_QUEUE_NAME, successAction, failRateThreshold, suspendTimeOnHighFailRate);
	}

	/**
	 * Create a drain for the queue of queries
	 *
	 * @param ctx Context to work with (where do the queries come from)
	 * @param storage Storage to use for finding answers
	 *
	 * @return A queue drain that drains queries and responds with answers
	 */
	public static final QueryQueueDrain create(ClusterContext ctx, Storage storage) {
		final PropertyReader propertyReader = PropertyReader.on(ctx.getProperties());
		final Float failRateThreshold = propertyReader.getFloat(FAIL_RATE_BEFORE_SUSPEND, DEFAULT_FAIL_RATE_BEFORE_SUSPEND);
		final Long suspendTimeOnHighFailRate = TimeUnit.SECONDS.toMillis(propertyReader.getLong(SUSPENSION_TIME, DEFAULT_SUSPENSION_TIME));
		final Long queryTimeout = TimeUnit.SECONDS.toMillis(propertyReader.getLong(QUERY_TIMEOUT, DEFAULT_QUERY_TIMEOUT));
		final Long queryProcTimeout = TimeUnit.SECONDS.toMillis(propertyReader.getLong(QUERY_PROCESSING_TIMEOUT, DEFAULT_QUERY_PROCESSING_TIMEOUT));
		final Long maxTotalQueryTimeout = queryTimeout + queryProcTimeout;

		final SuccessAction<Query> action = new AnswerQueryAction(storage, ctx.getMap(Names.PERSISTENCE_QUERY_ANSWERS_MAP_NAME), maxTotalQueryTimeout);
		return new QueryQueueDrain(ctx, action, failRateThreshold, suspendTimeOnHighFailRate);
	}
}
