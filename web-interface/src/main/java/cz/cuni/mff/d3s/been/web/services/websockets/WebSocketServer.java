package cz.cuni.mff.d3s.been.web.services.websockets;

import java.util.*;

import javax.websocket.DeploymentException;

import org.apache.tapestry5.ioc.ServiceResources;
import org.glassfish.tyrus.server.Server;
import org.slf4j.Logger;

/**
 * User: donarus Date: 4/22/13 Time: 4:37 PM
 */
public class WebSocketServer {

	private Server server;

	private Map<Class<Event>, List<WebSocket>> socketInstantions;

	private final ServiceResources serviceResources;

	private final Logger logger;

	public WebSocketServer(final ServiceResources serviceResources, final Logger logger) {
		this.serviceResources = serviceResources;
		this.logger = logger;

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (server != null) {
					server.stop();
				}
			}
		});
	}

	public final void start(Map<Class<Event>, Set<Class<WebSocket>>> sockets) {
		this.socketInstantions = new HashMap<>();
		Set _sockets = new HashSet();
		for (Map.Entry<Class<Event>, Set<Class<WebSocket>>> entry : sockets.entrySet()) {
			Class<Event> eventClass = entry.getKey();
			Set<Class<WebSocket>> webSocketClasses = entry.getValue();

			// we have to get all WebSocket classes for binding in WebSocket Server
			_sockets.addAll((Set) webSocketClasses);

			// we want to get all WebSocket instances for resolving events
			for (Class<WebSocket> serviceClass : webSocketClasses) {
				if (!socketInstantions.containsKey(eventClass)) {
					socketInstantions.put(eventClass, new ArrayList<WebSocket>());
				}
				socketInstantions.get(eventClass).add(serviceResources.getService(OverviewLogSocket.class));
			}

		}

		server = new Server("127.0.0.1", 8025, "/", (Set) sockets.entrySet());
		try {
			server.start();
		} catch (DeploymentException e) {
			logger.error("Can't start WebSocket server.", e);
		}
	}

	public void newEvent(Event event) {
		List<WebSocket> receivers = socketInstantions.get(event.getClass());
		if (receivers == null) {
			return;
		}

		for (WebSocket socket : receivers) {
			socket.onEvent(event);
		}

	}
}
