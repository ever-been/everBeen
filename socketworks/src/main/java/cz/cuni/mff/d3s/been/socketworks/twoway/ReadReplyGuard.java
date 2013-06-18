package cz.cuni.mff.d3s.been.socketworks.twoway;

import java.util.ArrayList;
import java.util.List;

import org.jeromq.ZMQ;

import cz.cuni.mff.d3s.been.mq.Context;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.mq.ZMQContext;
import cz.cuni.mff.d3s.been.socketworks.QueueGuard;

/**
 * @author darklight
 */
public class ReadReplyGuard implements QueueGuard {
	private final PollPipeline pollPipeline;
	private int inputPort;

	ReadReplyGuard(ZMQContext zctx, PollPipeline pollPipeline, ReadReplyHandlerFactory handlerFactory) {
		this.pollPipeline = pollPipeline;
	}

	public static
			ReadReplyGuard
			create(String hostname, String queueName, ReadReplyHandlerFactory handlerFactory) throws MessagingException {
		final ZMQContext zctx = Context.getReference();
		final SkeletalFrameForwardMapper forwardMapper = new SkeletalFrameForwardMapper();
		final PollPartaker router = PipelineRouter.create(zctx.socket(ZMQ.ROUTER), forwardMapper);
		final PollPartaker dealer = PipelineDealer.create(zctx.socket(ZMQ.DEALER), forwardMapper);
		final FrameSink handler = PipelineHandler.create(handlerFactory, forwardMapper);

		// Bypass the 0MQ router/dealer model. Messages received on router get passed to handler.
		// Handler spawns threads that handle content, then re-attaches IDs to responses.
		// Crunched content then gets forwarded to dealer, who only provides de-multiplexing.
		forwardMapper.addRoute(router, handler);
		forwardMapper.addRoute(handler, dealer);

		final List<PollPartaker> partakers = new ArrayList<PollPartaker>(2);
		partakers.add(router);
		partakers.add(dealer);

		final PollPipeline pollPipeline = PollPipeline.create(partakers, forwardMapper);
		return new ReadReplyGuard(zctx, pollPipeline, handlerFactory);
	}

	@Override
	public Integer getPort() {
		return inputPort;
	}

	@Override
	public void listen() {
		pollPipeline.start();
	}

	@Override
	public void terminate() throws MessagingException {
		pollPipeline.doStop();
	}

}
