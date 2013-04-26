package cz.cuni.mff.d3s.been.web.services.websockets;

import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;

/**
 * User: donarus Date: 4/25/13 Time: 9:18 PM
 */

@ServerEndpoint(value = OverviewLogSocket.ENDPOINT_NAME)
public class OverviewLogSocket extends WebSocket<LogEvent> {

	public static final String ENDPOINT_NAME = "overview-log";

	public OverviewLogSocket(Logger logger) {
		super(logger);
	}

	@Override
	protected final String getEndpointName() {
		return ENDPOINT_NAME;
	}

	/* we want default behavior (redirect LogEvent object
	directly to socket), so we don't need to re-implement
	onEvent(..) method */
}
