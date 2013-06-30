package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequest;

/**
 * @author Martin Sixta
 */
final class MapSetAction implements Action {
	private final CheckpointRequest request;
	private final ClusterContext ctx;

	public MapSetAction(CheckpointRequest request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Reply goGetSome() {
		String map = Actions.checkpointMapNameForRequest(request);
		String key = request.getSelector();

		//if (!ctx.containsInstance(Instance.InstanceType.MAP, map)) {
		//	return Replies.createErrorReply("No such map %s", map);
		//}

		Object mapValue = ctx.getMap(map).put(key, request.getValue());

		String replyValue;
		if (mapValue == null) {
			replyValue = "";
		} else {
			replyValue = mapValue.toString();
		}

		return Replies.createOkReply(replyValue);
	}
}
