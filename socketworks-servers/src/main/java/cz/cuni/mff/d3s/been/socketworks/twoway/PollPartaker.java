package cz.cuni.mff.d3s.been.socketworks.twoway;

import java.util.Iterator;
import java.util.UUID;

import cz.cuni.mff.d3s.been.cluster.Service;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.socketworks.Socketworks;
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

	final String hostname;
    Integer port;
	final Socket mySocket;
	final FrameForwardMapper replyMapper;

	protected PollPartaker(Socket mySocket, String hostname, FrameForwardMapper replyMapper) {
		this.hostname = hostname;
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

    public void start() {
        port = mySocket.bindToRandomPort(Socketworks.Protocol.TCP.bindAddr(hostname));
    }

    public void stop() {
        port = null;
        mySocket.close();
    }

    Socket getSocket() {
        return mySocket;
    }

    public Integer getPort() {
        return port;
    }

	public abstract int getPollType();
}
