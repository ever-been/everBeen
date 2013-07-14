package cz.cuni.mff.d3s.been.hostruntime;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.persistence.Query;
import cz.cuni.mff.d3s.been.socketworks.SocketHandlerException;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReadReplyHandler;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Handler for queries into persistence layer.
 *
 * @author darklight
 */
public class PersistenceQueryHandler implements ReadReplyHandler {

	private static final Logger log = LoggerFactory.getLogger(PersistenceQueryHandler.class);

	private final ClusterContext ctx;
	private final HandlerRecycler recycler;
	private final ObjectReader queryReader;

	PersistenceQueryHandler(ClusterContext ctx, ObjectMapper om, HandlerRecycler recycler) {
		this.ctx = ctx;
		this.queryReader = om.reader(Query.class);
		this.recycler = recycler;
	}

	public String handle(String message) throws SocketHandlerException, InterruptedException {
		log.debug("Got {}", message);
		Query q = null;
		try {
			q = queryReader.readValue(message);
		} catch (IOException e) {
			throw new SocketHandlerException(String.format("Failed to deserialize query '%s'", message), e);
		}
		final String answer = ctx.getPersistence().query(q).toString(); // will produce [JSON, JSON, ..., JSON] which is valid JSON
		log.debug("Replying {}", answer);
		return answer;
	}

	@Override
	public void markAsRecyclable() {
		if (recycler != null) {
			recycler.recycle(this);
		}
	}
}
