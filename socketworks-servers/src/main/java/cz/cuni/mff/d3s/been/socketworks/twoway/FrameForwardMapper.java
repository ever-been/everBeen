package cz.cuni.mff.d3s.been.socketworks.twoway;

import cz.cuni.mff.d3s.been.mq.MessagingException;

/**
 * A mapper of frame-buffer forwards within the poll ring
 */
interface FrameForwardMapper {

	/**
	 * Get the forward mapping
	 *
	 * @param sink Who is forwarding
	 *
	 * @return To whom the frame-buffer should be forwarded
	 *
	 * @throws MessagingException When the forwarding breaks (no forward defined)
	 */
	public FrameSink getForwardFor(FrameSink sink) throws MessagingException;
}
