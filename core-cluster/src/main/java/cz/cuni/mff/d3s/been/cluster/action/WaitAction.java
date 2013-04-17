package cz.cuni.mff.d3s.been.cluster.action;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Instance;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.rep.Replay;
import cz.cuni.mff.d3s.been.mq.rep.Replays;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Martin Sixta
 */
final class WaitAction implements Action {
	private final Request request;
	private final ClusterContext ctx;
	BlockingQueue<String> queue = new LinkedBlockingQueue<>();

	public WaitAction(Request request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	class MapWaiter implements EntryListener<String, String> {

		@Override
		public void entryAdded(EntryEvent<String, String> event) {
			try {
				queue.put(event.getValue());
			} catch (InterruptedException e) {

			}
		}

		@Override
		public void entryRemoved(EntryEvent<String, String> event) {}

		@Override
		public void entryUpdated(EntryEvent<String, String> event) {
			try {
				queue.put(event.getValue());
			} catch (InterruptedException e) {

			}
		}

		@Override
		public void entryEvicted(EntryEvent<String, String> event) {}
	}

	@Override
	public Replay goGetSome() {
		String[] args = request.getSelector().split("#");
		if (args.length != 2 || args[0].isEmpty() || args[1].isEmpty()) {
			return Replays.createErrorReplay("Wrong selector specified: %s. Format is 'map#key'");
		}

		String map = args[0];
		String key = args[1];

		if (!ctx.containsInstance(Instance.InstanceType.MAP, map)) {
			//return Replays.createErrorReplay("No such map %s", map);
		}
		Replay replay = null;

		final MapWaiter waiter = new MapWaiter();

		IMap<String, String> imap = ctx.getMap(map);

		imap.addEntryListener(waiter, key, true);

		String value = imap.get(key);

		if (value == null) {
			try {
				value = queue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (value == null) {
			replay = Replays.createErrorReplay("Unknown error");
		} else {
			replay = Replays.createOkReplay(value);
		}

		imap.removeEntryListener(waiter);
		queue.clear();

		return replay;
	}
}
