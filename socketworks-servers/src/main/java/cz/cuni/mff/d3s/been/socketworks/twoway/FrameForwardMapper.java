package cz.cuni.mff.d3s.been.socketworks.twoway;

import cz.cuni.mff.d3s.been.mq.MessagingException;

public interface FrameForwardMapper {
	public FrameSink getForwardFor(FrameSink sink) throws MessagingException;
}
