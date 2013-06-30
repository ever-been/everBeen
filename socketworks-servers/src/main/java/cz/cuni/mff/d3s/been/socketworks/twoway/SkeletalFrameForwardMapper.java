package cz.cuni.mff.d3s.been.socketworks.twoway;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.d3s.been.mq.MessagingException;

class SkeletalFrameForwardMapper implements FrameForwardMapper {

	private final Map<FrameSink, FrameSink> map = new HashMap<FrameSink, FrameSink>();

	public void addRoute(FrameSink from, FrameSink to) {
		map.put(from, to);
	}

	@Override
	public FrameSink getForwardFor(FrameSink sink) throws MessagingException {
		return map.get(sink);
	}
}