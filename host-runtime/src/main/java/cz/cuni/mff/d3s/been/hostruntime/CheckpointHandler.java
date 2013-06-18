package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.cluster.action.Action;
import cz.cuni.mff.d3s.been.cluster.action.Actions;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.mq.rep.Replies;
import cz.cuni.mff.d3s.been.mq.req.Request;
import cz.cuni.mff.d3s.been.socketworks.SocketHandlerException;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReadReplyHandler;

public class CheckpointHandler implements ReadReplyHandler {

	private final ClusterContext ctx;
	private final CheckpointHandlerRecycler recycler;

	private CheckpointHandler(ClusterContext ctx, CheckpointHandlerRecycler recycler) {
		this.ctx = ctx;
		this.recycler = recycler;
	}

	/**
	 * Create a {@link CheckpointHandler}, providing it a way to recycle itself
	 * after service.
	 * 
	 * @param ctx
	 *          Current cluster context (used to listening for checkpoints)
	 * @param recycler
	 *          Recycler to use after service
	 * 
	 * @return The {@link CheckpointHandler}
	 */
	static final CheckpointHandler create(ClusterContext ctx, CheckpointHandlerRecycler recycler) {
		return new CheckpointHandler(ctx, recycler);
	}

	@Override
	public String handle(String message) throws SocketHandlerException, InterruptedException {
		Request request = null;

		try {
			request = Request.fromJson(message);
		} catch (JSONUtils.JSONSerializerException e) {
			return Replies.createErrorReply("Cannot deserialize").toJson();
		}

		Action action = Actions.createAction(request, ctx);
		return action.goGetSome().toJson();
	}

	@Override
	public void markAsRecyclable() {
		if (recycler != null) {
			recycler.recycle(this);
		}
	}

}
