package cz.cuni.mff.d3s.been.cluster.action;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequest;

/**
 * An {@link Action} that handles a request for waiting until a value is
 * assigned to a key in the checkpoint map.
 * 
 * @author Martin Sixta
 */
final class MapWaitAction implements Action {

	/** slf4j logger */
	private static final Logger log = LoggerFactory.getLogger(MapWaitAction.class);

	/** the request to handle */
	private final CheckpointRequest request;

	/** BEEN cluster instance */
	private final ClusterContext ctx;

	/** a blocking queue that will be used for the waiting operation */
	BlockingQueue<String> queue = new LinkedBlockingQueue<>();

	/**
	 * Default constructor, creates the action with the specified request and
	 * cluster context.
	 * 
	 * @param request
	 *          the request to handle
	 * @param ctx
	 *          the cluster context
	 */
	public MapWaitAction(CheckpointRequest request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	/**
	 * A helper class which implements a listener for a specified Hazelcast map
	 * entry and adds it into the blocking queue when the event occurs.
	 */
	class MapWaiter implements EntryListener<String, String> {

		@Override
		public void entryAdded(EntryEvent<String, String> event) {
			queue.add(event.getValue());
		}

		@Override
		public void entryRemoved(EntryEvent<String, String> event) {}

		@Override
		public void entryUpdated(EntryEvent<String, String> event) {
			queue.add(event.getValue());
		}

		@Override
		public void entryEvicted(EntryEvent<String, String> event) {}
	}

	@Override
	public Reply handle() {
		String map = Actions.checkpointMapNameForRequest(request);
		String key = request.getSelector();

		//if (!ctx.containsInstance(Instance.InstanceType.MAP, map)) {
		//	return Replies.createErrorReply("No such map %s", map);
		//}

		Reply reply = null;

		final MapWaiter waiter = new MapWaiter();

		IMap<String, String> iMap = ctx.getMap(map);

		iMap.addEntryListener(waiter, key, true);

		String value = iMap.get(key);

		boolean timeout = false;

		if (value == null) {
			try {
				if (request.getTimeout() <= 0) {
					value = queue.take();
				} else {
					value = queue.poll(request.getTimeout(), TimeUnit.MILLISECONDS);
					if (value == null) {
						timeout = true;
					}
				}
			} catch (InterruptedException e) {
				log.warn("Poll interrupted", e);
			}
		}

		if (value == null) {
			if (timeout) {
				reply = Replies.createErrorReply("TIMEOUT");

			} else {
				reply = Replies.createErrorReply("Unknown error");
			}
		} else {
			reply = Replies.createOkReply(value);
		}

		iMap.removeEntryListener(waiter);
		queue.clear();

		return reply;
	}

}
