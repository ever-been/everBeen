package cz.cuni.mff.d3s.been.swrepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class HttpServer {

	private final String host;

	private final int port;

	private HttpRequestHandler requestHandler;

	private Connection connection;

	public HttpServer(final String host, final int port) {
		this.host = host;
		this.port = port;
	}

	public void setRequestHandler(HttpRequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}

	void handle(Request req, Response resp) {
		requestHandler.handle(req, resp);
	}

	void start() throws HttpServerException {
		Container container = new Container() {
			@Override
			public void handle(Request req, Response resp) {
				HttpServer.this.handle(req, resp);
			}
		};

		SocketAddress address = new InetSocketAddress(host, port);

		// FIXME review exception handling
		Server server = null;
		try {
			server = new ContainerServer(container);
		} catch (IOException e) {
			throw new HttpServerException("Creation of HTTP SERVER Server failed.", e);
		}

		try {
			connection = new SocketConnection(server);
		} catch (IOException e) {
			throw new HttpServerException("Creation of SOCKET CONNECTION for created http server failed.", e);
		}

		try {
			connection.connect(address);
		} catch (IOException e) {
			String inetAddr = host + ":" + port;
			throw new HttpServerException(String.format(
					"Connecting HTTP SERVER SOCKET CONNECTION to defined address '%s' failed.", inetAddr), e);
		}
	}

	void stop() throws HttpServerException {
		try {
			connection.close();
		} catch (IOException e) {
			throw new HttpServerException("HTTP SERVER SOCKET CONNECTION cannot be closed", e);
		}
	}

}
