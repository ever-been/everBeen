package cz.cuni.mff.d3s.been.hostruntime;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.codehaus.jackson.map.ObjectMapper;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReadReplyHandler;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReadReplyHandlerFactory;

/**
 * Factory for handlers servicing queries into persistence layer.
 * 
 * @author Radek Macha
 */
public class PersistenceQueryHandlerFactory implements ReadReplyHandlerFactory, HandlerRecycler {

	private final ClusterContext clusterContext;
	private final BlockingQueue<ReadReplyHandler> idleHandlers;
	private final ObjectMapper om;

	/**
	 * Creates new PersistenceQueryHandlerFactory.
	 * 
	 * @param clusterContext
	 *          connection to the cluster
	 */
	PersistenceQueryHandlerFactory(ClusterContext clusterContext) {
		this.clusterContext = clusterContext;
		this.idleHandlers = new LinkedBlockingQueue<ReadReplyHandler>();
		this.om = new ObjectMapper();

		om.enableDefaultTyping(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
	}

	/**
	 * Create a handler factory that provides handlers that forwards persistence
	 * queries into the cluster.
	 * 
	 * @param ctx
	 *          Cluster context (will be used for access to distributed data
	 *          structures)
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
