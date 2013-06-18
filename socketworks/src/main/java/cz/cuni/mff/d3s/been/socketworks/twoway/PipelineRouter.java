package cz.cuni.mff.d3s.been.socketworks.twoway;

import org.jeromq.ZMQ;
import org.jeromq.ZMQ.Socket;

class PipelineRouter extends PollPartaker {

	private PipelineRouter(Socket mySocket, FrameForwardMapper replyMapper) {
		super(mySocket, replyMapper);
	}

	public static PipelineRouter create(Socket mySocket, FrameForwardMapper replyMapper) {
		return new PipelineRouter(mySocket, replyMapper);
	}

	@Override
	public int getPollType() {
		return ZMQ.POLLIN;
	}

}
