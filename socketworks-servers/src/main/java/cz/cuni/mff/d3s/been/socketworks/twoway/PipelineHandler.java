package cz.cuni.mff.d3s.been.socketworks.twoway;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.cuni.mff.d3s.been.mq.MessagingException;

public class PipelineHandler implements FrameSink {

	private final ExecutorService exec;
	private final ReadReplyHandlerFactory factory;
	private final FrameForwardMapper forwardMapper;

	private PipelineHandler(ReadReplyHandlerFactory factory, FrameForwardMapper forwardMapper) {
		this.exec = Executors.newCachedThreadPool();
		this.factory = factory;
		this.forwardMapper = forwardMapper;
	}

	public static PipelineHandler create(ReadReplyHandlerFactory factory, FrameForwardMapper forwardMapper) {
		return new PipelineHandler(factory, forwardMapper);
	}

	@Override
	public void receiveFromBuddy(Frames frames) throws MessagingException {
		final ReplyingWorker worker = ReplyingWorker.create(forwardMapper.getForwardFor(this));
		worker.setUp(factory.getHandler(), frames);
		exec.submit(worker);
	}

	@Override
	public void receiveFromWire(Frames frames) throws MessagingException {
		throw new MessagingException("This sink has no wire.");
	}
}
