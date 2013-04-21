package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.rep.Replies;
import cz.cuni.mff.d3s.been.mq.rep.Reply;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Martin Sixta
 */
final class MapGetAction implements Action {
	private final Request request;
	private final ClusterContext ctx;

	public MapGetAction(Request request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Reply goGetSome() {
		String[] args;

		try {
			args = MapActionUtils.parseSelector(request.getSelector());
		} catch (Exception e) {
			return Replies.createErrorReply(e.getMessage());
		}

		String map = args[0];
		String key = args[1];

		// TODO later migh be a good idea to add it back
		//if (!ctx.containsInstance(Instance.InstanceType.MAP, map)) {
		//return Replies.createErrorReply("No such map %s", map);
		//}

		Object mapValue = ctx.getMap(map).get(key);

		String replyValue;
		if (mapValue == null) {
			replyValue = "";
		} else {
			replyValue = mapValue.toString();
		}

		return Replies.createOkReply(replyValue);
	}

}
