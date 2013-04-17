package cz.cuni.mff.d3s.been.cluster.action;

import com.hazelcast.core.Instance;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.rep.Replay;
import cz.cuni.mff.d3s.been.mq.rep.Replays;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Martin Sixta
 */
final class SetMapAction implements Action {
	private final Request request;
	private final ClusterContext ctx;

	public SetMapAction(Request request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
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

		Object mapValue = ctx.getMap(map).put(key, request.getValue());

		String replayValue;
		if (mapValue == null) {
			replayValue = "";
		} else {
			replayValue = mapValue.toString();
		}

		return Replays.createOkReplay(replayValue);
	}
}
