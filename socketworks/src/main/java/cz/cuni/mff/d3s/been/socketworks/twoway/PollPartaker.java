package cz.cuni.mff.d3s.been.socketworks.twoway;

import java.util.Iterator;
import java.util.UUID;

import org.jeromq.ZMQ;
import org.jeromq.ZMQ.Socket;

import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * A participant in the poll pipeline. Represents a socket and actions taken for
 * events received on that socket.
 * 
 * @author darklight
 * 
 */
abstract class PollPartaker implements FrameSink {

	private static final int NOFLAGS = 0;

	static final String TCP_PROTO = "inproc";

	final String hostname;
	final Socket mySocket;
	final FrameForwardMapper replyMapper;

	protected PollPartaker(Socket mySocket, FrameForwardMapper replyMapper) {
		this.hostname = String.format("%s://%s", TCP_PROTO, UUID.randomUUID().toString());
		this.mySocket = mySocket;
		this.replyMapper = replyMapper;
	}

	protected final void forward(Frames frames) throws MessagingException {
		final FrameSink target = replyMapper.getForwardFor(this);
		if (target == null) {
			throw new MessagingException(String.format("Cannot find forward destination for sink %s", toString()));
		}
		target.receiveFromBuddy(frames);
	}

	protected final void send(Frames frames) throws MessagingException {
		final Iterator<byte[]> i = frames.iterator();
		// the send can usually happen from various threads
		synchronized (mySocket) {
			while (i.hasNext()) {
				final byte[] frame = i.next();
				mySocket.send(frame, (i.hasNext()) ? ZMQ.SNDMORE : NOFLAGS);
			}
		}
	}

	@Override
	public void receiveFromBuddy(Frames frames) throws MessagingException {
		send(frames);
	}

	@Override
	public void receiveFromWire(Frames frames) throws MessagingException {
		forward(frames);
	}

	public final Socket getSocket() {
		return mySocket;
	}

	public abstract int getPollType();
}
