package cz.cuni.mff.d3s.been.swrepository.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import org.apache.http.HttpException;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of listener thread for http sockets used by {@link HttpServer}
 * .
 */
class HttpListener extends Thread {

	private static final int SOCKET_SO_TIMEOUT_MILLIS = 300;

	private static final Logger log = LoggerFactory.getLogger(HttpListener.class);

	private final HttpService service;
	private final HttpContext context;
	private final SocketAddress sockAddr;
	private final HttpParams params;
	private ServerSocket serverSocket;

	/**
	 * Creates new http socket listener for given service on given address with
	 * given params.
	 * 
	 * @param service
	 *          service on which this listener will invoke request handling on
	 *          socket receive
	 * @param sockAddr
	 *          address on which the listener will be listening
	 * @param params
	 *          parameters for connection which will be created on given socket
	 */
	HttpListener(HttpService service, SocketAddress sockAddr, HttpParams params) {
		this.service = service;
		this.sockAddr = sockAddr;
		this.context = new BasicHttpContext();
		this.params = params;
	}

	/**
	 * Creates new socket from socket address given in constructor and binds this
	 * listener to it.
	 * 
	 * @throws HttpServerException
	 *           when socket cannot be created from some reason
	 */
	public void bind() throws HttpServerException {
		try {
			serverSocket = new ServerSocket();
			serverSocket.setSoTimeout(SOCKET_SO_TIMEOUT_MILLIS);
			serverSocket.bind(sockAddr);
		} catch (IOException e) {
			throw new HttpServerException(String.format("Failed to bind HTTP listener thread on %s.", sockAddr.toString()), e);
		}

		log.info(String.format("Listener thread bound to socket %s", serverSocket.toString()));

	}

	@Override
	public void run() {
		setName(getClass().getSimpleName());

		super.run();

		while (!Thread.interrupted()) {
			Socket socket = acceptSocket();
			if (socket == null) {
				continue;
			}

			log.debug("Listener thread accepted connection...");

			DefaultHttpServerConnection connection = createConnection(socket);
			if (connection == null) {
				continue;
			}

			handleRequest(connection);

			closeConnection(connection);

			log.debug("Request processed.");
		}

		try {
			serverSocket.close();
			log.info("Socket {} released.", serverSocket.toString());
		} catch (IOException e) {
			log.error("Failed to close listener socket - {}", e.getMessage());
		} finally {
			log.debug("Listener {} is down.", getName());
		}
	}

	private Socket acceptSocket() {
		try {
			return serverSocket.accept();
		} catch (SocketTimeoutException e) {
			return null;
		} catch (IOException e) {
			log.error("Failed to accept incoming socket connection - {}", e.getMessage());
		}
		return null;
	}

	private DefaultHttpServerConnection createConnection(Socket socket) {
		DefaultHttpServerConnection connection = new DefaultHttpServerConnection();
		try {
			connection.bind(socket, params);
		} catch (IOException e) {
			log.error("Failed to bind incoming connection {} - {}", connection.toString(), e.getMessage());
			return null;
		}
		return connection;
	}

	private void closeConnection(DefaultHttpServerConnection connection) {
		try {
			connection.close();
		} catch (IOException e) {
			log.error("Leaked connection {} when attempting release after handling request", connection.toString());
		}
	}

	private void handleRequest(DefaultHttpServerConnection connection) {
		try {
			service.handleRequest(connection, context);
		} catch (HttpException e) {
			log.error("Could not process incoming connection {} - {}", connection.toString(), e.getMessage());
		} catch (IOException e) {
			log.error("I/O error when processing incoming connection {} - {}", connection.toString(), e.getMessage());
		}
	}
}
