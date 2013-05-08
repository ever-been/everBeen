package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.rep.Replies;
import cz.cuni.mff.d3s.been.mq.rep.Reply;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Martin Sixta
 */
final class MapSetAction implements Action {
	private final Request request;
	private final ClusterContext ctx;

	public MapSetAction(Request request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Reply handle() {
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
