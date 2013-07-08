package cz.cuni.mff.d3s.been.socketworks.twoway;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.socketworks.Socketworks;
import org.jeromq.ZMQ;

import cz.cuni.mff.d3s.been.mq.Context;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.mq.ZMQContext;
import cz.cuni.mff.d3s.been.socketworks.QueueGuard;

/**
 * @author darklight
 */
class ReadReplyGuard implements QueueGuard {
    private final String hostname;
	private final PollPipeline pollPipeline;
    private final ZMQContext zctx;
    private final PipelineRouter router;
    private final PipelineDealer dealer;

	ReadReplyGuard(String hostname, PollPipeline pollPipeline, PipelineRouter pipelineRouter, PipelineDealer pipelineDealer, ZMQContext zctx) {
        this.hostname = hostname;
		this.pollPipeline = pollPipeline;
        this.router = pipelineRouter;
        this.dealer = pipelineDealer;
        this.zctx = zctx;
	}

	public static
			ReadReplyGuard
			create(String hostname, ReadReplyHandlerFactory handlerFactory) throws MessagingException {
		final ZMQContext zctx = Context.getReference();
		final SkeletalFrameForwardMapper forwardMapper = new SkeletalFrameForwardMapper();
		final PipelineRouter router = PipelineRouter.create(zctx.socket(ZMQ.ROUTER), hostname, forwardMapper);
		final PipelineDealer dealer = PipelineDealer.create(zctx.socket(ZMQ.DEALER), hostname, forwardMapper);
		final FrameSink handler = PipelineHandler.create(handlerFactory, forwardMapper);

		// Bypass the 0MQ router/dealer model. Messages received on router get passed to handler.
		// Handler spawns threads that handle content, then re-attaches IDs to responses.
		// Crunched content then gets forwarded to dealer, who only provides de-multiplexing by reattaching IDs and forwarding back to the router to process sending.
		forwardMapper.addRoute(router, handler);
		forwardMapper.addRoute(handler, dealer);
        forwardMapper.addRoute(dealer, router);

		final List<PollPartaker> partakers = new ArrayList<PollPartaker>(2);
		partakers.add(router);
		partakers.add(dealer);

		final PollPipeline pollPipeline = PollPipeline.create(partakers, forwardMapper);
		return new ReadReplyGuard(hostname, pollPipeline, router, dealer, zctx);
	}

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
	public Integer getPort() {
		return router.getPort();
	}

    @Override
    public String getConnection() {
        return Socketworks.Protocol.TCP.connection(getHostname(), getPort());
    }

	@Override
	public void listen() {
        dealer.start();
        router.start();
		pollPipeline.start();
	}

	@Override
	public void terminate() throws MessagingException {
		pollPipeline.doStop();
        try {
            pollPipeline.join();
        } catch (InterruptedException e) {
            throw new MessagingException("Join on poll pipeline interrupted", e);
        } finally {
            router.stop();
            dealer.stop();
            zctx.term();
        }
	}

}
