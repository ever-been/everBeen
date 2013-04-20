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
import cz.cuni.mff.d3s.been.mq.rep.Replay;
import cz.cuni.mff.d3s.been.mq.rep.Replays;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Martin Sixta
 */
final class MapWaitAction implements Action {
	private static final Logger log = LoggerFactory.getLogger(MapWaitAction.class);
	private final Request request;
	private final ClusterContext ctx;
	BlockingQueue<String> queue = new LinkedBlockingQueue<>();

	public MapWaitAction(Request request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

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
	public Replay goGetSome() {
		String[] args;

		try {
			args = MapActionUtils.parseSelector(request.getSelector());
		} catch (Exception e) {
			return Replays.createErrorReplay(e.getMessage());
		}

		String map = args[0];
		String key = args[1];

		//if (!ctx.containsInstance(Instance.InstanceType.MAP, map)) {
		//	return Replays.createErrorReplay("No such map %s", map);
		//}

		Replay replay = null;

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
				replay = Replays.createErrorReplay("TIMEOUT");

			} else {
				replay = Replays.createErrorReplay("Unknown error");
			}
		} else {
			replay = Replays.createOkReplay(value);
		}

		iMap.removeEntryListener(waiter);
		queue.clear();

		return replay;
	}
}
