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
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskContextState;
import cz.cuni.mff.d3s.been.mq.rep.Replies;
import cz.cuni.mff.d3s.been.mq.rep.Reply;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Kuba Brecka
 */
public class ContextWaitAction implements Action {

	private static final Logger log = LoggerFactory.getLogger(ContextWaitAction.class);

	private final Request request;
	private final ClusterContext ctx;
	BlockingQueue<TaskContextEntry> queue = new LinkedBlockingQueue<>();

	public ContextWaitAction(Request request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	class MapWaiter implements EntryListener<String, TaskContextEntry> {

		@Override
		public void entryAdded(EntryEvent<String, TaskContextEntry> event) {}

		@Override
		public void entryRemoved(EntryEvent<String, TaskContextEntry> event) {
			queue.add(event.getValue());
		}

		@Override
		public void entryUpdated(EntryEvent<String, TaskContextEntry> event) {
			if (event.getValue().getContextState() == TaskContextState.FINISHED) {
				// TODO aborted?

				queue.add(event.getValue());
			}
		}

		@Override
		public void entryEvicted(EntryEvent<String, TaskContextEntry> event) {
			queue.add(event.getValue());
		}
	}

	@Override
	public Reply handle() {
		String key = request.getValue();

		Reply reply = null;

		final MapWaiter waiter = new MapWaiter();

		IMap<String, TaskContextEntry> iMap = ctx.getTaskContexts().getTaskContextsMap();

		iMap.addEntryListener(waiter, key, true);

		TaskContextEntry value = iMap.get(key);

		boolean timeout = false;

		// TODO states...
		if (value == null || value.getContextState() != TaskContextState.FINISHED) {
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
			reply = Replies.createOkReply(value.getContextState().toString());
		}

		iMap.removeEntryListener(waiter);
		queue.clear();

		return reply;
	}
}
