package cz.cuni.mff.d3s.been.socketworks.twoway;

import org.jeromq.ZMQ;
import org.jeromq.ZMQ.Socket;

class PipelineRouter extends PollPartaker {

	private PipelineRouter(Socket mySocket, String hostname, FrameForwardMapper replyMapper) {
		super(mySocket, hostname, replyMapper);
	}

	public static PipelineRouter create(Socket mySocket, String hostname, FrameForwardMapper replyMapper) {
		return new PipelineRouter(mySocket, hostname, replyMapper);
	}

	@Override
	public int getPollType() {
		return ZMQ.POLLIN;
	}

}
