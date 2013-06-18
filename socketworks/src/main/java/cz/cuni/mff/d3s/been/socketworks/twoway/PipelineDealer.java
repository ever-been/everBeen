package cz.cuni.mff.d3s.been.socketworks.twoway;

import java.util.Iterator;

import org.jeromq.ZMQ;
import org.jeromq.ZMQ.Socket;

import cz.cuni.mff.d3s.been.mq.Context;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.mq.ZMQContext;

/**
 * A (maybe unnecessary) implementation of a 0MQ dealer. Its job is to convey
 * frames received from the pipeline handler back to the low-level 0MQ sockets.
 * 
 * @author darklight
 * 
 */
class PipelineDealer extends PollPartaker {

	private static final int NOFLAGS = 0;

	private PipelineDealer(Socket mySocket, FrameForwardMapper replyMapper) {
		super(mySocket, replyMapper);
	}

	public static PipelineDealer create(Socket mySocket, FrameForwardMapper replyMapper) {
		return new PipelineDealer(mySocket, replyMapper);
	}

	@Override
	public int getPollType() {
		return ZMQ.POLLIN;
	}

	@Override
	public void receiveFromBuddy(Frames frames) throws MessagingException {
		// this content is thread-safe because it creates/cleans its own sockets
		final ZMQContext zctx = Context.getReference();
		final Socket sendReplySocket = zctx.socket(ZMQ.DEALER);
		final Iterator<byte[]> fi = frames.iterator();
		sendReplySocket.connect(this.hostname);
		while (fi.hasNext()) {
			final byte[] frame = fi.next();
			sendReplySocket.send(frame, fi.hasNext() ? ZMQ.SNDMORE : NOFLAGS);
		}
		sendReplySocket.close();
	}

}
