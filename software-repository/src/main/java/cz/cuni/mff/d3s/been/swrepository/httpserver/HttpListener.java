package cz.cuni.mff.d3s.been.swrepository.httpserver;

import java.io.IOException;
import java.net.*;

import org.apache.http.HttpException;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HttpListener extends Thread {

	private static final Logger log = LoggerFactory.getLogger(HttpListener.class);

	private final HttpService service;
	private final HttpContext context;
    private final SocketAddress sockAddr;
	private final HttpParams params;
	private ServerSocket serverSocket;

	HttpListener(
			HttpService service,
			SocketAddress sockAddr,
			HttpParams params) {
		this.service = service;
        this.sockAddr = sockAddr;
		this.context = new BasicHttpContext();
		this.params = params;
	}

	public void bind() throws HttpServerException {
		try {
			serverSocket = new ServerSocket();//(port, HttpServer.MAX_CONNECTIONS, inetAddr);
            serverSocket.bind(sockAddr);
		} catch (IOException e) {
			throw new HttpServerException(String.format(
					"Failed to bind HTTP listener thread on %s.", sockAddr.toString()), e);
		}

		log.info(String.format(
				"Listener thread bound to socket %s",
				serverSocket.toString()));

	}

	@Override
	public void run() {
		super.run();

		Socket socket;
		while (!Thread.interrupted()) {
			log.debug("Listener thread waiting for connection...");
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				log.error(String.format(
						"Failed to accept incoming socket connection - %s",
						e.getMessage()));
				continue;
			}

			DefaultHttpServerConnection connection = new DefaultHttpServerConnection();
			try {
				connection.bind(socket, params);
			} catch (IOException e) {
				log.error(String.format(
						"Failed to bind incoming connection %s - %s",
						connection.toString(),
						e.getMessage()));
				continue;
			}

			try {
				service.handleRequest(connection, context);
			} catch (HttpException e) {
				log.error(
						"Could not process incoming connection %s - %s",
						connection.toString(),
						e.getMessage());
				continue;
			} catch (IOException e) {
				log.error(String.format(
						"I/O error when processing incoming connection %s - %s",
						connection.toString(),
						e.getMessage()));
				continue;
			}
			try {
				connection.close();
			} catch (IOException e) {
				log.error(String.format(
						"Leaked connection %s when attempting release after handling request",
						connection.toString()));
			}
			log.debug("Request processed.");
		}

		try {
			serverSocket.close();
			log.info(String.format(
					"Socket %s released, listener is down.",
					serverSocket.toString()));
		} catch (IOException e) {
			log.error(String.format("Failed to close listener socket - %s"));
		}
	}
}