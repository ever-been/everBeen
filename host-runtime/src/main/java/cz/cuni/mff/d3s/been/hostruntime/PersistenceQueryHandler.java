package cz.cuni.mff.d3s.been.hostruntime;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.persistence.Query;
import cz.cuni.mff.d3s.been.persistence.QuerySerializer;
import cz.cuni.mff.d3s.been.socketworks.SocketHandlerException;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReadReplyHandler;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * Handler for queries into persistence layer.
 * 
 * @author darklight
 */
public class PersistenceQueryHandler implements ReadReplyHandler {

	private static final Logger log = LoggerFactory.getLogger(PersistenceQueryHandler.class);

	private final ClusterContext ctx;
	private final ObjectMapper om;
	private final QuerySerializer querySerializer;
	private final HandlerRecycler recycler;

	/**
	 * Creates new PersistenceQueryHandler.
	 * 
	 * @param ctx
	 *          connection to the cluster
	 * @param om
	 *          mapper of objects
	 * @param recycler
	 *          recycler of handlers
	 */
	PersistenceQueryHandler(ClusterContext ctx, ObjectMapper om, HandlerRecycler recycler) {
		this.ctx = ctx;
		this.om = om;
		this.querySerializer = new QuerySerializer();
		this.recycler = recycler;
	}

	@Override
	public String handle(String message) throws SocketHandlerException, InterruptedException {
		log.debug("Got {}", message);
		Query q = null;
		try {
			q = querySerializer.deserializeQuery(message);
		} catch (JsonException e) {
			throw new SocketHandlerException(String.format("Failed to deserialize query '%s'", message), e);
		}
		try {
			final String answer = querySerializer.serializeAnswer(ctx.getPersistence().query(q));
			log.debug("Replying {}", answer);
			return answer;
		} catch (DAOException e) {
			throw new SocketHandlerException("Query failed", e);
		} catch (JsonException e) {
			throw new SocketHandlerException("Failed to serialize query results", e);
		}
	}

	@Override
	public void markAsRecyclable() {
		if (recycler != null) {
			recycler.recycle(this);
		}
	}
}
