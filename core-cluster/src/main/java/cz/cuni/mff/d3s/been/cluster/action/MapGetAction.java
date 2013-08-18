package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequest;

/**
 * An {@link Action} that handles a request for retrieving the current value
 * from the checkpoint map.
 * 
 * @author Martin Sixta
 */
final class MapGetAction implements Action {

	/** the request to handle */
	private final CheckpointRequest request;

	/** BEEN cluster instance */
	private final ClusterContext ctx;

	/**
	 * Default constructor, creates the action with the specified request and
	 * cluster context.
	 * 
	 * @param request
	 *          the request to handle
	 * @param ctx
	 *          the cluster context
	 */
	public MapGetAction(CheckpointRequest request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Reply handle() {
		String map = Actions.checkpointMapNameForRequest(request);
		String key = request.getSelector();

		// TODO later migh be a good idea to add it back
		//if (!ctx.containsInstance(Instance.InstanceType.MAP, map)) {
		//return Replies.createErrorReply("No such map %s", map);
		//}

		if (key == null || key.isEmpty()) {
			return Replies.createErrorReply("Key must be non-empty");
		}

		Object mapValue = ctx.getMap(map).get(key);

		String replyValue;
		if (mapValue == null) {
			replyValue = null;
		} else {
			replyValue = mapValue.toString();
		}

		return Replies.createOkReply(replyValue);
	}

}
