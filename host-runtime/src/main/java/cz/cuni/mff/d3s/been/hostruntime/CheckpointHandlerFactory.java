package cz.cuni.mff.d3s.been.hostruntime;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReadReplyHandler;
import cz.cuni.mff.d3s.been.socketworks.twoway.ReadReplyHandlerFactory;

class CheckpointHandlerFactory implements ReadReplyHandlerFactory, CheckpointHandlerRecycler {

	private final ClusterContext ctx;
	private final BlockingQueue<CheckpointHandler> recyclableHandlers;

	private CheckpointHandlerFactory(ClusterContext ctx) {
		this.ctx = ctx;
		recyclableHandlers = new LinkedBlockingQueue<CheckpointHandler>();
	}

	public static CheckpointHandlerFactory create(ClusterContext ctx) {
		return new CheckpointHandlerFactory(ctx);
	}

	@Override
	public ReadReplyHandler getHandler() {
		CheckpointHandler handler = recyclableHandlers.poll();
		return handler != null ? handler : CheckpointHandler.create(ctx, this);
	}

	@Override
	public void recycle(CheckpointHandler handler) {
		recyclableHandlers.add(handler);
	}

}
