package cz.cuni.mff.d3s.been.web.services.websockets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.slf4j.Logger;

/**
 * User: donarus Date: 4/25/13 Time: 9:19 PM
 */
public abstract class WebSocket<T extends Event> {

	private final Logger logger;

	static List<Session> peers = Collections.synchronizedList(new ArrayList<Session>());

	public WebSocket(Logger logger) {
		this.logger = logger;
	}

	@OnOpen
	public final void onOpen(Session session) {
		peers.add(session);

		logger.debug(String.format("User connected to endpoint '{}'. Users on endpoint : {}", getEndpointName(), peers.size()));
	}

	/*	@OnMessage
		public final void handle(String message, Session peer) {
	       WE DO NOT EXPECT RECEIVING MESSAGES FROM SOCKETS FOR NOW
		}*/

	@OnClose
	public final void onClose(Session peer) {
		peers.remove(peer);
		logger.debug(String.format("User disconnected from endpoint '{}'. Users on endpoint : {}", getEndpointName(), peers.size()));
	}

	public void sendText(String text) {
		for (Session client : peers) {
			client.getAsyncRemote().sendText(text);
		}
	}

	public void sendBytes(ByteBuffer buffer) {
		for (Session client : peers) {
			client.getAsyncRemote().sendBinary(buffer);
		}
	}

	public void sendObject(Object object) {
		for (Session client : peers) {
			client.getAsyncRemote().sendObject(object);
		}
	}

	protected abstract String getEndpointName();

	/**
	 * Default behavior is to resend event object directly to socket (via
	 * {@link WebSocket#sendObject(Object)} method). Override to change default
	 * behavior.
	 * 
	 * @param event
	 */
	protected void onEvent(T event) {
		sendObject(event);
	}
}
