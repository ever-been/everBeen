package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReadReplyHandler;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReadReplyHandlerFactory;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Factory for handlers servicing queries into persistence layer.
 */
public class PersistenceQueryHandlerFactory implements ReadReplyHandlerFactory, HandlerRecycler {

	private final ClusterContext clusterContext;
	private final BlockingQueue<ReadReplyHandler> idleHandlers;
	private final ObjectMapper om;

	PersistenceQueryHandlerFactory(ClusterContext clusterContext) {
		this.clusterContext = clusterContext;
		this.idleHandlers = new LinkedBlockingQueue<>();
		this.om = new ObjectMapper();
	}

	/**
	 * Create a handler factory that provides handlers that forwards persistence queries into the cluster.
	 *
	 * @param ctx Cluster context (will be used for access to distributed data structures)
	 *
	 * @return The handler factory
	 */
	public static final PersistenceQueryHandlerFactory create(ClusterContext ctx) {
		return new PersistenceQueryHandlerFactory(ctx);
	}

	@Override
	public ReadReplyHandler getHandler() {
		ReadReplyHandler handler = idleHandlers.poll();
		if (handler == null) {
			handler = new PersistenceQueryHandler(clusterContext, om, this);
		}
		return handler;
	}

	@Override
	public void recycle(ReadReplyHandler handler) {
		idleHandlers.add(handler);
	}
}
