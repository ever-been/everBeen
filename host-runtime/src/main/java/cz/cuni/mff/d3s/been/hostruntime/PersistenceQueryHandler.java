package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.persistence.Query;
import cz.cuni.mff.d3s.been.persistence.QuerySerializer;
import cz.cuni.mff.d3s.been.socketworks.SocketHandlerException;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReadReplyHandler;
import cz.cuni.mff.d3s.been.util.JsonException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

	PersistenceQueryHandler(ClusterContext ctx, ObjectMapper om, HandlerRecycler recycler) {
		this.ctx = ctx;
		this.om = om;
		this.querySerializer = new QuerySerializer();
		this.recycler = recycler;
	}

	public String handle(String message) throws SocketHandlerException, InterruptedException {
		log.debug("Got {}", message);
		Query q = null;
		try {
			q = querySerializer.deserializeQuery(message);
		} catch (JsonException e) {
			throw new SocketHandlerException(String.format("Failed to deserialize query '%s'", message), e);
		}
		try {
			final String answer = om.writeValueAsString(ctx.getPersistence().query(q)); // will produce [JSON, JSON, ..., JSON] which is valid JSON
			log.debug("Replying {}", answer);
			return answer;
		} catch (IOException e) {
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
